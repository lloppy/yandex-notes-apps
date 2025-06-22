package com.korniiienko.notesapp.ui.screens

import android.graphics.Color
import com.korniiienko.model.Importance
import com.korniiienko.model.Note
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.UUID

data class NoteEntity(
    val uid: String = UUID.randomUUID().toString(),
    val title: String = "",
    val content: String = "",
    val color: Int = Color.WHITE,
    val importance: Importance = Importance.BASIC,
    val selfDestructDate: Long? = null,
)

fun NoteEntity.toNote(): Note = Note(
    uid = uid,
    title = title,
    content = content,
    color = color,
    importance = importance,
    expirationDate = selfDestructDate
)


fun Note.toUiState(): NoteEntity = NoteEntity(
    uid = uid,
    title = title,
    content = content,
    color = color,
    importance = importance,
    selfDestructDate = expirationDate
)

fun Long.toFormattedDate(): String {
    val dateTime = LocalDateTime.ofEpochSecond(this, 0, ZoneOffset.UTC)
    return DateTimeFormatter
        .ofPattern("dd.MM.yyyy")
        .format(dateTime)
}