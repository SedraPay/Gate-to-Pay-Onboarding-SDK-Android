package com.gatetopay.onboardingapplication

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import java.util.Locale

object LanguageUtil {

    private const val PREFS_NAME = "app_prefs"
    private const val KEY_LANGUAGE = "language_code"

    fun toggleLanguage(context: Context): String {
        val currentLang = getSavedLanguage(context)
        val newLang = if (currentLang == "ar") "en" else "ar"
        setLocale(context, newLang)
        return newLang
    }

    fun setLocale(context: Context, languageCode: String): Context {
        saveLanguagePreference(context, languageCode)

        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.createConfigurationContext(config)
        } else {
            @Suppress("DEPRECATION")
            context.resources.updateConfiguration(config, context.resources.displayMetrics)
            context
        }
    }

    fun getSavedLanguage(context: Context): String {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_LANGUAGE, "en") ?: "en"
    }

    fun applySavedLocale(context: Context): Context {
        val languageCode = getSavedLanguage(context)
        return setLocale(context, languageCode)
    }

    private fun saveLanguagePreference(context: Context, languageCode: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_LANGUAGE, languageCode).apply()
    }
}
