package com.anonymous.starwarsapp.feature.characterdetails

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.anonymous.starwarsapp.R
import com.anonymous.starwarsapp.dagger.InjectableFragment
import com.anonymous.starwarsapp.model.SWCharacter
import com.anonymous.starwarsapp.model.SWFilm
import com.anonymous.starwarsapp.util.argument
import com.anonymous.starwarsapp.util.asDriver
import com.anonymous.starwarsapp.util.injectViewModel
import com.jakewharton.rxbinding3.swiperefreshlayout.refreshes
import com.trello.rxlifecycle3.android.lifecycle.kotlin.bindToLifecycle
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.fragment_character_details.*
import javax.inject.Inject

class CharacterDetailsFragment : Fragment(), InjectableFragment {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var viewModel: CharacterDetailsViewModel
    private val character: SWCharacter by argument(KEY_CHARACTER)

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
        characterDetailsSwipeRefresh.isRefreshing = true
        viewModel.loadCharacterDetails(character)
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
                onNext = ::showFilms,
                onError = { characterDetailsSwipeRefresh.isRefreshing = false }
            )
    }

    private fun showFilms(list: List<SWFilm>) {
        characterDetailsSwipeRefresh.isRefreshing = false
        characterDetailsName.text = TextUtils.join(", ", list.map { it.title })
    }

    companion object {
        val TAG = CharacterDetailsFragment::class.java.name
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