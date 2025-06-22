package com.yuaatn.instagram_notes.data.local.room.mappers

import com.yuaatn.instagram_notes.data.local.room.entity.NoteEntity
import com.yuaatn.instagram_notes.model.Note

fun NoteEntity.toDomain(): Note = Note(
    uid = this.uid,
    title = this.title,
    content = this.content,
    color = this.color,
    importance = this.importance,

    deadline = this.deadline,
    createdAt = this.createdAt,
    changedAt = this.changedAt,
    deviceId = this.deviceId
)

fun Note.toEntity(): NoteEntity = NoteEntity(
    uid = this.uid,
    title = this.title,
    content = this.content,
    color = this.color,
    importance = this.importance,

    deadline = this.deadline,
    createdAt = this.createdAt,
    changedAt = this.changedAt,
    deviceId = this.deviceId
)
