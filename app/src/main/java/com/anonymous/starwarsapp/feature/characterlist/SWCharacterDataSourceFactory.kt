package com.anonymous.starwarsapp.feature.characterlist

import androidx.paging.DataSource
import com.anonymous.starwarsapp.model.SWCharacter
import com.anonymous.starwarsapp.network.ApiController
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject

class SWCharacterDataSourceFactory(
    private val apiController: ApiController,
    private val compositeDisposable: CompositeDisposable,
    private val onError: (Throwable) -> Unit
) : DataSource.Factory<Int, SWCharacter>() {

    private var query: String? = null

    private val _dataSource = BehaviorSubject.create<SWCharacterDataSource>()
    val dataSource: Observable<SWCharacterDataSource> = _dataSource.hide()

    override fun create(): DataSource<Int, SWCharacter> {
        return SWCharacterDataSource(apiController, onError, compositeDisposable, query)
            .apply {
                _dataSource.onNext(this)
            }
    }

    fun setQuery(newQuery: String?) {
        query = newQuery
    }
}