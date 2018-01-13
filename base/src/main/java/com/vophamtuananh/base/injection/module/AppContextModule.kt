package com.vophamtuananh.base.injection.module

import android.content.Context
import dagger.Module
import dagger.Provides

/**
 * Created by vophamtuananh on 1/13/18.
 */

@Module
class AppContextModule(private val mContext: Context) {

    @Provides
    fun provideContext() : Context {
        return mContext
    }
}