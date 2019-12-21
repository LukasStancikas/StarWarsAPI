package com.anonymous.starwarsapp.feature.characterlist

import androidx.lifecycle.ViewModel
import com.anonymous.starwarsapp.model.SWCharacterPage
import com.anonymous.starwarsapp.network.ApiController
import com.anonymous.starwarsapp.network.DataResult
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import timber.log.Timber
import javax.inject.Inject

class CharacterListViewModel @Inject constructor(private val api: ApiController) : ViewModel() {
    private val _characterDataStream = BehaviorSubject.create<DataResult<SWCharacterPage>>()
    private val disposables = CompositeDisposable()

    val characterDataStream = _characterDataStream.hide()

    fun refreshCharacters() {
        _characterDataStream.onNext(DataResult.loading())
        api
            .getStarWarsCharacters(1)
            .subscribeOn(Schedulers.io())
            .doOnError(Timber::e)
            .subscribeBy(
                onSuccess = {_characterDataStream.onNext(DataResult.success(it))},
                onError = {
                    _characterDataStream.onNext(DataResult.error(it.message))
                }
            )
            .addTo(disposables)
    }

    override fun onCleared() {
        disposables.clear()
        super.onCleared()
    }
}