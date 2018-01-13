package com.vophamtuananh.base.imageloader

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Handler
import android.util.LruCache
import java.lang.ref.WeakReference
import java.util.*
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

/**
 * Created by vophamtuananh on 1/12/18.
 */
open class ImageLoader(context: Context) {

    companion object {
        private const val CORE_POOL_SIZE = 5

        private const val MAXIMUM_POOL_SIZE = 128

        private const val KEEP_ALIVE_TIME = 1

        private val mFileSynchronizer = FileSynchronizer()

        private val mWaitingKeepers = ArrayList<LoadInformationKeeper>()

        private val imageViews = Collections.synchronizedMap(WeakHashMap<ILoadingImageView, String>())

        private val handler = Handler()

        private var memoryCache: LruCache<String, BitmapDrawable>? = null

        private var executorService: ThreadPoolExecutor? = null
    }

    private val mContextWeakReference: WeakReference<Context>?

    private var builder: LoadInformationKeeper.Builder? = null

    init {
        val workQueue = object : LinkedBlockingQueue<Runnable>() {}

        executorService = ThreadPoolExecutor(
                CORE_POOL_SIZE,
                MAXIMUM_POOL_SIZE,
                KEEP_ALIVE_TIME.toLong(),
                TimeUnit.SECONDS,
                workQueue)

        memoryCache = object : LruCache<String, BitmapDrawable>((Runtime.getRuntime().maxMemory() / 8).toInt()) {
            override fun sizeOf(url: String, drawable: BitmapDrawable): Int {
                val bitmapSize = getBitmapSize(drawable) / 1024
                return if (bitmapSize == 0) 1 else bitmapSize
            }
        }

        mContextWeakReference = WeakReference(context)
    }

    private fun show(loadInformationKeeper: LoadInformationKeeper): Boolean {
        val loadingImageView = loadInformationKeeper.getLoadingImageView()
        if (loadingImageView != null) {
            if (loadInformationKeeper.url == null) {
                loadingImageView.setResourceId(loadInformationKeeper.errorHolderId)
                return true
            }

            imageViews.put(loadingImageView, loadInformationKeeper.url)

            val fileName = loadInformationKeeper.url!!.hashCode().toString()
            if (mFileSynchronizer.isProcessing(fileName)) {
                if (!mWaitingKeepers.contains(loadInformationKeeper))
                    mWaitingKeepers.add(loadInformationKeeper)
                return false
            }

            if (imageViewReused(loadInformationKeeper))
                return true

            val bitmapDrawable = memoryCache!!.get(loadInformationKeeper.url + "-" + loadInformationKeeper.width + "-" + loadInformationKeeper.height)
            if (bitmapDrawable != null) {
                loadingImageView.setDrawable(bitmapDrawable)
                loadingImageView.hideLoading()
            } else {
                mFileSynchronizer.registerProcess(fileName)
                queueDownloadPhoto(loadInformationKeeper)
                loadingImageView.showLoading()
            }
        }
        return true
    }

    private fun queueDownloadPhoto(informationKeeper: LoadInformationKeeper) {
        val c = mContextWeakReference!!.get()
        if (c == null) {
            val fileName = informationKeeper.url!!.hashCode().toString()
            mFileSynchronizer.unRegisterProcess(fileName)
            notifyWaitingKeepers()
            return
        }

        executorService!!.submit(LoadImageRunnable(c, informationKeeper, this::loadCompletedCallback))
    }

    private fun loadCompletedCallback(loadInformationKeeper: LoadInformationKeeper, bitmap: Bitmap?) {
        val context = mContextWeakReference!!.get()
        if (context == null) {
            val fileName = loadInformationKeeper.url!!.hashCode().toString()
            mFileSynchronizer.unRegisterProcess(fileName)
            handler.post { notifyWaitingKeepers(); }
            return
        }
        preDisplaying(context, bitmap, loadInformationKeeper)
    }

    protected fun reprocessBitmap(bitmap: Bitmap): Bitmap {
        return bitmap
    }

    private fun notifyWaitingKeepers() {
        if (mContextWeakReference?.get() == null) {
            mWaitingKeepers.clear()
            return
        }

        val iter = mWaitingKeepers.iterator()
        while (iter.hasNext()) {
            val loadInformationKeeper = iter.next()
            if (show(loadInformationKeeper))
                iter.remove()
        }
    }

    private fun preDisplaying(context: Context, bitmap: Bitmap?, loadInformationKeeper: LoadInformationKeeper) {
        var bitmapDrawable: BitmapDrawable? = null
        if (bitmap != null) {
            bitmapDrawable = BitmapDrawable(context.resources, reprocessBitmap(bitmap))
            try {
                memoryCache!!.put(loadInformationKeeper.url + "-" + loadInformationKeeper.width + "-" + loadInformationKeeper.height, bitmapDrawable)
            } catch (e: OutOfMemoryError) {
                memoryCache!!.evictAll()
            }

        }
        val fileName = loadInformationKeeper.url!!.hashCode().toString()
        mFileSynchronizer.unRegisterProcess(fileName)
        if (imageViewReused(loadInformationKeeper)) {
            handler.post { this.notifyWaitingKeepers() }
            return
        }
        displayBitmap(bitmapDrawable, loadInformationKeeper)
    }

    private fun displayBitmap(bitmapDrawable: BitmapDrawable?, loadInformationKeeper: LoadInformationKeeper) {
        handler.post {
            val bitmapCallback = loadInformationKeeper.callback
            if (bitmapCallback != null) {
                bitmapCallback.onCallBack(bitmapDrawable!!)
            } else {
                setImageDrawable(loadInformationKeeper, bitmapDrawable)
            }
            notifyWaitingKeepers()
        }
    }

    private fun setImageDrawable(loadInformationKeeper: LoadInformationKeeper, drawable: Drawable?) {
        val loadingImageView = loadInformationKeeper.getLoadingImageView() ?: return

        if (drawable != null) {
            loadingImageView.setDrawable(drawable)
        } else {
            loadingImageView.setResourceId(loadInformationKeeper.errorHolderId)
        }

        loadingImageView.hideLoading()
    }

    private fun imageViewReused(loadInformationKeeper: LoadInformationKeeper): Boolean {
        val loadingImageView = loadInformationKeeper.getLoadingImageView()
        if (loadingImageView != null) {
            val tag = imageViews[loadingImageView]
            return tag == null || tag != loadInformationKeeper.url
        }
        return true
    }

    private fun getBitmapSize(value: BitmapDrawable): Int {
        return value.bitmap.allocationByteCount

    }

    fun load(url: String): ImageLoader {
        builder = LoadInformationKeeper.Builder()
        builder!!.url(url)
        return this
    }

    fun placeHolderId(placeHolderId: Int): ImageLoader {
        builder!!.placeHolderId(placeHolderId)
        return this
    }

    fun errorHolderId(errorHolderId: Int): ImageLoader {
        builder!!.errorHolderId(errorHolderId)
        return this
    }

    fun width(showWidth: Int): ImageLoader {
        builder!!.width(showWidth)
        return this
    }

    fun height(showHeight: Int): ImageLoader {
        builder!!.height(showHeight)
        return this
    }

    fun config(config: Bitmap.Config): ImageLoader {
        builder!!.config(config)
        return this
    }

    fun callBack(callback: BitmapCallback): ImageLoader {
        builder!!.callBack(callback)
        return this
    }

    fun scaleType(scaleType: LoadInformationKeeper.ScaleType): ImageLoader {
        builder!!.scaleType(scaleType)
        return this
    }

    fun into(ILoadingImageView: ILoadingImageView) {
        builder!!.into(ILoadingImageView)
        show(builder!!.build())
    }
}