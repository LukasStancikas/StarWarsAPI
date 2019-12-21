package com.anonymous.starwarsapp.util

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

fun <T> Observable<T>.asDriver() : Observable<T>{
    return subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
}