package com.anonymous.starwarsapp.util

import com.anonymous.starwarsapp.network.NetworkState
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject

fun <T> Observable<T>.asDriver(): Observable<T> {
    return subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
}

fun <T> Single<T>.asDriver(): Single<T> {
    return subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
}


fun <T> Single<T>.passNetworkStates(networkStateStream: PublishSubject<NetworkState>): Single<T> {
    return doOnSubscribe { networkStateStream.onNext(NetworkState.Loading) }
        .doOnSuccess { networkStateStream.onNext(NetworkState.Success) }
        .doOnError { networkStateStream.onNext(NetworkState.Error(it)) }
}