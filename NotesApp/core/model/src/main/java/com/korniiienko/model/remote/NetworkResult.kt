package com.korniiienko.model.remote

sealed class NetworkResult<out T> {
    data class Success<out T>(val data: T) : NetworkResult<T>()
    data class Failure(val error: Throwable) : NetworkResult<Nothing>()

    inline fun <R> onSuccess(block: (T) -> R): NetworkResult<R> {
        return when (this) {
            is Success -> try {
                Success(block(data))
            } catch (e: Throwable) {
                Failure(e)
            }
            is Failure -> this
        }
    }

    inline fun onFailure(block: (Throwable) -> Unit): NetworkResult<T> {
        if (this is Failure) block(error)
        return this
    }
}