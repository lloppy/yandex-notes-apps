package com.korniiienko.data.local.room.mappers


import com.korniiienko.data.local.room.model.NoteEntity
import com.korniiienko.model.Note
import java.time.LocalDateTime

fun NoteEntity.toDomain(): Note = Note(
    uid = this.uid,
    title = this.title,
    content = this.content,
    color = this.color,
    importance = this.importance,
    expirationDate = this.selfDestructDate
)

fun Note.toEntity(): NoteEntity = NoteEntity(
    uid = this.uid,
    title = this.title,
    content = this.content,
    color = this.color,
    importance = this.importance,
    selfDestructDate = this.expirationDate
)
