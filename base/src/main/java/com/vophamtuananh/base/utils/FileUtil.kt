package com.vophamtuananh.base.utils

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import java.io.File
import java.io.IOException

/**
 * Created by vophamtuananh on 1/7/18.
 */
class FileUtil {

    companion object {
        private val PROCESSING_DIR_NAME = "processing"
        private val CAPTURED_FILE_NAME = "captured_file"

        fun getDiskCacheDir(context: Context, uniqueName: String): File {
            val cachePath = if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState())
                getExternalCacheDir(context)!!.path
            else
                context.cacheDir.path

            return File(cachePath + File.separator + uniqueName)
        }

        private fun getExternalCacheDir(context: Context): File? {
            return context.externalCacheDir
        }

        fun getOutputMediaFile(context: Context): File? {
            val mediaStorageDir = FileUtil.getDiskCacheDir(context, PROCESSING_DIR_NAME)
            if (!mediaStorageDir.exists()) {
                if (!mediaStorageDir.mkdirs()) {
                    return null
                }
            }
            try {
                return File.createTempFile(CAPTURED_FILE_NAME, ".jpg", mediaStorageDir)
            } catch (e: IOException) {
                e.printStackTrace()
            }

            return null
        }

        fun getRealPathFromURI(context: Context, contentUri: Uri): String? {
            var result: String? = null
            try {
                val proj = arrayOf(MediaStore.Images.Media.DATA)
                val cr = context.contentResolver
                val cursor = cr.query(contentUri, proj, null, null, null)
                if (cursor != null && !cursor.isClosed) {
                    val columnIndex = cursor
                            .getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                    cursor.moveToFirst()
                    result = cursor.getString(columnIndex)
                    cursor.close()
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }

            return result
        }
    }

}