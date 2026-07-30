package com.example.data.model

enum class AppLanguage(val code: String, val displayName: String, val nativeName: String, val isRtl: Boolean) {
    AR("ar", "Arabic", "العربية", true),
    FR("fr", "French", "Français", false),
    EN("en", "English", "English", false)
}
