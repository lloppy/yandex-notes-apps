package com.korniiienko.model.remote

class NetworkError(
    message: String,
    cause: Throwable? = null
) : Throwable(message, cause)
