package com.yuaatn.instagram_notes.data.local.file

import com.yuaatn.instagram_notes.data.local.LocalRepository
import com.yuaatn.instagram_notes.model.Note
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.count
import org.slf4j.LoggerFactory
import javax.inject.Inject

internal class FileNotebookProxy @Inject constructor(
    private val localRepositoryImpl: LocalRepository
) : LocalRepository {
    private val logger = LoggerFactory.getLogger(FileNotebookProxy::class.java)

    override val notes: Flow<List<Note>> get() = localRepositoryImpl.notes

    override suspend fun addNote(note: Note) {
        localRepositoryImpl.addNote(note)
        logger.debug("Добавлена заметка: ${note.title}")
    }

    override suspend fun getNoteByUid(uid: String): Flow<Note> {
        logger.debug("Получение заметки по UID: $uid")
        return localRepositoryImpl.getNoteByUid(uid)
    }

    override suspend fun updateNote(note: Note) {
        localRepositoryImpl.updateNote(note)
        logger.debug("Обновлена заметка: ${note.title} (UID: ${note.uid})")
    }

    override suspend fun deleteNote(uid: String) {
        localRepositoryImpl.deleteNote(uid)
        logger.debug("Удаление заметки UID=$uid")
    }

    override suspend fun saveToFile() {
        localRepositoryImpl.saveToFile()
        logger.debug("Сохранено ${localRepositoryImpl.notes.count()} заметок в файл")
    }

    override suspend fun loadFromFile() {
        localRepositoryImpl.loadFromFile()
        logger.debug("Загружено ${localRepositoryImpl.notes.count()} заметок из файла")
    }

    override suspend fun updateNotes(remoteNotes: List<Note>) {
        localRepositoryImpl.loadFromFile()
        logger.debug("Обновляем локальное хранилище. Загрузка заметок из удаленного репозитория в локальный")
    }
}