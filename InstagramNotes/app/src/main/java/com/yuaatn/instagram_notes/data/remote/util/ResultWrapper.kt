package com.yuaatn.instagram_notes.data.remote.util

sealed class ResultWrapper<out R> {
    data class Success<out R>(val payload: R) : ResultWrapper<R>()
    data class Error(val exception: Throwable) : ResultWrapper<Nothing>()

    inline fun handleError(action: (Throwable) -> Unit): ResultWrapper<R> {
        if (this is Error) action(exception)
        return this
    }
}