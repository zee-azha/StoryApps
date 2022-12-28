package com.example.storyapps.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SaveAuthPreferences constructor(private val dataStore: DataStore<Preferences>) {

    private val tokenKey = stringPreferencesKey(key)

    fun getToken(): Flow<String?> {
        return dataStore.data.map { preferences ->
            preferences[tokenKey]
        }
    }

    suspend fun saveAuthToken(token: String) {
        dataStore.edit {
            it[tokenKey] = token
        }
    }

    suspend fun deleteAuthToken(){
        dataStore.edit {
            it.clear()
        }

    }

    companion object {
        const val key: String = "token"

        @Volatile
        private var INSTANCE: SaveAuthPreferences? = null

        fun getInstance(dataStore: DataStore<Preferences>): SaveAuthPreferences {
            return INSTANCE ?: synchronized(this) {
                val instance = SaveAuthPreferences(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}