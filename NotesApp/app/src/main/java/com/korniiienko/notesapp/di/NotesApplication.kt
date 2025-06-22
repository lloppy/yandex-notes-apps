package com.korniiienko.notesapp.di

import android.app.Application

class NotesApplication: Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }
}