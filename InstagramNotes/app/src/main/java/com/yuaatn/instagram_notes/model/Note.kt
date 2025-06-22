package com.yuaatn.instagram_notes.model

import org.json.JSONObject
import java.util.Date
import java.util.UUID
import android.graphics.Color as AndroidColor

data class Note(
    val uid: String = UUID.randomUUID().toString(),
    val title: String,
    val content: String,
    val color: Int = AndroidColor.WHITE,
    val importance: Importance = Importance.NORMAL,
    val deadline: Long? = null,
    val createdAt: Long? = Date().time,
    val changedAt: Long? = null,
    val deviceId: String = "myDeviceId_123"
) {
    companion object {
        fun parse(json: JSONObject): Note? {
            return try {
                Note(
                    uid = json.optString("uid")
                        .takeIf { it.isNotEmpty() }
                        ?: UUID.randomUUID().toString(),

                    title = json.getString("title"),
                    content = json.getString("content"),
                    color = json.optInt("color", AndroidColor.WHITE),

                    importance = json.optString("importance")
                        .takeIf { it.isNotEmpty() }
                        ?.let { Importance.valueOf(it) }
                        ?: Importance.NORMAL
                )
            } catch (e: Exception) {
                null
            }
        }
    }

    val json: JSONObject
        get() = JSONObject().apply {
            put("uid", uid)
            put("title", title)
            put("content", content)

            if (color != AndroidColor.WHITE) {
                put("color", color)
            }

            if (importance != Importance.NORMAL) {
                put("importance", importance.name)
            }
        }
}