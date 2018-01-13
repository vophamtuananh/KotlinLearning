package com.vophamtuananh.base.injection.module

import android.content.Context
import com.vophamtuananh.base.imageloader.ImageLoader
import com.vophamtuananh.base.injection.scope.ApplicationScope
import dagger.Module
import dagger.Provides

/**
 * Created by vophamtuananh on 1/13/18.
 */

@Module(includes = [AppContextModule::class])
class ImageLoaderModule {

    @Provides
    @ApplicationScope
    fun provideImageLoader(context: Context): ImageLoader {
        return ImageLoader(context)
    }
}