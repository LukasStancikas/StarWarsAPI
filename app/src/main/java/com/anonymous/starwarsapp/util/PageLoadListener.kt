package com.anonymous.starwarsapp.util

import androidx.paging.PagedList
import timber.log.Timber

abstract class PageLoadListenerWrapper : PagedList.LoadStateListener {
    private var hasInitiallyRefreshed: Boolean = false

    abstract fun onStartedRefresh()

    abstract fun onEndedRefresh()

    abstract fun onStartedNextPageLoad()

    abstract fun onEndedNextPageLoad()

    abstract fun onEndOfPagesReached()

    abstract fun onError(error: Throwable?)

    abstract fun onRetryableError(error: Throwable?)

    override fun onLoadStateChanged(
        type: PagedList.LoadType,
        state: PagedList.LoadState,
        error: Throwable?
    ) {
        Timber.e(type.toString())
        Timber.e(state.toString())
        when {
            // Handle start of full refresh
            type == PagedList.LoadType.REFRESH -> {
                hasInitiallyRefreshed = false
                onStartedRefresh()
            }
            // Handle start of load of additional page
            state == PagedList.LoadState.LOADING && type == PagedList.LoadType.END -> {
                onStartedNextPageLoad()
            }
            // Handle end of load of additional page (also occurs on full refresh)
            hasInitiallyRefreshed && state == PagedList.LoadState.IDLE && type == PagedList.LoadType.END -> {
                onEndedNextPageLoad()
            }
            // Handle end of full refresh
            state == PagedList.LoadState.DONE && type == PagedList.LoadType.START -> {
                hasInitiallyRefreshed = true
                onEndedRefresh()
            }
            // Handle end of pages (nothing more to load)
            state == PagedList.LoadState.DONE && type == PagedList.LoadType.END -> {
                onEndOfPagesReached()
            }
            // Handle Error
            state == PagedList.LoadState.ERROR -> {
                onError(error)
            }
            // Handle Retryable Error
            state == PagedList.LoadState.RETRYABLE_ERROR -> {
                onRetryableError(error)
            }
        }
    }
}