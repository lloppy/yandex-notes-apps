package com.yuaatn.instagram_notes.data.remote.mapper

import android.graphics.Color
import com.yuaatn.instagram_notes.data.remote.model.NoteDto
import com.yuaatn.instagram_notes.model.Importance
import com.yuaatn.instagram_notes.model.Note
import java.util.Date

fun Note.toDto(): NoteDto = NoteDto(
    id = this.uid,
    text = this.title,
    importance =  when (this.importance) {
        Importance.LOW -> "low"
        Importance.NORMAL -> "basic"
        Importance.HIGH -> "important"
    },

    deadline = this.deadline,
    isDone = false,
    lastUpdatedBy = this.deviceId,

    createdAt = this.createdAt ?: Date().time,
    changedAt = this.changedAt ?: Date().time,

    color = if (this.color != Color.WHITE) {
        String.format("#%06X", 0xFFFFFF and this.color)
    } else {
        null
    }
)

fun NoteDto.toDomain(): Note = Note(
    uid = this.id,
    title = this.text,
    content = "",
    importance = when (this.importance.lowercase()) {
        "low" -> Importance.LOW
        "important" -> Importance.HIGH
        "normal" -> Importance.NORMAL
        else -> Importance.NORMAL
    },

    deadline = this.deadline,
    createdAt = this.createdAt,
    changedAt = this.changedAt,

    color = this.color?.let {
        try {
            Color.parseColor(it)
        } catch (e: IllegalArgumentException) {
            Color.WHITE
        }
    } ?: Color.WHITE,
)