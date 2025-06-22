package com.yuaatn.instagram_notes.data.local.room.entity

import android.graphics.Color
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.yuaatn.instagram_notes.data.local.room.mappers.Converters
import com.yuaatn.instagram_notes.model.Importance

@Entity(tableName = "notes")
@TypeConverters(Converters::class)
data class NoteEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val uid: String = "",

    val title: String = "",
    val content: String = "",
    val importance: Importance = Importance.NORMAL,
    val color: Int = Color.WHITE,

    val deadline: Long? = null,
    @ColumnInfo("created_at")
    val createdAt: Long? = null,
    @ColumnInfo("changed_at")
    val changedAt: Long? = null,
    @ColumnInfo("device_id")
    val deviceId: String = "myDeviceId_123"
)
