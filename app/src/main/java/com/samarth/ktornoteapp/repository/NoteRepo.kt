package com.samarth.ktornoteapp.repository

import com.samarth.ktornoteapp.data.remote.models.Users
import com.samarth.ktornoteapp.utils.Result
interface NoteRepo {
    suspend fun createUser(user:Users):Result<String>
    suspend fun login(user:Users):Result<String>
    suspend fun getUser():Result<Users>
    suspend fun logout():Result<String>
}