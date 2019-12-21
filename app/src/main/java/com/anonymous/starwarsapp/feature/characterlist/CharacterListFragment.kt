package com.anonymous.starwarsapp.feature.characterlist

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.anonymous.starwarsapp.R
import com.anonymous.starwarsapp.dagger.InjectableFragment
import com.anonymous.starwarsapp.model.SWCharacterPage
import com.anonymous.starwarsapp.network.DataResult
import com.anonymous.starwarsapp.util.asDriver
import com.anonymous.starwarsapp.util.injectViewModel
import com.anonymous.starwarsapp.util.showSnackbar
import com.google.android.material.snackbar.Snackbar
import com.jakewharton.rxbinding3.swiperefreshlayout.refreshes
import com.trello.rxlifecycle3.android.lifecycle.kotlin.bindToLifecycle
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.fragment_character_list.*
import javax.inject.Inject

class CharacterListFragment : Fragment(), InjectableFragment {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var viewModel: CharacterListViewModel
    private val adapter by lazy { CharacterAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = injectViewModel(viewModelFactory)
        return inflater.inflate(R.layout.fragment_character_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        characterRecycler.adapter = adapter
    }

    override fun onStart() {
        super.onStart()
        subscribeToData()
        subscribeViewInteractions()
        viewModel.refreshCharacters()
    }

    @SuppressLint("CheckResult")
    private fun subscribeViewInteractions() {
        characterSwipeRefresh
            .refreshes()
            .bindToLifecycle(this)
            .subscribeBy(
                onNext = { viewModel.refreshCharacters() }
            )
    }

    @SuppressLint("CheckResult")
    private fun subscribeToData() {
        viewModel
            .characterDataStream
            .asDriver()
            .bindToLifecycle(this)
            .subscribeBy(
                onNext = ::showData
            )
    }

    private fun showData(dataResult: DataResult<SWCharacterPage>) {
        when (dataResult.status) {
            DataResult.Status.SUCCESS -> {
                characterSwipeRefresh.isRefreshing = false
                dataResult.data?.let { adapter.setItems(it.results) }
                showSnackbar("StarWars characters loaded", Snackbar.LENGTH_SHORT)
            }
            DataResult.Status.ERROR -> {
                characterSwipeRefresh.isRefreshing = false
                showSnackbar(dataResult.message, Snackbar.LENGTH_SHORT)
            }
            DataResult.Status.LOADING -> {
                characterSwipeRefresh.isRefreshing = true
            }
        }
    }
}