package com.korniiienko.domain

import com.korniiienko.model.Note
import kotlinx.coroutines.flow.Flow

interface NotesRepository {
    val notes: Flow<List<Note>>
    fun addNote(note: Note)
    fun getNoteByUid(uid: String): Flow<Note>
    fun updateNote(note: Note)
    fun deleteNote(uid: String)
    fun saveToFile()
    fun loadFromFile()
    fun deleteAll()
}