package com.korniiienko.data.local.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.korniiienko.data.local.room.model.NoteEntity
import com.korniiienko.model.Importance
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(noteEntity: NoteEntity)

    @Query("UPDATE notes SET title = :title, content = :content, color = :color, importance = :importance, self_destruct_date = :selfDestructDate WHERE uid = :uid")
    suspend fun updateByUid(
        uid: String,
        title: String,
        content: String,
        color: Int,
        importance: Importance,
        selfDestructDate: Long?,
    ): Int

    @Delete
    suspend fun delete(noteEntity: NoteEntity)

    @Query("DELETE FROM notes WHERE uid = :uid")
    suspend fun deleteByUid(uid: String)

    @Query("DELETE FROM notes")
    suspend fun deleteAll()

    @Query("SELECT * FROM notes WHERE uid = :uid")
    fun getByUid(uid: String): Flow<NoteEntity?>

    @Query("SELECT * FROM notes")
    fun getAll(): Flow<List<NoteEntity>>

}