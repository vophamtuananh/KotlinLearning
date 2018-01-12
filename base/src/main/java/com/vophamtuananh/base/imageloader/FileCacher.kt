package com.vophamtuananh.base.imageloader

import android.content.Context
import com.vophamtuananh.base.utils.FileUtil
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

/**
 * Created by vophamtuananh on 1/12/18.
 */
class FileCacher(context: Context) {

    companion object {
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

    internal fun getFile(url: String): File {
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

        if (!file.exists() || file.length() < fileSize) {
            if (file.exists())
                file.delete()
            try {
                var writtenByte: Long = 0
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
            }
        }
    }

    @Synchronized private fun cleanDir(dir: File, bytes: Long) {
        var bytesDeleted: Long = 0
        val files = dir.listFiles()
        if (files != null) {
            for (file in files) {
                bytesDeleted += file.length()
                file.delete()

                if (bytesDeleted >= bytes) {
                    break
                }
            }
        }
        size -= bytesDeleted
    }

    private fun getDirSize(dir: File): Long {
        var size: Long = 0
        val files = dir.listFiles()
        files?.filter(predicate = {
            it.isFile
        })
        files?.filter { it.isFile }?.forEach { size += it.length() }
        return size
    }
}