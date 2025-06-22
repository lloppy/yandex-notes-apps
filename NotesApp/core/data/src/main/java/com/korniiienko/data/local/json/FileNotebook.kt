package com.korniiienko.data.local.json

import android.content.Context
import com.korniiienko.domain.NotesRepository
import com.korniiienko.model.Note
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.slf4j.LoggerFactory
import java.io.File

import java.io.IOException

class FileNotebook(
    private val context: Context,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO),
) : NotesRepository {

    private val logger = LoggerFactory.getLogger(FileNotebook::class.java)
    private val file = File(context.filesDir, "notes.json")

    private val _notes = MutableStateFlow<List<Note>>(emptyList())
    override val notes: Flow<List<Note>> = _notes.asStateFlow()

    override fun addNote(note: Note) {
        scope.launch {
            val updatedList = _notes.value + note
            _notes.emit(updatedList)
            logger.debug("Добавлена заметка: ${note.title}")
            saveToFile()
        }
    }

    override fun getNoteByUid(uid: String): Flow<Note> =
        _notes.mapNotNull { notesList -> notesList.find { it.uid == uid } }
            .filterNotNull()
            .distinctUntilChanged()

    override fun updateNote(note: Note) {
        scope.launch {
            val updatedList = _notes.value.map {
                if (it.uid == note.uid) note else it
            }
            _notes.emit(updatedList)
            logger.debug("Обновлена заметка UID=${note.uid}")
            saveToFile()
        }
    }

    override fun deleteNote(uid: String) {
        scope.launch {
            val updatedList = _notes.value.toMutableList().apply {
                removeAll { it.uid == uid }
            }
            _notes.emit(updatedList)
            saveToFile()
        }
    }

    override fun saveToFile() {
        try {
            val jsonArray = JSONArray()
            _notes.value.forEach { jsonArray.put(it.json) }
            file.writeText(jsonArray.toString())
            logger.debug("Сохранено ${_notes.value.size} заметок в файл")
        } catch (e: IOException) {
            logger.error("Ошибка при сохранении заметок в файл", e)
        }
    }

    override fun loadFromFile() {
        scope.launch {
            if (!file.exists()) {
                logger.debug("Файл не найден, загрузка пропущена")
                return@launch
            }
            try {
                val jsonText = file.readText()
                val array = JSONArray(jsonText)
                val loadedNotes = mutableListOf<Note>()
                for (i in 0 until array.length()) {
                    val jsonNote = array.getJSONObject(i)
                    Note.parse(jsonNote)?.let {
                        loadedNotes.add(it)
                    }
                }
                _notes.emit(loadedNotes)
                logger.debug("Загружено ${loadedNotes.size} заметок из файла")
            } catch (e: Exception) {
                logger.error("Ошибка при загрузке заметок из файла", e)
            }
        }
    }

    override fun deleteAll() {
        scope.launch {
            _notes.emit(emptyList())
            try {
                file.delete()
                logger.debug("Все заметки и файл удалены")
            } catch (e: IOException) {
                logger.error("Ошибка при удалении файла", e)
            }
        }
    }

    fun close() {
        scope.cancel()
    }
}
