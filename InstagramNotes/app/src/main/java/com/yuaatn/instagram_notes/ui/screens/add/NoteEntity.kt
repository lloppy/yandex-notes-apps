package com.yuaatn.instagram_notes.ui.screens.add

import android.graphics.Color
import com.yuaatn.instagram_notes.model.Importance
import com.yuaatn.instagram_notes.model.Note
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.UUID

data class NoteEntity(
    val uid: String = UUID.randomUUID().toString(),
    val title: String = "",
    val content: String = "",
    val color: Int = Color.WHITE,
    val importance: Importance = Importance.NORMAL,
    val expirationDate: Long? = null,
    val createdDate: Long? = null,
    val updatedDate: Long? = Date().time,
)

fun NoteEntity.toNote(): Note = Note(
    uid = uid,
    title = title,
    content = content,
    color = color,
    importance = importance,
)


fun Note.toUiState(): NoteEntity = NoteEntity(
    uid = uid,
    title = title,
    content = content,
    color = color,
    importance = importance,
)

fun Long.formattedDate(): String {
    val dateTime = LocalDateTime.ofEpochSecond(this, 0, ZoneOffset.UTC)
    return DateTimeFormatter
        .ofPattern("dd.MM.yyyy")
        .format(dateTime)
}