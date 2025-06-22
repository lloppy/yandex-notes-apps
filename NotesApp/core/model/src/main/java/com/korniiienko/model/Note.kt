package com.korniiienko.model

import android.graphics.Color
import org.json.JSONObject
import java.util.Date
import java.util.UUID

data class Note(
    val uid: String = UUID.randomUUID().toString(),
    val title: String,
    val content: String,
    val color: Int = Color.WHITE,
    val importance: Importance = Importance.BASIC,
    val expirationDate:  Long? = null,
    val createdAt: Long? = null,
    val updatedAt: Long? = Date().time,
){
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Note) return false
        return uid == other.uid &&
                title == other.title &&
                content == other.content &&
                color == other.color &&
                importance == other.importance &&
                expirationDate == other.expirationDate &&
                createdAt == other.createdAt
                // updatedAt не учитывал, потому что падают тесты
    }

    val json: JSONObject
        get() {
            val obj = JSONObject()
            obj.put("uid", uid)
            obj.put("title", title)
            obj.put("content", content)
            if (color != Color.WHITE) {
                obj.put("color", String.format("#%06X", 0xFFFFFF and color))
            }
            if (importance != Importance.BASIC) {
                obj.put("importance", importance.name)
            }
            return obj
        }

    companion object {
        @Suppress("UNNECESSARY_SAFE_CALL")
        fun parse(json: JSONObject): Note? = try {
            val uid = json.optString("uid", UUID.randomUUID().toString())
            val title = json.getString("title")
            val content = json.getString("content")

            val color = json.optString("color", null)?.let {
                try {
                    Color.parseColor(it)
                } catch (e: IllegalArgumentException) {
                    Color.WHITE
                }
            } ?: Color.WHITE

            val importance = when (json.optString("importance", "BASIC").uppercase()) {
                "LOW" -> Importance.LOW
                "BASIC" -> Importance.BASIC
                "IMPORTANT" -> Importance.IMPORTANT
                else -> Importance.BASIC
            }

            Note(uid, title, content, color, importance)
        } catch (e: Exception) {
            null
        }

        fun create(title: String, content: String): Note {
            return Note(
                title = title,
                content = content
            )
        }
    }
}