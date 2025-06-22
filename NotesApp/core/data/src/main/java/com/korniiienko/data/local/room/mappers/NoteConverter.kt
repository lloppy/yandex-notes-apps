package com.korniiienko.data.local.room.mappers

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.korniiienko.model.Importance
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@ProvidedTypeConverter
class NoteConverter {

    @RequiresApi(Build.VERSION_CODES.O)
    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

    @TypeConverter
    fun noteImportanceToString(importance: Importance): String {
        return importance.name
    }

    @TypeConverter
    fun stringToNoteImportance(importanceName: String): Importance {
        return try {
            Importance.valueOf(importanceName)
        } catch (e: IllegalArgumentException) {
            Importance.BASIC
        }
    }

    @TypeConverter
    fun fromLocalDateTime(value: LocalDateTime?): String? {
        return value?.format(formatter)
    }

    @TypeConverter
    fun toLocalDateTime(value: String?): LocalDateTime? {
        return value?.let { LocalDateTime.parse(it, formatter) }
    }
}