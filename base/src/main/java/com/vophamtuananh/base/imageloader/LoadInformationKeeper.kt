package com.vophamtuananh.base.imageloader

import android.graphics.Bitmap
import com.vophamtuananh.base.R
import java.lang.ref.WeakReference

/**
 * Created by vophamtuananh on 1/12/18.
 */
class LoadInformationKeeper {

    internal var url: String? = null
    internal var placeHolderId = R.color.colorLightGrey
    internal var errorHolderId = R.color.colorLightGrey
    internal var imageViewWeakReference: WeakReference<ILoadingImageView>? = null
    internal var width: Int = 0
    internal var height: Int = 0
    internal var config: Bitmap.Config = Bitmap.Config.RGB_565
    internal var scaleType = ScaleType.NOT_SCALE
    internal var callback: BitmapCallback? = null

    internal fun getLoadingImageView(): ILoadingImageView? {
        return if (imageViewWeakReference == null) null else imageViewWeakReference!!.get()
    }

    class Builder {
        private var loadInformationKeeper: LoadInformationKeeper = LoadInformationKeeper()

        internal fun url(url: String) {
            loadInformationKeeper.url = url
        }

        internal fun placeHolderId(placeHolderId: Int) {
            loadInformationKeeper.placeHolderId = placeHolderId
        }

        internal fun errorHolderId(errorHolderId: Int) {
            loadInformationKeeper.errorHolderId = errorHolderId
        }

        internal fun width(width: Int) {
            loadInformationKeeper.width = width
        }

        internal fun height(height: Int) {
            loadInformationKeeper.height = height
        }

        internal fun config(config: Bitmap.Config) {
            loadInformationKeeper.config = config
        }

        internal fun scaleType(scaleType: ScaleType) {
            loadInformationKeeper.scaleType = scaleType
        }

        internal fun callBack(callback: BitmapCallback) {
            loadInformationKeeper.callback = callback
        }

        internal fun into(imageView: ILoadingImageView) {
            loadInformationKeeper.imageViewWeakReference = WeakReference(imageView)
        }


        internal fun build(): LoadInformationKeeper {
            return loadInformationKeeper
        }
    }


    enum class ScaleType {
        NOT_SCALE, CROP, CENTER_INSIDE, SCALE_FULL_WIDTH, SCALE_FULL_HEIGHT
    }
}