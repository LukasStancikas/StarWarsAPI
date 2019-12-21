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
import com.anonymous.starwarsapp.network.NetworkState
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
        subscribeToViewModelStreams()
        subscribeViewInteractions()
    }

    @SuppressLint("CheckResult")
    private fun subscribeViewInteractions() {
        characterSwipeRefresh
            .refreshes()
            .bindToLifecycle(this)
            .subscribeBy(
                onNext = { viewModel.refreshAllData() }
            )
    }

    @SuppressLint("CheckResult")
    private fun subscribeToViewModelStreams() {
        viewModel
            .networkStateStream
            .asDriver()
            .bindToLifecycle(this)
            .subscribeBy(
                onNext = ::handleNetworkState
            )

        viewModel
            .characterDataStream
            .asDriver()
            .bindToLifecycle(this)
            .subscribeBy(
                onNext = adapter::submitList
            )
    }

    private fun handleNetworkState(networkState: NetworkState) {
        when (networkState) {
            NetworkState.Success -> {
                characterSwipeRefresh.isRefreshing = false
                showSnackbar("StarWars characters loaded", Snackbar.LENGTH_SHORT)
            }
            is NetworkState.Error -> {
                characterSwipeRefresh.isRefreshing = false
                showSnackbar(networkState.throwable.message, Snackbar.LENGTH_SHORT)
            }
            NetworkState.Loading -> {
                characterSwipeRefresh.isRefreshing = true
            }
        }
    }
}