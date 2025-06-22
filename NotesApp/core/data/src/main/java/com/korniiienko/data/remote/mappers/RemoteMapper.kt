package com.korniiienko.data.remote.mappers

import android.graphics.Color
import com.korniiienko.data.remote.model.NoteItemDto
import com.korniiienko.model.Importance
import com.korniiienko.model.Note
import java.util.Date
import java.util.Locale


fun Note.toRemoteDto(deviceId: String): NoteItemDto = NoteItemDto(
    id = this.uid,
    text = this.title,
    importance = this.importance.name.lowercase(Locale.getDefault()),
    deadline = this.expirationDate,
    done = false,
    createdAt = this.createdAt ?: Date().time,
    changedAt = this.updatedAt ?: Date().time,
    lastUpdatedBy = deviceId,
    color = if (this.color != Color.WHITE) {
        String.format("#%06X", 0xFFFFFF and this.color)
    } else {
        null
    }
)

fun NoteItemDto.toModel(): Note = Note(
    uid = this.id,
    title = this.text,
    content = "",
    importance = when (this.importance.lowercase()) {
        "low" -> Importance.LOW
        "important" -> Importance.IMPORTANT
        "basic" -> Importance.BASIC
        else -> Importance.BASIC
    },
    color = this.color?.let {
        try {
            Color.parseColor(it)
        } catch (e: IllegalArgumentException) {
            Color.WHITE
        }
    } ?: Color.WHITE,
    expirationDate = this.deadline,
    createdAt = this.createdAt,
    updatedAt = this.changedAt
)