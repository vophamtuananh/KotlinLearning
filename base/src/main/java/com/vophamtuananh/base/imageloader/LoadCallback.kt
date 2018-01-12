package com.vophamtuananh.base.imageloader

import android.graphics.Bitmap

/**
 * Created by vophamtuananh on 1/12/18.
 */
interface LoadCallback {

    fun completed(loadInformationKeeper: LoadInformationKeeper, bitmap: Bitmap?)

}