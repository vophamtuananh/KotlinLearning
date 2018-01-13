package com.vophamtuananh.base.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.provider.MediaStore
import android.support.media.ExifInterface
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException

/**
 * Created by vophamtuananh on 1/12/18.
 */
class BitmapUtil {

    companion object {

        fun saveBitmapToFile(bitmap: Bitmap?, file: File?, compressFormat: Bitmap.CompressFormat = Bitmap.CompressFormat.JPEG) {
            if (file == null)
                return
            file.deleteOnExit()

            var fos: FileOutputStream? = null
            try {
                fos = FileOutputStream(file)
                bitmap!!.compress(compressFormat, 100, fos)
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            } finally {
                if (fos != null) {
                    try {
                        fos.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }

                }
                bitmap?.recycle()
            }
        }

        fun getBitmapFromCachedFile(file: File, bitmapConfig: Bitmap.Config = Bitmap.Config.RGB_565): Bitmap {
            val opts = BitmapFactory.Options()
            opts.inPreferredConfig = bitmapConfig
            return BitmapFactory.decodeFile(file.absolutePath, opts)
        }

        fun getBitmapFromFile(file: File, bitmapConfig: Bitmap.Config): Bitmap? {
            val opts = BitmapFactory.Options()
            opts.inPreferredConfig = bitmapConfig
            val bitmap: Bitmap? = BitmapFactory.decodeFile(file.absolutePath, opts) ?: return null
            return rotationBitmap(file.absolutePath, bitmap!!)
        }

        private fun rotationBitmap(filePath: String, bm: Bitmap): Bitmap {
            var exif: ExifInterface? = null
            try {
                exif = ExifInterface(filePath)
            } catch (e: IOException) {
                e.printStackTrace()
            }

            var orientString: String? = null
            if (exif != null)
                orientString = exif.getAttribute(ExifInterface.TAG_ORIENTATION)

            val orientation = if (orientString != null) Integer.parseInt(orientString) else ExifInterface.ORIENTATION_NORMAL

            if (orientation != ExifInterface.ORIENTATION_NORMAL) {
                val rotationAngle = when(orientation) {
                    ExifInterface.ORIENTATION_ROTATE_90     -> 90
                    ExifInterface.ORIENTATION_ROTATE_180    -> 180
                    ExifInterface.ORIENTATION_ROTATE_270    -> 270
                    else                                    -> 0
                }

                if (rotationAngle == 0)
                    return bm

                return rotationBitmap(bm, rotationAngle)
            }
            return bm
        }

        private fun rotationBitmap(bitmap: Bitmap, orientation: Int): Bitmap {
            val matrix = Matrix()
            matrix.setRotate(orientation.toFloat(), bitmap.width.toFloat() / 2, bitmap.height.toFloat() / 2)
            return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        }

        fun decodeBitmapFromUri(context: Context, uri: Uri,
                                reqWidth: Int = 0, reqHeight: Int = 0,
                                bitmapConfig: Bitmap.Config = Bitmap.Config.RGB_565): Bitmap? {
            var bitmap: Bitmap? = null
            val path: String?
            try {
                path = FileUtil.getRealPathFromURI(context, uri)

                if (path != null && !path.isEmpty()) {
                    bitmap = decodeBitmapFromExitPath(path, reqWidth, reqHeight, bitmapConfig)
                } else {
                    bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
                    bitmap = scaleBitmap(bitmap, reqWidth, reqHeight)
                }
                if (bitmap == null)
                    return null
                return rotationBitmap(path!!, bitmap)
            } catch (ex: Exception) {
                ex.printStackTrace()
            }

            return bitmap
        }

        private fun decodeBitmapFromExitPath(path: String, reqWidth: Int = 0, reqHeight: Int = 0, bitmapConfig: Bitmap.Config = Bitmap.Config.RGB_565): Bitmap? {
            val opts = BitmapFactory.Options()
            opts.inJustDecodeBounds = true
            opts.inPreferredConfig = bitmapConfig
            BitmapFactory.decodeFile(path, opts)
            opts.inSampleSize = calculateInSampleSize(opts, reqWidth, reqHeight)
            opts.inJustDecodeBounds = false
            return BitmapFactory.decodeFile(path, opts)
        }

        private fun scaleBitmap(bitmap: Bitmap, width: Int, height: Int): Bitmap {
            var widthTmp = bitmap.width
            var heightTmp = bitmap.height
            if (width != 0 && height != 0) {

                while (true) {
                    if (widthTmp < width || heightTmp < height)
                        break
                    widthTmp /= 2
                    heightTmp /= 2
                }
            }
            return Bitmap.createScaledBitmap(bitmap, widthTmp, heightTmp, false)
        }

        private fun calculateInSampleSize(option: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {

            val width = option.outWidth
            val height = option.outHeight

            if (height == 0 || width == 0) {
                return 1
            }

            val stretchWidth = Math.round(width.toFloat() / reqWidth.toFloat())
            val stretchHeight = Math.round(height.toFloat() / reqHeight.toFloat())

            return if (stretchWidth <= stretchHeight)
                stretchHeight
            else
                stretchWidth
        }

    }
}