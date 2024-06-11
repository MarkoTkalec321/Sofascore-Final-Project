package com.sofascore.scoreandroidacademy.util

import android.content.Context
import android.content.SharedPreferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore("app_preferences")

class DataStoreManager(context: Context) {
    private val dataStore = context.dataStore

    companion object {
        val FIRST_LAUNCH = booleanPreferencesKey("first_launch")
        val THEME_KEY = stringPreferencesKey("theme_preference")
    }

    val isFirstLaunch: Flow<Boolean> = dataStore.data
        .map { preferences ->
            preferences[FIRST_LAUNCH] ?: true
        }

    suspend fun setFirstLaunchDone() {
        dataStore.edit { preferences ->
            preferences[FIRST_LAUNCH] = false
        }
    }

    val themePreference: Flow<String> = dataStore.data
        .map { preferences ->
            preferences[THEME_KEY] ?: "light"
        }

    suspend fun saveThemePreference(theme: String) {
        dataStore.edit { preferences ->
            preferences[THEME_KEY] = theme
        }
    }
}