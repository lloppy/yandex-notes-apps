package com.korniiienko.data.data

import android.content.Context
import android.os.Build
import androidx.test.core.app.ApplicationProvider
import com.korniiienko.data.local.json.FileNotebook
import com.korniiienko.data.local.json.JsonLocalRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest 
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File

@Config(sdk = [Build.VERSION_CODES.TIRAMISU])
@ExperimentalCoroutinesApi
@RunWith(RobolectricTestRunner::class)
class JsonLocalRepositoryTest {
    private val testDispatcher = TestCoroutineDispatcher()

    private lateinit var context: Context
    private lateinit var fileNotebook: FileNotebook
    private lateinit var repository: JsonLocalRepository

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        context = ApplicationProvider.getApplicationContext()
        fileNotebook = FileNotebook(context)
        repository = JsonLocalRepository(context)

        File(context.filesDir, "notes.json").delete()
    }

    @After
    fun tearDown() {
        fileNotebook.close()
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines()
    }

    @Test
    fun `add note should save to file`() = runTest  {
        val note = com.korniiienko.model.Note.create("Test", "Content")
        repository.addNote(note)
        repository.save()

        // Проверяем, что файл существует и не пустой
        val file = File(context.filesDir, "notes.json")
        assertTrue(file.exists())
        assertTrue(file.readText().isNotEmpty())
    }

    @Test
    fun `load should read from file`() = runTest  {
        val note = com.korniiienko.model.Note.create("Test", "Content")
        repository.addNote(note)
        repository.save()

        // Очищаем текущие данные
        fileNotebook.deleteNote(note.uid)

        // Загружаем из файла
        repository.load()
        val loadedNote = repository.getNoteByUid(note.uid)

        assertNotNull(loadedNote)
        assertEquals(note.title, loadedNote.first().title)
    }

    @Test
    fun `update note should modify file`() = runTest  {
        val note = com.korniiienko.model.Note.create("Test", "Content")
        repository.addNote(note)
        repository.save()
        testScheduler.runCurrent()

        val updatedNote = note.copy(title = "Updated")
        repository.updateNote(updatedNote)
        repository.save()
        testScheduler.runCurrent()

        val newRepo = JsonLocalRepository(context)
        newRepo.load()
        testScheduler.runCurrent()

        val loadedNote = newRepo.getNoteByUid(note.uid)
        assertEquals("Updated", loadedNote.first().title)
    }

    @Test
    fun `notes flow should emit current state`() = runTest  {
        val note = com.korniiienko.model.Note.create("Test", "Content")
        repository.addNote(note)

        val notes = repository.notes.first()
        assertEquals(1, notes.size)
        assertEquals(note.uid, notes[0].uid)
    }
}

@ExperimentalCoroutinesApi
@RunWith(RobolectricTestRunner::class)
class FileNotebookTest {
    private val testDispatcher = TestCoroutineDispatcher()
    private lateinit var context: Context
    private lateinit var fileNotebook: FileNotebook

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        context = ApplicationProvider.getApplicationContext()
        fileNotebook = FileNotebook(context)

        // Очищаем файл перед каждым тестом
        File(context.filesDir, "notes.json").delete()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines()
    }

    @Test
    fun `saveToFile should create file with notes`() = runTest  {
        val note = com.korniiienko.model.Note.create("Test", "Content")
        fileNotebook.addNote(note)
        fileNotebook.saveToFile()

        val file = File(context.filesDir, "notes.json")
        assertTrue(file.exists())
        assertTrue(file.readText().contains(note.title))
    }

    @Test
    fun `loadFromFile should restore notes`() = runTest  {
        val note = com.korniiienko.model.Note.create("Test", "Content")
        fileNotebook.addNote(note)
        fileNotebook.saveToFile()

        // Очищаем текущие данные
        fileNotebook.deleteNote(note.uid)

        // Загружаем из файла
        fileNotebook.loadFromFile()

        runBlocking {
            val notes = fileNotebook.notes.first()
            assertEquals(1, notes.size)
            assertEquals(note.title, notes[0].title)
        }
    }

    @Test
    fun `loadFromFile should handle empty file`() = runTest  {
        // Создаем пустой файл
        File(context.filesDir, "notes.json").createNewFile()

        fileNotebook.loadFromFile()

        runBlocking {
            val notes = fileNotebook.notes.first()
            assertTrue(notes.isEmpty())
        }
    }

    @Test
    fun `loadFromFile should handle missing file`() = runTest  {
        // Убедимся, что файла нет
        File(context.filesDir, "notes.json").delete()

        fileNotebook.loadFromFile()

        runBlocking {
            val notes = fileNotebook.notes.first()
            assertTrue(notes.isEmpty())
        }
    }

    @Test
    fun `addNote should update flow`() = runTest  {
        val note = com.korniiienko.model.Note.create("Test", "Content")
        fileNotebook.addNote(note)

        runBlocking {
            val notes = fileNotebook.notes.first()
            assertEquals(1, notes.size)
            assertEquals(note.uid, notes[0].uid)
        }
    }

    @Test
    fun `updateNote should modify existing note`() = runTest  {
        val note = com.korniiienko.model.Note.create("Test", "Content")
        fileNotebook.addNote(note)

        val updatedNote = note.copy(title = "Updated")
        fileNotebook.updateNote(updatedNote)

        runBlocking {
            val notes = fileNotebook.notes.first()
            assertEquals("Updated", notes[0].title)
        }
    }

    @Test
    fun `deleteNote should remove note`() = runTest  {
        val note = com.korniiienko.model.Note.create("Test", "Content")
        fileNotebook.addNote(note)
        fileNotebook.deleteNote(note.uid)

        runBlocking {
            val notes = fileNotebook.notes.first()
            assertTrue(notes.isEmpty())
        }
    }
}