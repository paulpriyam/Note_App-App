package com.samarth.ktornoteapp.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.google.gson.Gson
import com.samarth.ktornoteapp.data.local.NoteDatabase
import com.samarth.ktornoteapp.data.local.dao.NotesDao
import com.samarth.ktornoteapp.data.remote.models.NotesApi
import com.samarth.ktornoteapp.repository.NoteRepo
import com.samarth.ktornoteapp.repository.NoteRepoImpl
import com.samarth.ktornoteapp.utils.Constants.BASE_URL
import com.samarth.ktornoteapp.utils.SessionManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object AppModule {

    @Singleton
    @Provides
    fun provideGson() = Gson()


    @Provides
    @Singleton
    fun provideSessionManager(
        @ApplicationContext context: Context
    ) = SessionManager(context)

    @Singleton
    @Provides
    fun provideNotesdatabase(
        @ApplicationContext context: Context
    ): NoteDatabase = Room.databaseBuilder(context, NoteDatabase::class.java, "note_db").build()

    @Singleton
    @Provides
    fun provideNotesDao(
        noteDb: NoteDatabase
    ) = noteDb.getNoteDao()


    fun provideNoteApi(): NotesApi {
        val httpLoggingInterceptor =
            HttpLoggingInterceptor()
                .setLevel(HttpLoggingInterceptor.Level.BODY)

        val client = OkHttpClient.Builder()
            .addInterceptor(httpLoggingInterceptor)
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(NotesApi::class.java)

    }

    @Singleton
    @Provides
    fun providesNoteRepo(
        noteApi: NotesApi,
        notesDao: NotesDao,
        sessionManager: SessionManager
    ): NoteRepo {
        return NoteRepoImpl(
            noteApi,
            notesDao,
            sessionManager
        )
    }
}