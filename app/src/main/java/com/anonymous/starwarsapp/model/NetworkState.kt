package com.anonymous.starwarsapp.model

sealed class NetworkState {
    object Done : NetworkState()
    object Loading : NetworkState()
    data class Error(val error: Throwable) : NetworkState()
}