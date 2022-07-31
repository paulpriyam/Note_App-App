package com.samarth.ktornoteapp.repository

import com.samarth.ktornoteapp.data.local.dao.NotesDao
import com.samarth.ktornoteapp.data.local.models.LocalNote
import com.samarth.ktornoteapp.data.remote.models.NotesApi
import com.samarth.ktornoteapp.data.remote.models.RemoteNotes
import com.samarth.ktornoteapp.data.remote.models.Users
import com.samarth.ktornoteapp.utils.SessionManager
import com.samarth.ktornoteapp.utils.isNetworkConnected
import javax.inject.Inject
import com.samarth.ktornoteapp.utils.Result
import kotlinx.coroutines.flow.Flow

class NoteRepoImpl @Inject constructor(
    val noteApi: NotesApi,
    val notesDao: NotesDao,
    val sessionManager: SessionManager
) : NoteRepo {
    override suspend fun createUser(user: Users): Result<String> {
        return try {
            if (!isNetworkConnected(sessionManager.context)) {
                Result.Error<String>("No Internet Connection!")
            }

            val result = noteApi.createAccount(user)
            if (result.success) {
                sessionManager.updateSession(result.message, user.name ?: "", user.email)
                Result.Success("User Created Successfully!")
            } else {
                Result.Error<String>(result.message)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Error<String>(e.message ?: "Some Problem Occurred!")
        }
    }

    override suspend fun login(user: Users): Result<String> {
        return try {
            if (!isNetworkConnected(sessionManager.context)) {
                Result.Error<String>("No Internet Connection!")
            }

            val result = noteApi.login(user)
            if (result.success) {
                sessionManager.updateSession(result.message, user.name ?: "", user.email)
                getAllNotesFromServer()
                Result.Success("Logged In Successfully!")
            } else {
                Result.Error<String>(result.message)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Error<String>(e.message ?: "Some Problem Occurred!")
        }
    }

    override suspend fun getUser(): Result<Users> {
        return try {
            val name = sessionManager.getCurrentUserName()
            val email = sessionManager.getCurrentUserEmail()
            if (name == null || email == null) {
                Result.Error<Users>("User not Logged In!")
            }
            Result.Success(Users(name, email!!, ""))
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Error(e.message ?: "Some Problem Occurred!")
        }
    }

    override suspend fun logout(): Result<String> {
        return try {
            sessionManager.logout()
            Result.Success("Logged Out Successfully!")
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Error(e.message ?: "Some Problem Occurred!")
        }
    }

    override suspend fun createNote(note: LocalNote): Result<String> {
        try {
            notesDao.insertNote(note)
            val token = sessionManager.getJwtToken()
            if (token == null) {
                return Result.Error("User is not logged in")
            }
            val remoteNote = RemoteNotes(
                noteTitle = note.noteTitle,
                description = note.description,
                date = note.date,
                noteId = note.noteId
            )
            val res = noteApi.createNote(
                "Bearer $token",
                remoteNote
            )
            if (res.success) {
                notesDao.insertNote(note.apply { connected = true })
                return Result.Success("Note added successfully")
            } else {
                return Result.Error(res.message)
            }

        } catch (e: Exception) {
            e.printStackTrace()
            return Result.Error(e.message ?: "Some error occurred")
        }
    }

    override suspend fun updateNote(note: LocalNote): Result<String> {
        try {
            notesDao.insertNote(note)
            val token = sessionManager.getJwtToken()
            if (token == null) {
                return Result.Success("Note is Updated in Local Database!")
            }
            if (!isNetworkConnected(sessionManager.context)) {
                return Result.Error("Netwotk is not connect")
            }

            val result = noteApi.updateNote(
                "Bearer $token",
                RemoteNotes(
                    noteTitle = note.noteTitle,
                    description = note.description,
                    date = note.date,
                    noteId = note.noteId
                )
            )

            if (result.success) {
                notesDao.insertNote(note.also { it.connected = true })
                return Result.Success("Note Updated Successfully!")
            } else {
                return Result.Error(result.message)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return Result.Error(e.message ?: "Some Problem Occurred!")
        }
    }

    override fun getAllNotes(): Flow<List<LocalNote>> = notesDao.getAllNotesOrderedByDate()

    override suspend fun getAllNotesFromServer() {
        try {
            val token = sessionManager.getJwtToken() ?: return
            if (!isNetworkConnected(sessionManager.context)) {
                return
            }
            val result = noteApi.getAllNote("Bearer $token")
            result.forEach { remoteNote ->
                notesDao.insertNote(
                    LocalNote(
                        noteTitle = remoteNote.noteTitle,
                        description = remoteNote.description,
                        date = remoteNote.date,
                        connected = true,
                        noteId = remoteNote.noteId
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override suspend fun deleteNote(noteId: String) {

        try {

            notesDao.deleteNoteLocally(noteId)
            val token = sessionManager.getJwtToken() ?: kotlin.run {
                notesDao.deleteNote(noteId)
                return
            }
            if (!isNetworkConnected(sessionManager.context)) {
                return
            }

            val response = noteApi.deleteNote(
                "Bearer $token",
                noteId
            )

            if(response.success){
                notesDao.deleteNote(noteId)
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
    }
}