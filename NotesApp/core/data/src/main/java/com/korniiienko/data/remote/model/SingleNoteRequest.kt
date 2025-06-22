package com.korniiienko.data.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class SingleNoteRequest(
    val element: NoteItemDto
)