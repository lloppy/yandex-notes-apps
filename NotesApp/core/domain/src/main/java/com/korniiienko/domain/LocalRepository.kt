package com.korniiienko.domain

import com.korniiienko.model.Note
import kotlinx.coroutines.flow.Flow

interface LocalRepository {
    val notes: Flow<List<Note>>
    suspend fun addNote(note: Note)
    suspend fun updateNote(note: Note)
    suspend fun deleteNote(uid: String)
    suspend fun getNoteByUid(uid: String): Flow<Note?>
    suspend fun save()
    suspend fun load()
    suspend fun deleteAll()
}