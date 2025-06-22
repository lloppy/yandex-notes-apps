package com.yuaatn.instagram_notes.data.remote

import com.yuaatn.instagram_notes.data.remote.model.UidResponse
import com.yuaatn.instagram_notes.data.remote.util.ResultWrapper
import com.yuaatn.instagram_notes.model.Note

interface RemoteRepository {
    suspend fun fetchNotes(): ResultWrapper<List<Note>>
    suspend fun fetchNoteByUid(uid: String): ResultWrapper<Note>

    suspend fun createNote(note: Note): ResultWrapper<UidResponse>
    suspend fun updateNote(note: Note): ResultWrapper<UidResponse>
    suspend fun removeNoteByUid(uid: String): ResultWrapper<Unit>

    suspend fun patchNotes(notes: List<Note>): ResultWrapper<List<Note>>
    suspend fun fetchNotesWithThreshold(threshold: Int?): ResultWrapper<List<Note>>
    suspend fun clearNotes()
}