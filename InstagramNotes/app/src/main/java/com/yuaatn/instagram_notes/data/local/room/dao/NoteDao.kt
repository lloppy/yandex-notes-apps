package com.yuaatn.instagram_notes.data.local.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.yuaatn.instagram_notes.data.local.room.entity.NoteEntity
import com.yuaatn.instagram_notes.model.Importance
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface NoteDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(noteEntity: NoteEntity)

    @Query("UPDATE notes SET title = :title, content = :content, color = :color, importance = :importance, deadline = :deadline, changed_at = :changedAt WHERE uid = :uid")
    suspend fun updateByUid(
        uid: String,
        title: String,
        content: String,
        color: Int,
        importance: Importance,
        deadline: Long?,
        changedAt: Long? = Date().time
    ): Int

    @Delete
    suspend fun delete(noteEntity: NoteEntity)

    @Query("DELETE FROM notes WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("DELETE FROM notes WHERE uid = :uid")
    suspend fun deleteByUid(uid: String)

    @Query("SELECT * FROM notes WHERE id = :id")
    fun getById(id: Int): Flow<NoteEntity>

    @Query("SELECT * FROM notes WHERE uid = :uid")
    fun getByUid(uid: String): Flow<NoteEntity?>

    @Query("SELECT * FROM notes")
    fun getAll(): Flow<List<NoteEntity>>

}