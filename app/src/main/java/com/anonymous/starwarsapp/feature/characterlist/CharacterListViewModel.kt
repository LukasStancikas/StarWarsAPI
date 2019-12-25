package com.anonymous.starwarsapp.feature.characterlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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

    private val _emptyResultStream = MutableLiveData<Boolean>()
    val emptyResultStream: LiveData<Boolean> = _emptyResultStream

    private val _errorStream = MutableLiveData<Throwable>()
    val errorStream: LiveData<Throwable> = _errorStream

    private val compositeDisposable = CompositeDisposable()
    private val characterDataSourceFactory =
        SWCharacterDataSourceFactory(api, compositeDisposable, ::onCharacterLoadError)

    init {
        val config = PagedList.Config.Builder()
            .setPrefetchDistance(PAGE_PREFETCH_DISTANCE)
            .setEnablePlaceholders(true)
            .setPageSize(PAGE_SIZE)
            .build()

        characterDataStream = LivePagedListBuilder(characterDataSourceFactory, config)
            .setBoundaryCallback(object : PagedList.BoundaryCallback<SWCharacter>() {
                override fun onZeroItemsLoaded() {
                    super.onZeroItemsLoaded()
                    _emptyResultStream.postValue(true)
                }

                override fun onItemAtFrontLoaded(itemAtFront: SWCharacter) {
                    super.onItemAtFrontLoaded(itemAtFront)
                    _emptyResultStream.postValue(false)
                }
            })
            .build()

        refreshData()
    }

    override fun onCleared() {
        compositeDisposable.clear()
        super.onCleared()
    }

    fun refreshData() {
        characterDataSourceFactory
            .dataSource
            .take(1)
            .subscribeBy(
                onNext = { it.invalidate() },
                onError = Timber::e
            )
            .addTo(compositeDisposable)
    }

    fun searchForCharacter(query: String) {
        characterDataSourceFactory.setQuery(query)
        refreshData()
    }

    private fun onCharacterLoadError(error: Throwable) {
        _errorStream.postValue(error)
    }

    companion object {
        const val PAGE_PREFETCH_DISTANCE = 3
        const val PAGE_SIZE = 10
    }
}