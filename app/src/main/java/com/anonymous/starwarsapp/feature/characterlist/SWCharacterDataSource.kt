package com.anonymous.starwarsapp.feature.characterlist

import androidx.paging.PageKeyedDataSource
import com.anonymous.starwarsapp.model.SWCharacter
import com.anonymous.starwarsapp.network.ApiController
import com.anonymous.starwarsapp.util.asDriver
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy

class SWCharacterDataSource(
    private val api: ApiController,
    private val onError: (Throwable) -> Unit,
    private val compositeDisposable: CompositeDisposable,
    private val query: String?
) : PageKeyedDataSource<Int, SWCharacter>() {

    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, SWCharacter>
    ) {
        api.getStarWarsCharacters(query, 1)
            .asDriver()
            .subscribeBy(
                onSuccess = {
                    val nextPage = it.next?.let { 2 }
                    callback.onResult(it.results, null, nextPage)
                },
                onError = onError
            )
            .addTo(compositeDisposable)
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, SWCharacter>) {
        api.getStarWarsCharacters(query, params.key)
            .asDriver()
            .subscribeBy(
                onSuccess = {
                    val nextPage = it.next?.let { params.key + 1 }
                    callback.onResult(it.results, nextPage)
                },
                onError = onError
            )
            .addTo(compositeDisposable)
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, SWCharacter>) {
        api.getStarWarsCharacters(query, params.key)
            .asDriver()
            .subscribeBy(
                onSuccess = {
                    val previousPage = it.previous?.let { params.key - 1 }
                    callback.onResult(it.results, previousPage)
                },
                onError = onError
            )
            .addTo(compositeDisposable)
    }
}