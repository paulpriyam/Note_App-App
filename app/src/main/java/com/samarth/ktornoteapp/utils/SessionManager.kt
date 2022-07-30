package com.samarth.ktornoteapp.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.samarth.ktornoteapp.utils.Constants.JWT_TOKEN
import kotlinx.coroutines.flow.first

class SessionManager(val context: Context) {


    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("session_manager")

    suspend fun saveToken(token: String) {
        val jwtTokenKey = stringPreferencesKey(JWT_TOKEN)
        context.dataStore.edit {
            it[jwtTokenKey] = token
        }
    }

    suspend fun getToekn(): String? {
        val jwtTokenKey = stringPreferencesKey(JWT_TOKEN)
        val preferences = context.dataStore.data.first()
        return preferences[jwtTokenKey]
    }
}