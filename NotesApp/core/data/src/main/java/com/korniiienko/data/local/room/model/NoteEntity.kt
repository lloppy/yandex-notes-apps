package com.korniiienko.data.local.room.model

import android.graphics.Color
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.korniiienko.data.local.room.mappers.NoteConverter
import com.korniiienko.model.Importance

@Entity(tableName = "notes")
@TypeConverters(NoteConverter::class)
data class NoteEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val uid: String = "",
    val title: String = "",
    val content: String = "",
    val color: Int = Color.WHITE,
    val importance: Importance = Importance.BASIC,

    @ColumnInfo("self_destruct_date")
    val selfDestructDate: Long? = null,
)