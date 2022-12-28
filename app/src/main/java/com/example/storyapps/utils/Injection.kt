package com.example.storyapps.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.storyapps.data.Repository
import com.example.storyapps.data.SaveAuthPreferences
import com.example.storyapps.data.database.StoriesDatabase
import com.example.storyapps.data.remote.ApiInstance

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    "token"
)

object Injection {

    fun provideRepository(context: Context): Repository {
        val apiService = ApiInstance.getApiService()
        val preferences = SaveAuthPreferences.getInstance(context.dataStore)
        val database = StoriesDatabase.getDatabase(context)

        return Repository.getInstance(apiService, preferences,database)
    }
}