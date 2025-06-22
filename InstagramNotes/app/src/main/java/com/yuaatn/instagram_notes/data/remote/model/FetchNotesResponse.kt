package com.yuaatn.instagram_notes.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// реквесты
@Serializable
data class ElementNoteRequest (
    @SerialName("element") val element: NoteDto
)

@Serializable
data class PatchNotesRequest (
    val list: List<NoteDto>
)

// респонсы
@Serializable
class FetchNotesResponse (
    val status: String,
    val list: List<NoteDto>,
    val revision: Int
)

@Serializable
data class FetchNoteResponse (
    val status: String,
    val element: NoteDto,
    val revision: Int
)

@Serializable
data class NoteResponse (
    val status: String,
    val element: NoteDto,
    val revision: Int
)

data class UidResponse (
    val uid: String
)