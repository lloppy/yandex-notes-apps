package com.yuaatn.instagram_notes.data.remote

import com.yuaatn.instagram_notes.data.remote.mapper.toDomain
import com.yuaatn.instagram_notes.data.remote.mapper.toDto
import com.yuaatn.instagram_notes.data.remote.model.ElementNoteRequest
import com.yuaatn.instagram_notes.data.remote.model.PatchNotesRequest
import com.yuaatn.instagram_notes.data.remote.model.UidResponse
import com.yuaatn.instagram_notes.data.remote.util.ResultWrapper
import com.yuaatn.instagram_notes.model.Note
import kotlinx.coroutines.delay
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import javax.inject.Inject

private const val MAX_RETRIES = 3
private const val INITIAL_RETRY_DELAY_MS = 1000L
private const val MAX_RETRY_DELAY_MS = 10000L

internal class RemoteRepositoryImpl @Inject constructor(
    private val api: NotesApi,
) : RemoteRepository {

    private var currentRevision: Int = 0
    private val modificationQueue = mutableListOf<suspend () -> ResultWrapper<*>>()
    private var isProcessingQueue = false

    override suspend fun fetchNotes(): ResultWrapper<List<Note>> =
        executeWithRetry {
            api.fetchNotes().let { response ->
                currentRevision = response.revision
                ResultWrapper.Success(response.list.map { it.toDomain() })
            }
        }

    override suspend fun fetchNoteByUid(uid: String): ResultWrapper<Note> =
        executeWithRetry {
            api.fetchNoteByUid(uid).let { response ->
                currentRevision = response.revision
                ResultWrapper.Success(response.element.toDomain())
            }
        }

    override suspend fun createNote(note: Note): ResultWrapper<UidResponse> =
        executeModificationWithRetry {
            api.createNote(
                revision = currentRevision,
                request = ElementNoteRequest(
                    element = note.toDto()
                )
            ).let { response ->
                currentRevision = response.revision
                ResultWrapper.Success(UidResponse(response.element.id))
            }
        }

    override suspend fun updateNote(note: Note): ResultWrapper<UidResponse> =
        executeModificationWithRetry {
            try {
                api.updateNote(
                    revision = currentRevision,
                    noteUid = note.uid,
                    request = ElementNoteRequest(
                        element = note.toDto()
                    )
                ).let { response ->
                    currentRevision = response.revision
                    ResultWrapper.Success(UidResponse(response.element.id))
                }
            } catch (e: HttpException) {
                if (e.code() == 404) {
                    createNote(note)
                } else {
                    throw e
                }
            }
        }

    override suspend fun removeNoteByUid(uid: String): ResultWrapper<Unit> =
        executeModificationWithRetry {
            api.removeNoteByUid(
                revision = currentRevision,
                noteUid = uid
            ).let { response ->
                currentRevision = response.revision
                ResultWrapper.Success(Unit)
            }
        }

    override suspend fun patchNotes(notes: List<Note>): ResultWrapper<List<Note>> =
        executeModificationWithRetry {
            api.patchNotes(
                revision = currentRevision,
                request = PatchNotesRequest(notes.map { it.toDto() })
            ).let { response ->
                currentRevision = response.revision
                ResultWrapper.Success(response.list.map { it.toDomain() })
            }
        }

    override suspend fun fetchNotesWithThreshold(threshold: Int?): ResultWrapper<List<Note>> =
        executeWithRetry {
            api.fetchNotes(generateFailsThreshold = threshold).let { response ->
                currentRevision = response.revision
                ResultWrapper.Success(response.list.map { it.toDomain() })
            }
        }

    override suspend fun clearNotes() {
        fetchNotes().handleError { /* ignore */ }.let { result ->
            if (result is ResultWrapper.Success) {
                result.payload.forEach { note ->
                    removeNoteByUid(note.uid).handleError { /* ignore */ }
                }
            }
        }
    }

    private suspend fun <T> executeWithRetry(
        block: suspend () -> ResultWrapper<T>,
    ): ResultWrapper<T> {
        var attempt = 0
        var lastError: Throwable? = null
        var nextDelay = INITIAL_RETRY_DELAY_MS

        while (attempt < MAX_RETRIES) {
            try {
                return block()
            } catch (e: Exception) {
                lastError = e
                if (shouldRetry(e)) {
                    attempt++
                    delay(nextDelay)
                    nextDelay = (nextDelay * 2).coerceAtMost(MAX_RETRY_DELAY_MS)
                } else {
                    return ResultWrapper.Error(e)
                }
            }
        }
        return ResultWrapper.Error(lastError ?: Exception("Unknown error"))
    }

    @Suppress("UNCHECKED_CAST")
    private suspend fun <T> executeModificationWithRetry(
        block: suspend () -> ResultWrapper<T>,
    ): ResultWrapper<T> {
        modificationQueue.add(block)
        if (!isProcessingQueue) {
            isProcessingQueue = true
            processModificationQueue()
        }
        return ResultWrapper.Success(Unit) as ResultWrapper<T>
    }

    private suspend fun processModificationQueue() {
        while (modificationQueue.isNotEmpty()) {
            val operation = modificationQueue.first()
            var attempt = 0
            var success = false
            var nextDelay = INITIAL_RETRY_DELAY_MS

            while (attempt < MAX_RETRIES && !success) {
                try {
                    fetchNotes()
                    operation.invoke()
                    success = true
                    modificationQueue.removeAt(0)
                } catch (e: Exception) {
                    if (shouldRetryModification(e)) {
                        attempt++
                        delay(nextDelay)
                        nextDelay = (nextDelay * 2).coerceAtMost(MAX_RETRY_DELAY_MS)
                    } else {
                        modificationQueue.removeAt(0)
                        break
                    }
                }
            }
        }
        isProcessingQueue = false
    }

    private fun shouldRetry(e: Exception): Boolean = when (e) {
        is SocketTimeoutException -> true
        is HttpException -> e.code() in 500..599 || e.code() == 429
        is IOException -> true
        else -> false
    }

    private fun shouldRetryModification(e: Exception): Boolean = when (e) {
        is HttpException -> e.code() in 500..599 || e.code() == 429 || e.code() == 409
        else -> shouldRetry(e)
    }
}