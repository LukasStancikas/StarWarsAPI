package com.anonymous.starwarsapp.feature.characterlist

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.anonymous.starwarsapp.R
import com.anonymous.starwarsapp.dagger.InjectableFragment
import com.anonymous.starwarsapp.feature.MainActivity
import com.anonymous.starwarsapp.model.SWCharacter
import com.anonymous.starwarsapp.util.PageLoadListenerWrapper
import com.anonymous.starwarsapp.util.injectViewModel
import com.anonymous.starwarsapp.util.showShortSnackbar
import com.jakewharton.rxbinding3.appcompat.queryTextChanges
import com.jakewharton.rxbinding3.swiperefreshlayout.refreshes
import com.trello.rxlifecycle3.android.lifecycle.kotlin.bindToLifecycle
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.fragment_character_list.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class CharacterListFragment : Fragment(), InjectableFragment, CharacterAdapter.OnItemClickListener {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var viewModel: CharacterListViewModel
    private val adapter by lazy { CharacterAdapter() }

    private val pageLoadListener = object : PageLoadListenerWrapper() {
        override fun onStartedRefresh() {
            characterSwipeRefresh.isRefreshing = true
        }

        override fun onEndedRefresh() {
            characterSwipeRefresh.isRefreshing = false
            showShortSnackbar(getString(R.string.message_characters_loaded))
        }

        override fun onStartedNextPageLoad() {
            characterSwipeRefresh.isRefreshing = true
        }

        override fun onEndedNextPageLoad() {
            characterSwipeRefresh.isRefreshing = false
        }

        override fun onEndOfPagesReached() {
            characterSwipeRefresh.isRefreshing = false
            showShortSnackbar(getString(R.string.message_characters_ended))
        }

        override fun onError(error: Throwable?) {
            characterSwipeRefresh.isRefreshing = false
            showShortSnackbar(error?.message)
        }

        override fun onRetryableError(error: Throwable?) {
            characterSwipeRefresh.isRefreshing = false
            showShortSnackbar(error?.message)
        }
    }

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
        adapter.setOnItemClickListener(this)
    }

    override fun onStart() {
        super.onStart()
        adapter.addLoadStateListener(pageLoadListener)
        subscribeToViewModelStreams()
        subscribeViewInteractions()
    }

    override fun onStop() {
        adapter.removeLoadStateListener(pageLoadListener)
        super.onStop()
    }

    override fun onClick(item: SWCharacter) {
        (activity as? MainActivity)?.onSWCharacterClicked(item)
    }

    @SuppressLint("CheckResult")
    private fun subscribeViewInteractions() {
        characterSwipeRefresh
            .refreshes()
            .bindToLifecycle(this)
            .subscribeBy(
                onNext = { viewModel.refreshData() }
            )

        characterSearch
            .queryTextChanges()
            .debounce(SEARCH_DEBOUNCE_MS, TimeUnit.MILLISECONDS)
            .bindToLifecycle(this)
            .subscribeBy(
                onNext = { viewModel.searchForCharacter(it.toString()) }
            )
    }

    private fun subscribeToViewModelStreams() {
        viewModel
            .characterDataStream
            .observe(this, Observer {
                adapter.submitList(it)
            })

        viewModel
            .emptyResultStream
            .observe(this, Observer { emptyResults ->
                if (emptyResults) {
                    characterSwipeRefresh.isRefreshing = false
                    showShortSnackbar(getString(R.string.message_characters_empty))
                }
            })
    }

    companion object {
        const val SEARCH_DEBOUNCE_MS = 400L
    }
}