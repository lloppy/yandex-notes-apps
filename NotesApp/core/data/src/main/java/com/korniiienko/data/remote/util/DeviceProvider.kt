package com.korniiienko.data.remote.util

import android.content.Context

class DeviceProvider(private val context: Context) {

    fun getDeviceId(): String {
        val prefs = context.getSharedPreferences(PREFS_KEY, Context.MODE_PRIVATE)
        return prefs.getString(DEVICE_ID_KEY, null) ?: run {
            val newId = generateDeviceId()
            prefs.edit().putString(DEVICE_ID_KEY, newId).apply()
            newId
        }
    }

    private fun generateDeviceId(): String {
        return java.util.UUID.randomUUID().toString()
    }

    companion object{
        private const val PREFS_KEY = "device_prefs_key"
        private const val DEVICE_ID_KEY = "device_id_key"
    }
}