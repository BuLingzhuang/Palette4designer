package com.bulingzhuang.palette4designer.utils

import android.content.Context

/**
 * Created by bulingzhuang
 * on 2017/9/5
 * E-mail:bulingzhuang@foxmail.com
 */
object Tools {

    /**
     * dp转px
     */
    fun dp2px(context: Context, value: Float): Float {
        return context.resources.displayMetrics.density * value + 0.5f
    }
}