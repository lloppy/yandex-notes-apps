package com.korniiienko.data.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class PatchNotesRequest(
    val list: List<NoteItemDto>
)
