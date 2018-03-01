package com.vophamtuananh.base.utils

import android.app.Activity
import android.content.Intent

/**
 * Created by vophamtuananh on 12/24/17.
 */

inline fun <reified T> Activity.start(clearBackStack: Boolean = false) {
    val intent = Intent(this, T::class.java)
    if (clearBackStack)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    startActivity(intent)
}