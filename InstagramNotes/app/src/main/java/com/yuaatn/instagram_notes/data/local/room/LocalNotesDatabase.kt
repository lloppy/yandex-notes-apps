package com.yuaatn.instagram_notes.data.local.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.yuaatn.instagram_notes.data.local.room.dao.NoteDao
import com.yuaatn.instagram_notes.data.local.room.entity.NoteEntity
import com.yuaatn.instagram_notes.data.local.room.mappers.Converters

@Database(entities = [NoteEntity::class], version = 1)
@TypeConverters(Converters::class)
abstract class LocalNotesDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao

    companion object {
        @Volatile
        private var INSTANCE: LocalNotesDatabase? = null

        fun getDatabase(context: Context): LocalNotesDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context = context,
                    klass = LocalNotesDatabase::class.java,
                    name = "note_database"
                )
                    .addTypeConverter(Converters())
                    .build()
                    .also {
                        INSTANCE = it
                    }
            }
        }
    }
}