package com.yuaatn.instagram_notes.di

import android.content.Context
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.yuaatn.instagram_notes.data.local.LocalRepository
import com.yuaatn.instagram_notes.data.local.file.FileNotebookImpl
import com.yuaatn.instagram_notes.data.local.file.FileNotebookProxy
import com.yuaatn.instagram_notes.data.local.room.LocalNotesDatabase
import com.yuaatn.instagram_notes.data.local.room.repository.RoomRepository
import com.yuaatn.instagram_notes.data.remote.NotesApi
import com.yuaatn.instagram_notes.data.remote.RemoteRepository
import com.yuaatn.instagram_notes.data.remote.RemoteRepositoryImpl
import com.yuaatn.instagram_notes.data.sync.NotesSynchronizer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val API_BASE_URL = "https://hive.mrdekk.ru/todo/"
    private const val AUTH_TOKEN = "6b4e716a-46a1-44c6-a701-cabae53a6d03"

    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        encodeDefaults = true
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $AUTH_TOKEN")
                .addHeader("Accept", "application/json")
                .addHeader("Content-Type", "application/json")
                .build()
            chain.proceed(request)
        }
        .build()

    @Provides
    @Singleton
    fun provideRetrofit(
        json: Json,
        okHttpClient: OkHttpClient,
    ): Retrofit = Retrofit.Builder()
        .baseUrl(API_BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()

    @Provides
    @Singleton
    fun provideNotesApi(retrofit: Retrofit): NotesApi = retrofit.create(NotesApi::class.java)

    @Provides
    @Singleton
    fun provideRemoteRepository(api: NotesApi): RemoteRepository = RemoteRepositoryImpl(api)
}

@Module
@InstallIn(SingletonComponent::class)
object RepositoriesModule {

//    @Provides
//    @Singleton
//    fun provideFileNotebook(@ApplicationContext context: Context): LocalRepository {
//        val originalRepository = FileNotebookImpl(context)
//        return FileNotebookProxy(originalRepository)
//    }

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): LocalNotesDatabase {
        return LocalNotesDatabase.getDatabase(context)
    }

    @Provides
    @Singleton
    fun provideRoom(
        database: LocalNotesDatabase
    ): LocalRepository {
        val noteDao = database.noteDao()
        return RoomRepository(noteDao)
    }

    @Provides
    @Singleton
    fun provideNotesSynchronizer(
        local: LocalRepository,
        remote: RemoteRepository
    ): NotesSynchronizer {
        return NotesSynchronizer(
            localRepository = local,
            remoteRepository = remote
        )
    }
}