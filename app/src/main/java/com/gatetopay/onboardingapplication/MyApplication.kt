package com.gatetopay.onboardingapplication

import android.app.Application
import android.content.Context

class MyApplication : Application() {
    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(LanguageUtil.applySavedLocale(base))
    }
}
