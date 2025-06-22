package com.example.`data`.local.dao

import android.database.Cursor
import androidx.room.CoroutinesRoom
import androidx.room.EntityDeletionOrUpdateAdapter
import androidx.room.EntityInsertionAdapter
import androidx.room.RoomDatabase
import androidx.room.RoomSQLiteQuery
import androidx.room.RoomSQLiteQuery.Companion.acquire
import androidx.room.SharedSQLiteStatement
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.query
import androidx.sqlite.db.SupportSQLiteStatement
import com.example.`data`.local.entity.NoteEntity
import com.example.`data`.local.mappers.Converters
import com.example.model.Importance
import java.lang.Class
import java.util.ArrayList
import java.util.concurrent.Callable
import javax.`annotation`.processing.Generated
import kotlin.Int
import kotlin.Lazy
import kotlin.Long
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.List
import kotlin.collections.MutableList
import kotlin.jvm.JvmStatic
import kotlinx.coroutines.flow.Flow

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION"])
public class NoteDao_Impl(
  __db: RoomDatabase,
) : NoteDao {
  private val __db: RoomDatabase

  private val __insertionAdapterOfNoteEntity: EntityInsertionAdapter<NoteEntity>

  private val __converters: Lazy<Converters> = lazy {
    checkNotNull(__db.getTypeConverter(Converters::class.java))
  }


  private val __deletionAdapterOfNoteEntity: EntityDeletionOrUpdateAdapter<NoteEntity>

  private val __preparedStmtOfUpdateByUid: SharedSQLiteStatement

  private val __preparedStmtOfDeleteById: SharedSQLiteStatement

  private val __preparedStmtOfDeleteByUid: SharedSQLiteStatement

  private val __preparedStmtOfDeleteAll: SharedSQLiteStatement
  init {
    this.__db = __db
    this.__insertionAdapterOfNoteEntity = object : EntityInsertionAdapter<NoteEntity>(__db) {
      protected override fun createQuery(): String =
          "INSERT OR IGNORE INTO `notes` (`id`,`uid`,`title`,`content`,`color`,`importance`,`self_destruct_date`) VALUES (nullif(?, 0),?,?,?,?,?,?)"

      protected override fun bind(statement: SupportSQLiteStatement, entity: NoteEntity) {
        statement.bindLong(1, entity.id.toLong())
        statement.bindString(2, entity.uid)
        statement.bindString(3, entity.title)
        statement.bindString(4, entity.content)
        statement.bindLong(5, entity.color.toLong())
        val _tmp: String = __converters().noteImportanceToString(entity.importance)
        statement.bindString(6, _tmp)
        val _tmpSelfDestructDate: Long? = entity.selfDestructDate
        if (_tmpSelfDestructDate == null) {
          statement.bindNull(7)
        } else {
          statement.bindLong(7, _tmpSelfDestructDate)
        }
      }
    }
    this.__deletionAdapterOfNoteEntity = object : EntityDeletionOrUpdateAdapter<NoteEntity>(__db) {
      protected override fun createQuery(): String = "DELETE FROM `notes` WHERE `id` = ?"

      protected override fun bind(statement: SupportSQLiteStatement, entity: NoteEntity) {
        statement.bindLong(1, entity.id.toLong())
      }
    }
    this.__preparedStmtOfUpdateByUid = object : SharedSQLiteStatement(__db) {
      public override fun createQuery(): String {
        val _query: String =
            "UPDATE notes SET title = ?, content = ?, color = ?, importance = ?, self_destruct_date = ? WHERE uid = ?"
        return _query
      }
    }
    this.__preparedStmtOfDeleteById = object : SharedSQLiteStatement(__db) {
      public override fun createQuery(): String {
        val _query: String = "DELETE FROM notes WHERE id = ?"
        return _query
      }
    }
    this.__preparedStmtOfDeleteByUid = object : SharedSQLiteStatement(__db) {
      public override fun createQuery(): String {
        val _query: String = "DELETE FROM notes WHERE uid = ?"
        return _query
      }
    }
    this.__preparedStmtOfDeleteAll = object : SharedSQLiteStatement(__db) {
      public override fun createQuery(): String {
        val _query: String = "DELETE FROM notes"
        return _query
      }
    }
  }

  public override suspend fun insert(noteEntity: NoteEntity): Unit = CoroutinesRoom.execute(__db,
      true, object : Callable<Unit> {
    public override fun call() {
      __db.beginTransaction()
      try {
        __insertionAdapterOfNoteEntity.insert(noteEntity)
        __db.setTransactionSuccessful()
      } finally {
        __db.endTransaction()
      }
    }
  })

  public override suspend fun delete(noteEntity: NoteEntity): Unit = CoroutinesRoom.execute(__db,
      true, object : Callable<Unit> {
    public override fun call() {
      __db.beginTransaction()
      try {
        __deletionAdapterOfNoteEntity.handle(noteEntity)
        __db.setTransactionSuccessful()
      } finally {
        __db.endTransaction()
      }
    }
  })

  public override suspend fun updateByUid(
    uid: String,
    title: String,
    content: String,
    color: Int,
    importance: Importance,
    selfDestructDate: Long?,
  ): Int = CoroutinesRoom.execute(__db, true, object : Callable<Int> {
    public override fun call(): Int {
      val _stmt: SupportSQLiteStatement = __preparedStmtOfUpdateByUid.acquire()
      var _argIndex: Int = 1
      _stmt.bindString(_argIndex, title)
      _argIndex = 2
      _stmt.bindString(_argIndex, content)
      _argIndex = 3
      _stmt.bindLong(_argIndex, color.toLong())
      _argIndex = 4
      val _tmp: String = __converters().noteImportanceToString(importance)
      _stmt.bindString(_argIndex, _tmp)
      _argIndex = 5
      if (selfDestructDate == null) {
        _stmt.bindNull(_argIndex)
      } else {
        _stmt.bindLong(_argIndex, selfDestructDate)
      }
      _argIndex = 6
      _stmt.bindString(_argIndex, uid)
      try {
        __db.beginTransaction()
        try {
          val _result: Int = _stmt.executeUpdateDelete()
          __db.setTransactionSuccessful()
          return _result
        } finally {
          __db.endTransaction()
        }
      } finally {
        __preparedStmtOfUpdateByUid.release(_stmt)
      }
    }
  })

  public override suspend fun deleteById(id: Int): Unit = CoroutinesRoom.execute(__db, true, object
      : Callable<Unit> {
    public override fun call() {
      val _stmt: SupportSQLiteStatement = __preparedStmtOfDeleteById.acquire()
      var _argIndex: Int = 1
      _stmt.bindLong(_argIndex, id.toLong())
      try {
        __db.beginTransaction()
        try {
          _stmt.executeUpdateDelete()
          __db.setTransactionSuccessful()
        } finally {
          __db.endTransaction()
        }
      } finally {
        __preparedStmtOfDeleteById.release(_stmt)
      }
    }
  })

  public override suspend fun deleteByUid(uid: String): Unit = CoroutinesRoom.execute(__db, true,
      object : Callable<Unit> {
    public override fun call() {
      val _stmt: SupportSQLiteStatement = __preparedStmtOfDeleteByUid.acquire()
      var _argIndex: Int = 1
      _stmt.bindString(_argIndex, uid)
      try {
        __db.beginTransaction()
        try {
          _stmt.executeUpdateDelete()
          __db.setTransactionSuccessful()
        } finally {
          __db.endTransaction()
        }
      } finally {
        __preparedStmtOfDeleteByUid.release(_stmt)
      }
    }
  })

  public override suspend fun deleteAll(): Unit = CoroutinesRoom.execute(__db, true, object :
      Callable<Unit> {
    public override fun call() {
      val _stmt: SupportSQLiteStatement = __preparedStmtOfDeleteAll.acquire()
      try {
        __db.beginTransaction()
        try {
          _stmt.executeUpdateDelete()
          __db.setTransactionSuccessful()
        } finally {
          __db.endTransaction()
        }
      } finally {
        __preparedStmtOfDeleteAll.release(_stmt)
      }
    }
  })

  public override fun getById(id: Int): Flow<NoteEntity> {
    val _sql: String = "SELECT * FROM notes WHERE id = ?"
    val _statement: RoomSQLiteQuery = acquire(_sql, 1)
    var _argIndex: Int = 1
    _statement.bindLong(_argIndex, id.toLong())
    return CoroutinesRoom.createFlow(__db, false, arrayOf("notes"), object : Callable<NoteEntity> {
      public override fun call(): NoteEntity {
        val _cursor: Cursor = query(__db, _statement, false, null)
        try {
          val _cursorIndexOfId: Int = getColumnIndexOrThrow(_cursor, "id")
          val _cursorIndexOfUid: Int = getColumnIndexOrThrow(_cursor, "uid")
          val _cursorIndexOfTitle: Int = getColumnIndexOrThrow(_cursor, "title")
          val _cursorIndexOfContent: Int = getColumnIndexOrThrow(_cursor, "content")
          val _cursorIndexOfColor: Int = getColumnIndexOrThrow(_cursor, "color")
          val _cursorIndexOfImportance: Int = getColumnIndexOrThrow(_cursor, "importance")
          val _cursorIndexOfSelfDestructDate: Int = getColumnIndexOrThrow(_cursor,
              "self_destruct_date")
          val _result: NoteEntity
          if (_cursor.moveToFirst()) {
            val _tmpId: Int
            _tmpId = _cursor.getInt(_cursorIndexOfId)
            val _tmpUid: String
            _tmpUid = _cursor.getString(_cursorIndexOfUid)
            val _tmpTitle: String
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle)
            val _tmpContent: String
            _tmpContent = _cursor.getString(_cursorIndexOfContent)
            val _tmpColor: Int
            _tmpColor = _cursor.getInt(_cursorIndexOfColor)
            val _tmpImportance: Importance
            val _tmp: String
            _tmp = _cursor.getString(_cursorIndexOfImportance)
            _tmpImportance = __converters().stringToNoteImportance(_tmp)
            val _tmpSelfDestructDate: Long?
            if (_cursor.isNull(_cursorIndexOfSelfDestructDate)) {
              _tmpSelfDestructDate = null
            } else {
              _tmpSelfDestructDate = _cursor.getLong(_cursorIndexOfSelfDestructDate)
            }
            _result =
                NoteEntity(_tmpId,_tmpUid,_tmpTitle,_tmpContent,_tmpColor,_tmpImportance,_tmpSelfDestructDate)
          } else {
            error("The query result was empty, but expected a single row to return a NON-NULL object of type <com.example.`data`.local.entity.NoteEntity>.")
          }
          return _result
        } finally {
          _cursor.close()
        }
      }

      protected fun finalize() {
        _statement.release()
      }
    })
  }

  public override fun getByUid(uid: String): Flow<NoteEntity?> {
    val _sql: String = "SELECT * FROM notes WHERE uid = ?"
    val _statement: RoomSQLiteQuery = acquire(_sql, 1)
    var _argIndex: Int = 1
    _statement.bindString(_argIndex, uid)
    return CoroutinesRoom.createFlow(__db, false, arrayOf("notes"), object : Callable<NoteEntity?> {
      public override fun call(): NoteEntity? {
        val _cursor: Cursor = query(__db, _statement, false, null)
        try {
          val _cursorIndexOfId: Int = getColumnIndexOrThrow(_cursor, "id")
          val _cursorIndexOfUid: Int = getColumnIndexOrThrow(_cursor, "uid")
          val _cursorIndexOfTitle: Int = getColumnIndexOrThrow(_cursor, "title")
          val _cursorIndexOfContent: Int = getColumnIndexOrThrow(_cursor, "content")
          val _cursorIndexOfColor: Int = getColumnIndexOrThrow(_cursor, "color")
          val _cursorIndexOfImportance: Int = getColumnIndexOrThrow(_cursor, "importance")
          val _cursorIndexOfSelfDestructDate: Int = getColumnIndexOrThrow(_cursor,
              "self_destruct_date")
          val _result: NoteEntity?
          if (_cursor.moveToFirst()) {
            val _tmpId: Int
            _tmpId = _cursor.getInt(_cursorIndexOfId)
            val _tmpUid: String
            _tmpUid = _cursor.getString(_cursorIndexOfUid)
            val _tmpTitle: String
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle)
            val _tmpContent: String
            _tmpContent = _cursor.getString(_cursorIndexOfContent)
            val _tmpColor: Int
            _tmpColor = _cursor.getInt(_cursorIndexOfColor)
            val _tmpImportance: Importance
            val _tmp: String
            _tmp = _cursor.getString(_cursorIndexOfImportance)
            _tmpImportance = __converters().stringToNoteImportance(_tmp)
            val _tmpSelfDestructDate: Long?
            if (_cursor.isNull(_cursorIndexOfSelfDestructDate)) {
              _tmpSelfDestructDate = null
            } else {
              _tmpSelfDestructDate = _cursor.getLong(_cursorIndexOfSelfDestructDate)
            }
            _result =
                NoteEntity(_tmpId,_tmpUid,_tmpTitle,_tmpContent,_tmpColor,_tmpImportance,_tmpSelfDestructDate)
          } else {
            _result = null
          }
          return _result
        } finally {
          _cursor.close()
        }
      }

      protected fun finalize() {
        _statement.release()
      }
    })
  }

  public override fun getAll(): Flow<List<NoteEntity>> {
    val _sql: String = "SELECT * FROM notes"
    val _statement: RoomSQLiteQuery = acquire(_sql, 0)
    return CoroutinesRoom.createFlow(__db, false, arrayOf("notes"), object :
        Callable<List<NoteEntity>> {
      public override fun call(): List<NoteEntity> {
        val _cursor: Cursor = query(__db, _statement, false, null)
        try {
          val _cursorIndexOfId: Int = getColumnIndexOrThrow(_cursor, "id")
          val _cursorIndexOfUid: Int = getColumnIndexOrThrow(_cursor, "uid")
          val _cursorIndexOfTitle: Int = getColumnIndexOrThrow(_cursor, "title")
          val _cursorIndexOfContent: Int = getColumnIndexOrThrow(_cursor, "content")
          val _cursorIndexOfColor: Int = getColumnIndexOrThrow(_cursor, "color")
          val _cursorIndexOfImportance: Int = getColumnIndexOrThrow(_cursor, "importance")
          val _cursorIndexOfSelfDestructDate: Int = getColumnIndexOrThrow(_cursor,
              "self_destruct_date")
          val _result: MutableList<NoteEntity> = ArrayList<NoteEntity>(_cursor.getCount())
          while (_cursor.moveToNext()) {
            val _item: NoteEntity
            val _tmpId: Int
            _tmpId = _cursor.getInt(_cursorIndexOfId)
            val _tmpUid: String
            _tmpUid = _cursor.getString(_cursorIndexOfUid)
            val _tmpTitle: String
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle)
            val _tmpContent: String
            _tmpContent = _cursor.getString(_cursorIndexOfContent)
            val _tmpColor: Int
            _tmpColor = _cursor.getInt(_cursorIndexOfColor)
            val _tmpImportance: Importance
            val _tmp: String
            _tmp = _cursor.getString(_cursorIndexOfImportance)
            _tmpImportance = __converters().stringToNoteImportance(_tmp)
            val _tmpSelfDestructDate: Long?
            if (_cursor.isNull(_cursorIndexOfSelfDestructDate)) {
              _tmpSelfDestructDate = null
            } else {
              _tmpSelfDestructDate = _cursor.getLong(_cursorIndexOfSelfDestructDate)
            }
            _item =
                NoteEntity(_tmpId,_tmpUid,_tmpTitle,_tmpContent,_tmpColor,_tmpImportance,_tmpSelfDestructDate)
            _result.add(_item)
          }
          return _result
        } finally {
          _cursor.close()
        }
      }

      protected fun finalize() {
        _statement.release()
      }
    })
  }

  private fun __converters(): Converters = __converters.value

  public companion object {
    @JvmStatic
    public fun getRequiredConverters(): List<Class<*>> = listOf(Converters::class.java)
  }
}
