package com.anonymous.starwarsapp.feature.characterlist

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.paging.PagedList
import com.anonymous.starwarsapp.R
import com.anonymous.starwarsapp.dagger.InjectableFragment
import com.anonymous.starwarsapp.util.injectViewModel
import com.anonymous.starwarsapp.util.showSnackbar
import com.google.android.material.snackbar.Snackbar
import com.jakewharton.rxbinding3.swiperefreshlayout.refreshes
import com.trello.rxlifecycle3.android.lifecycle.kotlin.bindToLifecycle
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.fragment_character_list.*
import javax.inject.Inject

class CharacterListFragment : Fragment(), InjectableFragment, PagedList.LoadStateListener {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var viewModel: CharacterListViewModel
    private val adapter by lazy { CharacterAdapter() }

    private var hasInitiallyRefreshed: Boolean = false

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
        adapter.addLoadStateListener(this)
        subscribeToPagedCharacterStream()
        subscribeViewInteractions()
    }

    override fun onStop() {
        adapter.removeLoadStateListener(this)
        super.onStop()
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

    private fun subscribeToPagedCharacterStream() {
        viewModel
            .characterDataStream
            .observe(this, Observer {
                adapter.submitList(it)
            })
    }

    override fun onLoadStateChanged(
        type: PagedList.LoadType,
        state: PagedList.LoadState,
        error: Throwable?
    ) {
        when {
            // Handle start of full refresh
            type == PagedList.LoadType.REFRESH -> {
                hasInitiallyRefreshed = false
                characterSwipeRefresh.isRefreshing = true
            }
            // Handle start of load of additional page
            state == PagedList.LoadState.LOADING && type == PagedList.LoadType.END -> {
                characterSwipeRefresh.isRefreshing = true
            }
            // Handle end of load of additional page (also occurs on full refresh)
            hasInitiallyRefreshed && state == PagedList.LoadState.IDLE && type == PagedList.LoadType.END -> {
                characterSwipeRefresh.isRefreshing = false
            }
            // Handle end of full refresh
            state == PagedList.LoadState.DONE && type == PagedList.LoadType.START -> {
                hasInitiallyRefreshed = true
                characterSwipeRefresh.isRefreshing = false
                showSnackbar(getString(R.string.message_characters_loaded), Snackbar.LENGTH_SHORT)
            }
            // Handle end of pages (nothing more to load)
            state == PagedList.LoadState.DONE && type == PagedList.LoadType.END -> {
                characterSwipeRefresh.isRefreshing = false
                showSnackbar(getString(R.string.message_characters_ended), Snackbar.LENGTH_SHORT)
            }
            // Handle Error
            state == PagedList.LoadState.ERROR -> {
                characterSwipeRefresh.isRefreshing = false
                showSnackbar(error?.message, Snackbar.LENGTH_SHORT)
            }
            // Handle Retryable Error
            state == PagedList.LoadState.RETRYABLE_ERROR -> {
                characterSwipeRefresh.isRefreshing = false
                showSnackbar(error?.message, Snackbar.LENGTH_SHORT)
            }
        }
    }
}