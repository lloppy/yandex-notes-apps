package com.korniiienko.notesapp.di

import android.content.Context
import com.korniiienko.data.local.json.JsonLocalRepository
import com.korniiienko.data.ThemeRepositoryImpl
import com.korniiienko.data.local.room.RoomLocalRepository
import com.korniiienko.data.local.room.RoomNotesDatabase
import com.korniiienko.data.remote.RemoteRepositoryImpl
import com.korniiienko.data.remote.util.DeviceProvider
import com.korniiienko.domain.LocalRepository
import com.korniiienko.domain.RemoteRepository
import com.korniiienko.domain.ThemeRepository

interface AppContainer {
    val localRepository: LocalRepository
    val remoteRepository: RemoteRepository
    val themeRepository: ThemeRepository
}

class AppDataContainer(private val context: Context) : AppContainer {

    private val networkManager by lazy { NetworkManager() }
    private val deviceProvider by lazy { DeviceProvider(context) }


    override val remoteRepository: RemoteRepository by lazy {
        RemoteRepositoryImpl(
            api = networkManager.apiService,
            deviceProvider = deviceProvider
        )
    }

//    override val localRepository: LocalRepository by lazy {
//        JsonLocalRepository(context = context)
//    }

    override val localRepository: LocalRepository by lazy {
        RoomLocalRepository(
            dao = RoomNotesDatabase.getDatabase(context).noteDao()
        )
    }
    override val themeRepository: ThemeRepository = ThemeRepositoryImpl(context)
}