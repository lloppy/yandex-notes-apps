package com.korniiienko.data.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class GetNotesResponse(
    val status: String,
    val list: List<NoteItemDto>,
    val revision: Int
)