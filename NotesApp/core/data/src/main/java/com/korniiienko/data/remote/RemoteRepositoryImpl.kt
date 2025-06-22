package com.korniiienko.data.remote

import com.korniiienko.data.remote.mappers.toModel
import com.korniiienko.data.remote.mappers.toRemoteDto
import com.korniiienko.model.remote.UidModel
import com.korniiienko.data.remote.model.PatchNotesRequest
import com.korniiienko.data.remote.model.SingleNoteRequest
import com.korniiienko.data.remote.util.DeviceProvider
import com.korniiienko.domain.RemoteRepository
import com.korniiienko.model.Note
import com.korniiienko.model.remote.NetworkError
import com.korniiienko.model.remote.NetworkResult
import org.slf4j.LoggerFactory
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import javax.net.ssl.SSLHandshakeException

class RemoteRepositoryImpl(
    private val api: RemoteApiService,
    private val deviceProvider: DeviceProvider
) : RemoteRepository {
    private val log = LoggerFactory.getLogger("NotesRemoteRepo")
    private val deviceId = deviceProvider.getDeviceId()
    
    private var syncVersion: Int = 0

    override suspend fun getNotes(): NetworkResult<List<Note>> {
        return try {
            log.trace("Initiating notes synchronization")
            val response = api.getNotes()
            syncVersion = response.revision
            log.debug("Synchronized ${response.list.size} notes (v$syncVersion)")
            NetworkResult.Success(response.list.map { it.toModel() })
        } catch (e: Exception) {
            log.warn("Notes synchronization failed", e)
            NetworkResult.Failure(e.toCustomError())
        }
    }

    override suspend fun getNote(noteUid: String): NetworkResult<Note> {
        return try {
            log.trace("Requesting note [$noteUid]")
            val response = api.getNote(noteUid = noteUid)
            syncVersion = response.revision
            log.debug("Retrieved note [${response.element.id}] (v$syncVersion)")
            NetworkResult.Success(response.element.toModel())
        } catch (e: Exception) {
            log.warn("Failed to retrieve note [$noteUid]", e)
            NetworkResult.Failure(e.toCustomError())
        }
    }

    override suspend fun addNote(note: Note): NetworkResult<UidModel> {
        return try {
            val currentVersion = api.getNotes().revision
            log.trace("Creating new note [${note.title}]")

            val response = api.addNote(
                revision = currentVersion,
                request = SingleNoteRequest(note.toRemoteDto(deviceId))
            )
            syncVersion = response.revision
            log.debug("Created note [${response.element.id}] (v$syncVersion)")
            NetworkResult.Success(UidModel(response.element.id))
        } catch (e: Exception) {
            log.warn("Failed to create note [${note.uid}]", e)
            NetworkResult.Failure(e.toCustomError())
        }
    }

    override suspend fun updateNote(note: Note): NetworkResult<UidModel> {
        return try {
            val currentVersion = api.getNotes().revision
            log.trace("Updating note [${note.uid}]")

            try {
                val response = api.updateNote(
                    revision = currentVersion,
                    noteUid = note.uid,
                    request = SingleNoteRequest(note.toRemoteDto(deviceId))
                )

                syncVersion = response.revision
                log.debug("Updated note [${response.element.id}] (v$syncVersion)")
                NetworkResult.Success(UidModel(response.element.id))
            } catch (e: HttpException) {
                if (e.code() == 404) {
                    log.info("Note not found, creating new entry")
                    addNote(note)
                } else {
                    throw e
                }
            }
        } catch (e: Exception) {
            log.warn("Failed to update note [${note.uid}]", e)
            NetworkResult.Failure(e.toCustomError())
        }
    }

    override suspend fun deleteNote(uid: String): NetworkResult<Unit> {
        return try {
            log.trace("Removing note [$uid]")
            val currentVersion = api.getNotes().revision
            val response = api.deleteNote(revision = currentVersion, noteUid = uid)
            syncVersion = response.revision
            log.debug("Removed note [$uid] (v$syncVersion)")
            NetworkResult.Success(Unit)
        } catch (e: Exception) {
            log.warn("Failed to remove note [$uid]", e)
            NetworkResult.Failure(e.toCustomError())
        }
    }

    override suspend fun patchNotes(notes: List<Note>): NetworkResult<List<Note>> {
        return try {
            log.trace("Bulk updating ${notes.size} notes")
            val currentVersion = api.getNotes().revision
            val response = api.patchNotes(
                revision = currentVersion,
                request = PatchNotesRequest(notes.map { it.toRemoteDto(deviceId) })
            )
            syncVersion = response.revision
            log.debug("Bulk update completed (v$syncVersion)")
            NetworkResult.Success(response.list.map { it.toModel() })
        } catch (e: Exception) {
            log.warn("Bulk update failed", e)
            NetworkResult.Failure(e.toCustomError())
        }
    }

    override suspend fun getNotesWithThreshold(threshold: Int?): NetworkResult<List<Note>> {
        return try {
            log.trace("Fetching with failure threshold: $threshold")
            val response = api.getNotes(generateFailsThreshold = threshold)
            syncVersion = response.revision
            log.debug("Threshold fetch completed (v$syncVersion)")
            NetworkResult.Success(response.list.map { it.toModel() })
        } catch (e: Exception) {
            log.warn("Threshold fetch failed", e)
            NetworkResult.Failure(e.toCustomError())
        }
    }

    override suspend fun clearAllNotes() {
        try {
            var currentVersion = api.getNotes().revision
            api.getNotes().list.forEach { note ->
                val response = api.deleteNote(
                    revision = currentVersion,
                    noteUid = note.id
                )
                currentVersion = response.revision
            }
            log.info("All notes cleared")
        } catch (e: Exception) {
            log.error("Failed to clear all notes", e)
        }
    }

    private fun Throwable.toCustomError(): Throwable {
        return when (this) {
            is SocketTimeoutException -> NetworkError("Connection timeout", this)
            is SSLHandshakeException -> NetworkError("Security error", this)
            is IOException -> NetworkError("Network unavailable", this)
            is HttpException -> when (code()) {
                400 -> NetworkError("Invalid request", this)
                401 -> NetworkError("Authentication required", this)
                404 -> NetworkError("Resource not found", this)
                409 -> NetworkError("Version conflict", this)
                413 -> NetworkError("Data too large", this)
                429 -> NetworkError("Too many attempts", this)
                in 500..599 -> NetworkError("Server issue", this)
                else -> NetworkError("HTTP error ${code()}", this)
            }
            else -> NetworkError("Unexpected error ${this.localizedMessage}", this)
        }
    }
}