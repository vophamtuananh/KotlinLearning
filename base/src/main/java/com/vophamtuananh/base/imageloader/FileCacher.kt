package com.vophamtuananh.base.imageloader

import android.content.Context
import android.graphics.Bitmap
import com.vophamtuananh.base.utils.BitmapUtil
import com.vophamtuananh.base.utils.FileUtil
import java.io.*

/**
 * Created by vophamtuananh on 1/12/18.
 */
class FileCacher(context: Context) {

    companion object {
        private val MAX_CACHE_SIZE = 1500

        private const val IMAGE_CACHE_DIR_NAME = "cached_image"

        private const val MAX_SIZE = 209715200

        private val syncObject = Object()

        private var size: Long = -1

        private var cacheDir: File? = null
    }

    init {
        if (cacheDir == null) {
            synchronized(syncObject) {
                if (cacheDir == null) {
                    cacheDir = FileUtil.getDiskCacheDir(context, IMAGE_CACHE_DIR_NAME)
                    if (!cacheDir!!.exists())
                        cacheDir!!.mkdirs()
                    size = getDirSize(cacheDir!!)
                }
            }
        }
    }

    internal fun getFile(url: String): File? {
        val filename = url.hashCode().toString()

        return File(cacheDir, filename)
    }

    internal fun saveFile(inputStream: InputStream, file: File, fileSize: Long) {
        val newSize = fileSize + size
        if (newSize > MAX_SIZE) {
            synchronized(syncObject) {
                cleanDir(cacheDir!!, newSize - MAX_SIZE)
            }
        }

        val bufferSize = 1024
        var os: FileOutputStream? = null

        var writtenByte: Long = 0
        if (!file.exists() || file.length() < fileSize) {
            if (file.exists())
                file.delete()
            try {
                os = FileOutputStream(file)
                val bytes = ByteArray(bufferSize)
                while (true) {
                    val count = inputStream.read(bytes, 0, bufferSize)
                    if (count == -1)
                        break
                    os.write(bytes, 0, count)
                    writtenByte += count.toLong()
                }
                size += fileSize
            } catch (ex: Exception) {
                if (file.exists())
                    file.delete()
            } finally {
                if (os != null) {
                    try {
                        os.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }

                }
                if (file.exists()) {
                    if (file.length() == 0L || writtenByte < fileSize) {
                        file.delete()
                    } else {
                        compressImageFile(file, MAX_CACHE_SIZE, MAX_CACHE_SIZE)
                    }
                }
            }
        }
    }

    private fun compressImageFile(imageFile: File, reqWidth: Int, reqHeight: Int) {
        val destinationPath = imageFile.absolutePath
        var fileOutputStream: FileOutputStream? = null
        try {
            val bitmap = BitmapUtil.decodeBitmapFromExitFile(imageFile, reqWidth, reqHeight)
            imageFile.delete()
            fileOutputStream = FileOutputStream(destinationPath)
            bitmap?.compress(Bitmap.CompressFormat.WEBP, 85, fileOutputStream)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.flush()
                    fileOutputStream.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }
        }
    }

    @Synchronized private fun cleanDir(dir: File, bytes: Long) {
        var bytesDeleted: Long = 0
        val files = dir.listFiles()
        files?.forEach {
            bytesDeleted += it.length()
            it.delete()
            if (bytesDeleted >= bytes) return
        }
        size -= bytesDeleted
    }

    private fun getDirSize(dir: File): Long {
        var size: Long = 0
        val files = dir.listFiles()
        files?.filter { it.isFile }?.forEach { size += it.length() }
        return size
    }
}