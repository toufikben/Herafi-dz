package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.lifecycleScope
import com.example.data.model.AppLanguage
import com.example.data.prefs.AppPreferencesManager
import com.example.ui.MainViewModel
import com.example.ui.screens.HomeScreen
import com.example.ui.theme.HerafiDzTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    private val themeMode = MutableStateFlow(AppPreferencesManager.ThemeMode.SYSTEM)
    private val langCode = MutableStateFlow("ar")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        lifecycleScope.launch {
            AppPreferencesManager.themeMode(this@MainActivity).collect {
                themeMode.value = it
            }
        }
        lifecycleScope.launch {
            AppPreferencesManager.language(this@MainActivity).collect {
                langCode.value = it
                applyLanguage(it)
            }
        }

        // Apply persisted language immediately (before first composition)
        applyLanguage(langCode.value)

        setContent {
            val mode by themeMode.collectAsState()
            val languageOverride by langCode.collectAsState()
            val darkTheme = when (mode) {
                AppPreferencesManager.ThemeMode.SYSTEM -> isSystemInDarkTheme()
                AppPreferencesManager.ThemeMode.DARK -> true
                AppPreferencesManager.ThemeMode.LIGHT -> false
            }
            HerafiDzTheme(darkTheme = darkTheme) {
                HomeScreen(viewModel = viewModel, languageOverride = languageOverride)
            }
        }
    }

    private fun applyLanguage(code: String) {
        val lang = AppLanguage.entries.firstOrNull { it.code == code } ?: AppLanguage.AR
        val config = resources.configuration
        val locale = java.util.Locale(lang.code)
        config.setLocale(locale)
        config.setLayoutDirection(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
    }
}
