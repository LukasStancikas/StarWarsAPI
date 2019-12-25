package com.anonymous.starwarsapp.feature.characterdetails

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.anonymous.starwarsapp.R
import com.anonymous.starwarsapp.dagger.InjectableFragment
import com.anonymous.starwarsapp.model.NetworkState
import com.anonymous.starwarsapp.model.SWCharacter
import com.anonymous.starwarsapp.model.SWFilm
import com.anonymous.starwarsapp.model.SWStarship
import com.anonymous.starwarsapp.util.*
import com.jakewharton.rxbinding3.swiperefreshlayout.refreshes
import com.trello.rxlifecycle3.android.lifecycle.kotlin.bindToLifecycle
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.fragment_character_details.*
import kotlinx.android.synthetic.main.item_character_expanded.*
import javax.inject.Inject

class CharacterDetailsFragment : Fragment(), InjectableFragment {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var viewModel: CharacterDetailsViewModel
    private val character: SWCharacter by argument(KEY_CHARACTER)
    private val filmAdapter = FilmAdapter()
    private val starShipAdapter = StarShipAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = injectViewModel(viewModelFactory)
        return inflater.inflate(R.layout.fragment_character_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        characterDetailsToolbar.setNavigationOnClickListener {
            activity?.onBackPressed()
        }
        viewModel.loadCharacterDetails(character)
        characterFilmsRecycler.adapter = filmAdapter
        characterStarshipsRecycler.adapter = starShipAdapter
        showCharacterDetails()
    }

    private fun showCharacterDetails() {
        characterDetailsName.text = character.name
        characterDetailsGender.text = character.gender
        characterDetailsBirthYear.text = character.birthYear
        characterDetailsHeight.text = character.height
        characterDetailsMass.text = character.mass
        characterDetailsSkinColor.text = character.skinColor
        characterDetailsHairColor.text = character.hairColor
    }

    override fun onStart() {
        super.onStart()
        subscribeToViewModelStreams()
        subscribeToViewInteractions()
    }

    @SuppressLint("CheckResult")
    private fun subscribeToViewInteractions() {
        characterDetailsSwipeRefresh
            .refreshes()
            .bindToLifecycle(this)
            .subscribeBy(
                onNext = { viewModel.loadCharacterDetails(character) }
            )
    }

    @SuppressLint("CheckResult")
    private fun subscribeToViewModelStreams() {
        viewModel
            .filmsDataStream
            .asDriver()
            .bindToLifecycle(this)
            .subscribeBy(
                onNext = ::showFilms
            )

        viewModel
            .starshipsDataStream
            .asDriver()
            .bindToLifecycle(this)
            .subscribeBy(
                onNext = ::showStarships
            )

        viewModel
            .loadingStream
            .asDriver()
            .bindToLifecycle(this)
            .subscribeBy(
                onNext = ::changeLoadingState
            )
    }

    private fun changeLoadingState(networkState: NetworkState) {
        when (networkState) {
            NetworkState.Done -> characterDetailsSwipeRefresh.isRefreshing = false
            NetworkState.Loading -> characterDetailsSwipeRefresh.isRefreshing = true
            is NetworkState.Error -> {
                characterDetailsSwipeRefresh.isRefreshing = false
                showShortSnackbar(networkState.error.message)
            }
        }
    }

    private fun showFilms(list: List<SWFilm>) {
        characterFilmsLabel.goneIf(list.isEmpty())
        characterFilmsRecycler.goneIf(list.isEmpty())
        filmAdapter.setItems(list)
    }

    private fun showStarships(list: List<SWStarship>) {
        characterStarshipsLabel.goneIf(list.isEmpty())
        characterStarshipsRecycler.goneIf(list.isEmpty())
        starShipAdapter.setItems(list)
    }

    companion object {
        val TAG: String = CharacterDetailsFragment::class.java.name
        private const val KEY_CHARACTER = "character"

        fun getInstance(character: SWCharacter): CharacterDetailsFragment {
            return CharacterDetailsFragment().apply {
                val bundle = Bundle()
                bundle.putParcelable(KEY_CHARACTER, character)
                arguments = bundle
            }
        }
    }
}