package com.korniiienko.data.local.json

import android.content.Context
import com.korniiienko.domain.LocalRepository
import com.korniiienko.model.Note
import kotlinx.coroutines.flow.Flow

class JsonLocalRepository(
    context: Context,
) : LocalRepository {
    private val fileNotebook = FileNotebook(context)

    override val notes: Flow<List<Note>> = fileNotebook.notes

    override suspend fun addNote(note: Note) {
        fileNotebook.addNote(note)
        fileNotebook.saveToFile()
    }

    override suspend fun updateNote(note: Note) {
        fileNotebook.updateNote(note)
    }

    override suspend fun deleteNote(uid: String) {
        fileNotebook.deleteNote(uid)
    }

    override suspend fun getNoteByUid(uid: String): Flow<Note> {
        return fileNotebook.getNoteByUid(uid)
    }

    override suspend fun save() {
        fileNotebook.saveToFile()
    }

    override suspend fun load() {
        fileNotebook.loadFromFile()
    }

    override suspend fun deleteAll() {
        fileNotebook.deleteAll()
    }
}
