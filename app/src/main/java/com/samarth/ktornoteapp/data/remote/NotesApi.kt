package com.samarth.ktornoteapp.data.remote.models

import com.samarth.ktornoteapp.utils.Constants.API_VERSION
import retrofit2.http.*

interface NotesApi {

    @Headers("Content-Type: application/json")
    @POST("$API_VERSION/users/register")
    suspend fun createAccount(
        @Body user:Users
    ): StandardResponse




    @Headers("Content-Type: application/json")
    @POST("$API_VERSION/users/login")
    suspend fun login(
        @Body user:Users
    ): StandardResponse



    // ======== NOTES ============

    @Headers("Content-Type: application/json")
    @POST("$API_VERSION/notes/create")
    suspend fun createNote(
        @Header("Authorization") token:String,
        @Body note:RemoteNotes
    ): StandardResponse




    @Headers("Content-Type: application/json")
    @GET("$API_VERSION/notes")
    suspend fun getAllNote(
        @Header("Authorization") token:String
    ): List<RemoteNotes>





    @Headers("Content-Type: application/json")
    @POST("$API_VERSION/notes/update")
    suspend fun updateNote(
        @Header("Authorization") token:String,
        @Body note:RemoteNotes
    ): StandardResponse


    @Headers("Content-Type: application/json")
    @DELETE("$API_VERSION/notes/delete")
    suspend fun deleteNote(
        @Header("Authorization") token:String,
        @Query("id") noteId:String
    ): StandardResponse




}