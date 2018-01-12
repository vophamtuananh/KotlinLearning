package com.vophamtuananh.base.imageloader

import java.util.concurrent.ConcurrentHashMap

/**
 * Created by vophamtuananh on 1/12/18.
 */
class FileSynchronizer {

    companion object {
        private val mProcessingFiles = ConcurrentHashMap<String, Any>()
    }

    fun registerProcess(fileName: String) {
        synchronized(this) {
            mProcessingFiles.put(fileName, Any())
        }
    }

    fun unRegisterProcess(fileName: String) {
        synchronized(this) {
            if (mProcessingFiles.containsKey(fileName)) {
                mProcessingFiles.remove(fileName)
            }
        }
    }

    fun isProcessing(fileName: String): Boolean {
        synchronized(this) {
            return mProcessingFiles.containsKey(fileName)
        }
    }

}