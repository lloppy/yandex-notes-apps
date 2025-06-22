package com.yuaatn.instagram_notes.data.local.room.mappers

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.yuaatn.instagram_notes.model.Importance
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@ProvidedTypeConverter
class Converters {

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
            Importance.NORMAL
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