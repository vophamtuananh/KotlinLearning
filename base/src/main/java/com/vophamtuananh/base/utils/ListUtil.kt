package com.vophamtuananh.base.utils

import java.util.ArrayList

/**
 * Created by vophamtuananh on 1/12/18.
 */
class ListUtil {

    companion object {
        fun <T> union(list1: List<T>, list2: List<T>): List<T> {
            return object : ArrayList<T>() {
                init {
                    addAll(list1)
                    addAll(list2)
                }
            }
        }

        fun <T> isNotEmpty(list: List<T>?): Boolean {
            return list != null && !list.isEmpty()
        }

        fun <T> isEmpty(list: List<T>?): Boolean {
            return list == null || list.isEmpty()
        }
    }

}