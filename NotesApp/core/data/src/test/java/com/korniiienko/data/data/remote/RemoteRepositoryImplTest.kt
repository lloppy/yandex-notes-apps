package com.korniiienko.data.data.remote

import android.graphics.Color
import com.korniiienko.data.remote.model.NoteItemDto
import com.korniiienko.data.remote.mappers.toModel
import com.korniiienko.data.remote.mappers.toRemoteDto
import com.korniiienko.model.Importance
import com.korniiienko.model.Note
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class NoteMappingTests {

    @Test
    fun `преобразование заметки в DTO сохраняет все параметры`() {
        // Подготовка тестовых данных
        val testNote = Note(
            uid = "test-uid-123",
            title = "Тестовая заметка",
            content = "Содержание заметки",
            color = Color.RED,
            importance = Importance.IMPORTANT,
            expirationDate = 1672531200L, // 1 января 2023
            createdAt = 1672444800L, // 31 декабря 2022
            updatedAt = 1672448400L // 31 декабря 2022 01:00
        )
        val deviceIdentifier = "mobile-xyz"

        // Выполнение преобразования
        val resultDto = testNote.toRemoteDto(deviceIdentifier)

        // Проверка результатов
        assertEquals(testNote.uid, resultDto.id)
        assertEquals(testNote.title, resultDto.text)
        assertEquals("important", resultDto.importance)
        assertEquals(testNote.expirationDate, resultDto.deadline)
        assertEquals(false, resultDto.done)
        assertEquals(testNote.createdAt, resultDto.createdAt)
        assertEquals(testNote.updatedAt, resultDto.changedAt)
        assertEquals(deviceIdentifier, resultDto.lastUpdatedBy)
        assertEquals("#FF0000", resultDto.color)
    }

    @Test
    fun `преобразование заметки с дефолтными значениями в DTO`() {
        // Подготовка минимальной заметки
        val simpleNote = Note(
            title = "Простая заметка",
            content = "Без особых параметров"
        )
        val deviceId = "tablet-456"

        // Выполнение преобразования
        val resultDto = simpleNote.toRemoteDto(deviceId)

        // Проверки
        assertNotNull(resultDto.id)
        assertEquals("Простая заметка", resultDto.text)
        assertEquals("basic", resultDto.importance)
        assertNull(resultDto.deadline)
        assertEquals(false, resultDto.done)
        assertNotNull(resultDto.createdAt)
        assertNotNull(resultDto.changedAt)
        assertEquals(deviceId, resultDto.lastUpdatedBy)
        assertNull(resultDto.color)
    }

    @Test
    fun `преобразование DTO в доменную модель заметки`() {
        // Подготовка DTO объекта
        val remoteNote = NoteItemDto(
            id = "remote-note-001",
            text = "Удаленная заметка",
            importance = Importance.IMPORTANT.name,
            deadline = 1672617600L, // 2 января 2023
            done = false,
            createdAt = 1672531200L, // 1 января 2023
            changedAt = 1672534800L, // 1 января 2023 01:00
            lastUpdatedBy = "desktop-789",
            color = "#00FF00"
        )

        // Выполнение преобразования
        val domainNote = remoteNote.toModel()

        // Проверки
        assertEquals(remoteNote.id, domainNote.uid)
        assertEquals(remoteNote.text, domainNote.title)
        assertEquals("", domainNote.content) // Описание не передается
        assertEquals(Importance.IMPORTANT, domainNote.importance)
        assertEquals(Color.GREEN, domainNote.color)
        assertEquals(remoteNote.deadline, domainNote.expirationDate)
        assertEquals(remoteNote.createdAt, domainNote.createdAt)
        assertEquals(remoteNote.changedAt, domainNote.updatedAt)
    }

    @Test
    fun `обработка различных уровней важности при преобразовании`() {
        // Проверка всех вариантов важности
        val lowPriorityDto = NoteItemDto(
            id = "note-1", text = "Низкая", importance = Importance.LOW.name, done = false,
            createdAt = 1L, changedAt = 1L, lastUpdatedBy = "device"
        )
        assertEquals(Importance.LOW, lowPriorityDto.toModel().importance)

        val highPriorityDto = NoteItemDto(
            id = "note-2", text = "Высокая", importance = Importance.IMPORTANT.name, done = false,
            createdAt = 1L, changedAt = 1L, lastUpdatedBy = "device"
        )
        assertEquals(Importance.IMPORTANT, highPriorityDto.toModel().importance)

        val invaluidPriorityDto = NoteItemDto(
            id = "note-3", text = "Неизвестная", importance = "invaluid", done = false,
            createdAt = 1L, changedAt = 1L, lastUpdatedBy = "device"
        )
        assertEquals(Importance.BASIC, invaluidPriorityDto.toModel().importance)
    }

    @Test
    fun `обработка некорректного цветового кода`() {
        // DTO с невалидным цветом
        val invaluidColorNote = NoteItemDto(
            id = "color-test",
            text = "Тест цвета",
            importance = Importance.BASIC.name,
            done = false,
            createdAt = 1L,
            changedAt = 1L,
            lastUpdatedBy = "device",
            color = "not-a-color"
        )

        // Должен вернуться белый цвет по умолчанию
        assertEquals(Color.WHITE, invaluidColorNote.toModel().color)
    }

    @Test
    fun `корректное преобразование цветов в hex-строку`() {
        // Проверка разных цветов
        val blueNote = Note(title = "Синяя", content = "empty", color = Color.BLUE)
        assertEquals("#0000FF", blueNote.toRemoteDto("device").color)

        val yellowNote = Note(title = "Желтая", content = "empty", color = Color.YELLOW)
        assertEquals("#FFFF00", yellowNote.toRemoteDto("device").color)

        // Белый цвет должен преобразовываться в null
        val whiteNote = Note(title = "Белая", content = "empty", color = Color.WHITE)
        assertNull(whiteNote.toRemoteDto("device").color)
    }
}