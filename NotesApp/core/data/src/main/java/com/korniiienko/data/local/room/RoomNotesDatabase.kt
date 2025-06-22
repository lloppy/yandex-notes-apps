package com.korniiienko.data.local.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.korniiienko.data.local.room.mappers.NoteConverter
import com.korniiienko.data.local.room.model.NoteEntity

@Database(entities = [NoteEntity::class], version = 1, exportSchema = false)
@TypeConverters(NoteConverter::class)
abstract class RoomNotesDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao

    companion object {
        @Volatile
        private var INSTANCE: RoomNotesDatabase? = null

        fun getDatabase(context: Context): RoomNotesDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context = context,
                    klass = RoomNotesDatabase::class.java,
                    name = "note_database"
                )
                    .addTypeConverter(NoteConverter())
                    .fallbackToDestructiveMigration()
                    .build()
                    .also {
                        INSTANCE = it
                    }
            }
        }
    }
}