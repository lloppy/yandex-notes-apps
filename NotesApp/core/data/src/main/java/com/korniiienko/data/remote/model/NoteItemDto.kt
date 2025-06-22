package com.korniiienko.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.Date

@Serializable
data class NoteItemDto(
    val id: String,
    val text: String,
    val importance: String,
    val deadline: Long? = null,
    val done: Boolean,
    @SerialName("created_at") val createdAt: Long,
    @SerialName("changed_at") val changedAt: Long = Date().time,
    @SerialName("last_updated_by") val lastUpdatedBy: String,
    val color: String? = null,
)