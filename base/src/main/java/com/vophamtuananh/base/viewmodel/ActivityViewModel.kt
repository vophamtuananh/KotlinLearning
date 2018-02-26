package com.vophamtuananh.base.viewmodel

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.OnLifecycleEvent
import android.arch.lifecycle.ViewModel
import android.os.Bundle
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import java.lang.ref.WeakReference

/**
 * Created by vophamtuananh on 1/7/18.
 */
open class ActivityViewModel : ViewModel(), LifecycleObserver {

    @Volatile private var mViewWeakReference: WeakReference<CommonView>? = null

    private var compositeDisposables: CompositeDisposable? = null

    protected fun view(): CommonView? {
        return mViewWeakReference?.get()
    }

    fun onAttached(view: CommonView) {
        mViewWeakReference = WeakReference(view)
        view.lifecycle.addObserver(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreated() {
        if (compositeDisposables == null)
            compositeDisposables = CompositeDisposable()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun resume() {
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun pause() {
    }


    fun onRestoreInstanceState(savedInstanceState: Bundle) {

    }

    fun onSaveInstanceState(outState: Bundle) {

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun cleanup() {
        val view = mViewWeakReference?.get()
        view?.lifecycle?.removeObserver(this)
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposables?.dispose()
    }

    fun addDisposable(disposable: Disposable) {
        compositeDisposables?.add(disposable)
    }
}