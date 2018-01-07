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
open class ActivityViewModel<V : CommonView> : ViewModel(), LifecycleObserver {

    @Volatile private var mViewWeakReference: WeakReference<V>? = null

    private var compositeDisposables: CompositeDisposable? = null

    protected fun view(): V? {
        if (mViewWeakReference == null) return null
        return mViewWeakReference!!.get()
    }

    fun onCreated(view: V) {
        mViewWeakReference = WeakReference(view)
        if (compositeDisposables == null)
            compositeDisposables = CompositeDisposable()
        view.lifecycle.addObserver(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun resume() {
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun pause() {
        if (compositeDisposables != null)
            compositeDisposables!!.clear()
    }


    fun onRestoreInstanceState(savedInstanceState: Bundle) {

    }

    fun onSaveInstanceState(outState: Bundle) {

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun cleanup() {
        if (mViewWeakReference != null) {
            val view = mViewWeakReference!!.get()
            view?.lifecycle?.removeObserver(this)
        }
    }

    override fun onCleared() {
        super.onCleared()
        if (compositeDisposables != null)
            compositeDisposables!!.dispose()
    }

    fun addDisposable(disposable: Disposable) {
        compositeDisposables!!.add(disposable)
    }
}