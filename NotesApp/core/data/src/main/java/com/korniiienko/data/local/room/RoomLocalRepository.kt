package com.korniiienko.data.local.room

import android.util.Log
import com.korniiienko.data.local.room.mappers.toDomain
import com.korniiienko.data.local.room.mappers.toEntity
import com.korniiienko.domain.LocalRepository
import com.korniiienko.model.Note
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map

class RoomLocalRepository(
    private val dao: NoteDao,
) : LocalRepository {

    override val notes: Flow<List<Note>> = dao.getAll().map { entities ->
        entities.map { it.toDomain() }
    }

    override suspend fun addNote(note: Note) {
        val updatedRows = dao.updateByUid(
            uid = note.uid,
            title = note.title,
            content = note.content,
            color = note.color,
            importance = note.importance,
            selfDestructDate = note.expirationDate
        )

        if (updatedRows == 0) {
            dao.insert(note.toEntity())
        }
    }

    override suspend fun updateNote(note: Note) {
        val updatedRows = dao.updateByUid(
            uid = note.uid,
            title = note.title,
            content = note.content,
            color = note.color,
            importance = note.importance,
            selfDestructDate = note.expirationDate
        )
        if (updatedRows == 0) {
            dao.insert(note.toEntity())
        }
    }

    override suspend fun getNoteByUid(uid: String): Flow<Note?> {
        return dao.getByUid(uid)
            .filterNotNull()
            .map {
                it.toDomain()
            }
    }

    override suspend fun deleteNote(uid: String) {
        dao.deleteByUid(uid)
    }

    override suspend fun deleteAll() {
        dao.deleteAll()
    }

    override suspend fun save() {
        TODO("Not yet implemented - unused here - only for json sync")
    }

    override suspend fun load() {
        Log.w("RoomLocalRepository","unused here with room- only for json sync")
    }
}