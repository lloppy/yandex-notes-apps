package com.yuaatn.instagram_notes.data.sync

import com.yuaatn.instagram_notes.model.Note
import kotlinx.coroutines.flow.Flow

interface SyncManager {
    val notes: Flow<List<Note>>

    suspend fun synchronize()
    suspend fun getNoteByUid(uid: String): Flow<Note?>

    suspend fun syncOnCreate(note: Note)
    suspend fun syncOnUpdate(note: Note)
    suspend fun syncOnDelete(uid: String)
}

interface ConflictResolver {
    suspend fun resolveConflicts(localNotes: List<Note>, remoteNotes: List<Note>)
}
