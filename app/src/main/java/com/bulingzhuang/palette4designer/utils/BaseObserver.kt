package com.bulingzhuang.palette4designer.utils

import android.util.Log
import io.reactivex.Observer
import io.reactivex.disposables.Disposable

/**
 * Created by bulingzhuang
 * on 2017/9/5
 * E-mail:bulingzhuang@foxmail.com
 */
abstract class BaseObserver<T> : Observer<T> {

    var disposable: Disposable? = null

    override fun onSubscribe(d: Disposable?) {
        Log.e("blz", "onSubscribe")
        disposable = d
    }
}