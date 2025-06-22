package com.korniiienko.data.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class GetNoteResponse(
    val status: String,
    val element: NoteItemDto,
    val revision: Int
)
