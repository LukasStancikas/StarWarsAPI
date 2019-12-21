package com.anonymous.starwarsapp.network

sealed class NetworkState {
    object Success : NetworkState()
    object Loading : NetworkState()
    class Error(val throwable: Throwable) : NetworkState()
}