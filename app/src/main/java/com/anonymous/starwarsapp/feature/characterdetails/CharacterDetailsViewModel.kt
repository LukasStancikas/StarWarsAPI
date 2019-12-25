package com.anonymous.starwarsapp.feature.characterdetails

import androidx.lifecycle.ViewModel
import com.anonymous.starwarsapp.model.NetworkState
import com.anonymous.starwarsapp.model.SWCharacter
import com.anonymous.starwarsapp.model.SWFilm
import com.anonymous.starwarsapp.model.SWStarship
import com.anonymous.starwarsapp.network.ApiController
import com.anonymous.starwarsapp.util.asDriver
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.Singles
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.subjects.BehaviorSubject
import timber.log.Timber
import javax.inject.Inject

class CharacterDetailsViewModel @Inject constructor(private val api: ApiController) : ViewModel() {
    private val _filmsDataStream = BehaviorSubject.create<List<SWFilm>>()
    private val _starshipsDataStream = BehaviorSubject.create<List<SWStarship>>()
    private val _loadingStream = BehaviorSubject.create<NetworkState>()

    private val compositeDisposable = CompositeDisposable()

    val filmsDataStream: Observable<List<SWFilm>> = _filmsDataStream.hide()
    val starshipsDataStream: Observable<List<SWStarship>> = _starshipsDataStream.hide()
    val loadingStream: Observable<NetworkState> = _loadingStream.hide()

    override fun onCleared() {
        compositeDisposable.clear()
        super.onCleared()
    }

    fun loadCharacterDetails(character: SWCharacter) {
        val filmRequests = character.getFilmIds().map { api.getStarWarsFilm(it) }
        val starshipRequests = character.getStarshipIds().map { api.getStarWarsStarship(it) }

        val filmListStream = convertRequestsToListStream(filmRequests)
        val starshipListStream = convertRequestsToListStream(starshipRequests)

        filmListStream
            .asDriver()
            .subscribeBy(
                onSuccess = _filmsDataStream::onNext,
                onError = {
                    _loadingStream.onNext(NetworkState.Error(it))
                }
            )
            .addTo(compositeDisposable)

        starshipListStream
            .asDriver()
            .subscribeBy(
                onSuccess = _starshipsDataStream::onNext,
                onError = {
                    _loadingStream.onNext(NetworkState.Error(it))
                }
            )
            .addTo(compositeDisposable)

        Singles.zip(filmListStream, starshipListStream)
            .asDriver()
            .doOnSubscribe { _loadingStream.onNext(NetworkState.Loading) }
            .doOnError(Timber::e)
            .subscribeBy(
                onSuccess = {
                    _loadingStream.onNext(NetworkState.Done)
                },
                onError = {}
            )
            .addTo(compositeDisposable)
    }

    private fun <T> convertRequestsToListStream(requests: List<Single<T>>): Single<List<T>> {
        return if (requests.isNotEmpty()) {
            Single.merge(requests)
                .buffer(requests.size)
                .take(1)
                .singleOrError()
        } else {
            Single.just(emptyList())
        }
    }
}