package com.korniiienko.data.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class SingleNoteResponse(
    val status: String,
    val element: NoteItemDto,
    val revision: Int
)
