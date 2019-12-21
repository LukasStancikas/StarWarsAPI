package com.anonymous.starwarsapp.feature.characterlist

import androidx.lifecycle.ViewModel
import androidx.paging.PagedList
import androidx.paging.RxPagedListBuilder
import com.anonymous.starwarsapp.model.SWCharacter
import com.anonymous.starwarsapp.network.ApiController
import com.anonymous.starwarsapp.network.NetworkState
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import timber.log.Timber
import javax.inject.Inject

class CharacterListViewModel @Inject constructor(api: ApiController) : ViewModel() {
    private val _characterDataStream = BehaviorSubject.create<PagedList<SWCharacter>>()
    private val _networkStateStream = PublishSubject.create<NetworkState>()
    private val compositeDisposable = CompositeDisposable()
    private val characterDataSourceFactory = SWCharacterDataSourceFactory(api, compositeDisposable)

    val characterDataStream: Observable<PagedList<SWCharacter>> = _characterDataStream.hide()
    val networkStateStream: Observable<NetworkState> = _networkStateStream.hide()

    init {
        characterDataSourceFactory
            .dataSource
            .take(1)
            .concatMap(SWCharacterDataSource::networkStateStream)
            .doOnError(Timber::e)
            .subscribeBy(
                onNext = _networkStateStream::onNext,
                onError = { _networkStateStream.onNext(NetworkState.Error(it)) }
            )
            .addTo(compositeDisposable)

        RxPagedListBuilder(characterDataSourceFactory, 1)
            .buildObservable()
            .doOnError(Timber::e)
            .subscribeBy(
                onNext = _characterDataStream::onNext,
                onError = { _networkStateStream.onNext(NetworkState.Error(it)) }
            )
            .addTo(compositeDisposable)
    }

    fun refreshAllData() {
        characterDataSourceFactory
            .dataSource
            .take(1)
            .subscribeBy(
                onNext = { it.invalidate() },
                onError = Timber::e
            )
            .addTo(compositeDisposable)
    }

    override fun onCleared() {
        compositeDisposable.clear()
        super.onCleared()
    }
}