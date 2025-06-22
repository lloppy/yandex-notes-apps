package com.yuaatn.instagram_notes.data.local.file

import android.content.Context
import com.yuaatn.instagram_notes.data.local.LocalRepository
import com.yuaatn.instagram_notes.model.Note
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import org.json.JSONArray
import java.io.File
import javax.inject.Inject

internal class FileNotebookImpl @Inject constructor(
    @ApplicationContext context: Context
) : LocalRepository {

    private val file = File(context.filesDir, "notes.json")
    private val _notes = MutableStateFlow<List<Note>>(emptyList())
    override val notes: Flow<List<Note>> get() = _notes

    override suspend fun addNote(note: Note) {
        _notes.value += note
    }

    override suspend fun getNoteByUid(uid: String): Flow<Note> =
        flow {
            val habit = checkNotNull(_notes.value.find { it.uid == uid })
            emit(habit)
        }

    override suspend fun updateNote(note: Note) {
        _notes.update { currentNotes ->
            currentNotes.map { if (it.uid == note.uid) note else it }
        }
    }

    override suspend fun deleteNote(uid: String) {
        _notes.value = _notes.value.filterNot { it.uid == uid }
    }

    override suspend fun saveToFile() {
        val jsonArray = JSONArray()
        _notes.value.forEach { jsonArray.put(it.json) }
        file.writeText(jsonArray.toString())
    }

    override suspend fun loadFromFile() {
        if (!file.exists()) return

        val jsonText = file.readText()
        val array = JSONArray(jsonText)
        val loadedNotes = mutableListOf<Note>()
        for (i in 0 until array.length()) {
            val jsonNote = array.getJSONObject(i)
            Note.parse(jsonNote)?.let {
                loadedNotes.add(it)
            }
        }
        _notes.value = loadedNotes
    }

    override suspend fun updateNotes(remoteNotes: List<Note>) {
        _notes.value = remoteNotes
        saveToFile()
    }
}