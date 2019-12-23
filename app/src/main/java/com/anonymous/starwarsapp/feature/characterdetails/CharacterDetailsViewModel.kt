package com.anonymous.starwarsapp.feature.characterdetails

import androidx.lifecycle.ViewModel
import com.anonymous.starwarsapp.model.SWCharacter
import com.anonymous.starwarsapp.model.SWFilm
import com.anonymous.starwarsapp.model.SWStarship
import com.anonymous.starwarsapp.network.ApiController
import com.anonymous.starwarsapp.util.asDriver
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import timber.log.Timber
import javax.inject.Inject

class CharacterDetailsViewModel @Inject constructor(private val api: ApiController) : ViewModel() {
    private val _filmsDataStream = BehaviorSubject.create<List<SWFilm>>()
    private val _starshipsDataStream = BehaviorSubject.create<List<SWStarship>>()

    private val compositeDisposable = CompositeDisposable()

    val filmsDataStream = _filmsDataStream.hide()
    val starshipsDataStream = _starshipsDataStream.hide()

    override fun onCleared() {
        compositeDisposable.clear()
        super.onCleared()
    }

    fun loadCharacterDetails(character: SWCharacter) {
        val filmRequests = character.getFilmIds().map { api.getStarWarsFilm(it) }
        Single.concat(filmRequests)
            .buffer(filmRequests.size)
            .asDriver()
            .doOnError(Timber::e)
            .subscribeBy(
                onNext = _filmsDataStream::onNext,
                onError = _filmsDataStream::onError
            )
            .addTo(compositeDisposable)
    }
}