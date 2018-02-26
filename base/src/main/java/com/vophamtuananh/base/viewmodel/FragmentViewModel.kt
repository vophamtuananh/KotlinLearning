package com.vophamtuananh.base.viewmodel

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.OnLifecycleEvent
import android.arch.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import java.lang.ref.WeakReference

/**
 * Created by vophamtuananh on 1/7/18.
 */
open class FragmentViewModel : ViewModel(), LifecycleObserver {

    @Volatile private var mViewWeakReference: WeakReference<CommonView>? = null

    private var compositeDisposables: CompositeDisposable? = null

    protected fun view(): CommonView? {
        return mViewWeakReference?.get()
    }

    open fun onAttach(view: CommonView) {
        mViewWeakReference = WeakReference(view)
        view.lifecycle.addObserver(this)
    }

    open fun onInitialized() {}

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    open protected fun onCreated() {
        if (compositeDisposables == null)
            compositeDisposables = CompositeDisposable()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    open protected fun onStart() {}

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    open protected fun onResume() {}

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    open protected fun onPause() {}

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    open protected fun onStop() {}

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    open protected fun onDestroy() {
        val view = mViewWeakReference?.get()
        view?.lifecycle?.removeObserver(this)
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposables?.dispose()
    }

    protected fun addDisposable(disposable: Disposable) {
        compositeDisposables?.add(disposable)
    }
}