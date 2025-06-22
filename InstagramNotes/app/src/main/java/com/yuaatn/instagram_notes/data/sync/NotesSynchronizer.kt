package com.yuaatn.instagram_notes.data.sync

import com.yuaatn.instagram_notes.data.local.LocalRepository
import com.yuaatn.instagram_notes.data.remote.RemoteRepository
import com.yuaatn.instagram_notes.data.remote.util.ResultWrapper
import com.yuaatn.instagram_notes.model.Note
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import org.slf4j.LoggerFactory
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotesSynchronizer @Inject constructor(
    private val localRepository: LocalRepository,
    private val remoteRepository: RemoteRepository
) : SyncManager, ConflictResolver {

    private val logger = LoggerFactory.getLogger(NotesSynchronizer::class.java)

    override val notes: Flow<List<Note>> = flow {
        val localNotes = localRepository.notes.first()
        if (localNotes.isEmpty()) {
            logger.info("Local notes empty, fetching from server...")
            synchronize()
        }
        emitAll(localRepository.notes)
    }

    override suspend fun synchronize() {
        logger.info("Starting synchronization process")

        try {
            logger.debug("Fetching remote notes...")
            when (val remoteResult = remoteRepository.fetchNotes()) {
                is ResultWrapper.Success -> {
                    logger.info("Successfully fetched ${remoteResult.payload.size} remote notes")
                    val remoteNotes = remoteResult.payload

                    localRepository.updateNotes(remoteNotes)

                    logger.info("Local notes updated with server data")
                }
                is ResultWrapper.Error -> {
                    logger.warn("Failed to fetch remote notes: ${remoteResult.exception.message}")
                    pushLocalChangesToServer()
                }
            }
        } catch (e: Exception) {
            logger.error("Synchronization failed with error: ${e.message}", e)
            throw e
        } finally {
            logger.info("Synchronization process completed")
        }
    }

    override suspend fun getNoteByUid(uid: String): Flow<Note?> {
        val localNoteFlow = localRepository.getNoteByUid(uid)
        val localNote = localNoteFlow.first()

        return if (localNote != null) {
            localNoteFlow
        } else {
            when (val remoteResult = remoteRepository.fetchNoteByUid(uid)) {
                is ResultWrapper.Success -> {
                    val remoteNote = remoteResult.payload
                    localRepository.addNote(remoteNote)
                    localRepository.getNoteByUid(uid)
                }
                is ResultWrapper.Error -> {
                    logger.warn("Note with UID=$uid not found on server: ${remoteResult.exception.message}")
                    kotlinx.coroutines.flow.flowOf(null)
                }
            }
        }
    }

    override suspend fun resolveConflicts(localNotes: List<Note>, remoteNotes: List<Note>) {
        logger.debug("Resolving conflicts between local and remote notes")

        val localNotesMap = localNotes.associateBy { it.uid }
        val remoteNotesMap = remoteNotes.associateBy { it.uid }

        var deletedCount = 0
        var addedCount = 0
        var updatedCount = 0

        localNotes.forEach { localNote ->
            if (!remoteNotesMap.containsKey(localNote.uid)) {
                logger.debug("Deleting local note (UID: ${localNote.uid}) as it doesn't exist on server")
                localRepository.deleteNote(localNote.uid)
                deletedCount++
            }
        }

        remoteNotes.forEach { remoteNote ->
            val localNote = localNotesMap[remoteNote.uid]

            if (localNote == null) {
                logger.debug("Adding new note from server (UID: ${remoteNote.uid})")
                localRepository.addNote(remoteNote)
                addedCount++
            } else {
                if (localNote != remoteNote) {
                    logger.debug("Updating local note (UID: ${remoteNote.uid}) with server version")
                    localRepository.updateNote(remoteNote)
                    updatedCount++
                }
            }
        }

        logger.info("Conflict resolution completed: $deletedCount deleted, $addedCount added, $updatedCount updated")
    }

    private suspend fun pushLocalChangesToServer() {
        val localNotes = localRepository.notes.first()
        logger.info("Pushing ${localNotes.size} local notes to server")

        localNotes.forEachIndexed { index, note ->
            logger.debug("Processing note ${index + 1}/${localNotes.size} (UID: ${note.uid})")
            when (val result = remoteRepository.updateNote(note)) {
                is ResultWrapper.Success -> {
                    val uidResponse = result.payload
                    logger.debug("Successfully updated note on server (UID: ${uidResponse.uid})")
                }
                is ResultWrapper.Error -> {
                    logger.error("Failed to update note on server (UID: ${note.uid}): ${result.exception.message}")
                }
            }
        }
    }

    override suspend fun syncOnCreate(note: Note) {
        logger.info("Syncing new note creation (UID: ${note.uid})")
        remoteRepository.createNote(note).handleResult("create")
        localRepository.addNote(note)
    }

    override suspend fun syncOnUpdate(note: Note) {
        logger.info("Syncing note update (UID: ${note.uid})")
        remoteRepository.updateNote(note).handleResult("update")
        localRepository.updateNote(note)
    }

    override suspend fun syncOnDelete(uid: String) {
        logger.info("Syncing note deletion (UID: $uid)")
        remoteRepository.removeNoteByUid(uid).handleResult("delete")
        localRepository.deleteNote(uid)
    }

    private suspend fun <T> ResultWrapper<T>.handleResult(operation: String) {
        when (this) {
            is ResultWrapper.Success -> {
                logger.debug("$operation operation succeeded")
            }
            is ResultWrapper.Error -> {
                logger.warn("$operation operation failed with message: ${exception.message}")
                logger.info("Triggering full synchronization due to failure")
                synchronize()
            }
        }
    }
}