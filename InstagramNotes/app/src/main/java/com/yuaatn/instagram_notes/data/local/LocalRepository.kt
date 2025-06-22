package com.yuaatn.instagram_notes.data.local

import com.yuaatn.instagram_notes.model.Note
import kotlinx.coroutines.flow.Flow

interface LocalRepository {
    val notes: Flow<List<Note>>

    suspend fun addNote(note: Note)
    suspend fun getNoteByUid(uid: String): Flow<Note>
    suspend fun updateNote(note: Note)
    suspend fun deleteNote(uid: String)

    suspend fun saveToFile()
    suspend fun loadFromFile()
    suspend fun updateNotes(remoteNotes: List<Note>)
}