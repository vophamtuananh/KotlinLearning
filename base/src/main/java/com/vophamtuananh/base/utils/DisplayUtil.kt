package com.vophamtuananh.base.utils

import android.content.Context

/**
 * Created by vophamtuananh on 12/24/17.
 */
class DisplayUtil {

    companion object {
        private var deviceWidth = 0
        private var deviceHeight = 0

        fun getDeviceWidth(context: Context): Int {
            if (deviceWidth == 0)
                deviceWidth = context.resources.displayMetrics.widthPixels
            return deviceWidth
        }

        fun getDeviceHeight(context: Context): Int {
            if (deviceHeight == 0)
                deviceHeight = context.resources.displayMetrics.heightPixels
            return deviceHeight
        }
    }
}