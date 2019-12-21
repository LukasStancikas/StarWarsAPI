package com.anonymous.starwarsapp.feature.characterlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.anonymous.starwarsapp.model.SWCharacter
import com.anonymous.starwarsapp.network.ApiController
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import timber.log.Timber
import javax.inject.Inject

class CharacterListViewModel @Inject constructor(api: ApiController) : ViewModel() {
    val characterDataStream: LiveData<PagedList<SWCharacter>>

    private val compositeDisposable = CompositeDisposable()
    private val characterDataSourceFactory = SWCharacterDataSourceFactory(api, compositeDisposable)

    init {
        val config = PagedList.Config.Builder()
            .setPrefetchDistance(3)
            .setEnablePlaceholders(true)
            .setPageSize(10)
            .build()

        characterDataStream = LivePagedListBuilder(characterDataSourceFactory, config)
            .build()
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