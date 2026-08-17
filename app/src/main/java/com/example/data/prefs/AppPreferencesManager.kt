package com.example.data.prefs

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Central store for persistent app preferences (theme, language) backed by DataStore.
 */
object AppPreferencesManager {

    private const val PREFS_NAME = "herafi_prefs"
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = PREFS_NAME)

    private val KEY_THEME_DARK = stringPreferencesKey("theme_dark") // "dark", "light", "system"
    private val KEY_LANGUAGE = stringPreferencesKey("app_language") // "ar", "fr", "en"

    enum class ThemeMode(val value: String, val displayName: String) {
        SYSTEM("system", "النظام"),
        LIGHT("light", "فاتح"),
        DARK("dark", "داكن");

        companion object {
            fun from(value: String?): ThemeMode =
                entries.firstOrNull { it.value == value } ?: SYSTEM
        }
    }

    fun themeMode(context: Context): Flow<ThemeMode> =
        context.dataStore.data.map { ThemeMode.from(it[KEY_THEME_DARK]) }

    fun language(context: Context): Flow<String> =
        context.dataStore.data.map { it[KEY_LANGUAGE] ?: "ar" }

    suspend fun setThemeMode(context: Context, mode: ThemeMode) {
        context.dataStore.edit { it[KEY_THEME_DARK] = mode.value }
    }

    suspend fun setLanguage(context: Context, code: String) {
        context.dataStore.edit { it[KEY_LANGUAGE] = code }
    }
}
