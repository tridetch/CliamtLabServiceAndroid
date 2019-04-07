package ru.climatlab.service.data

import android.content.SharedPreferences
import android.preference.PreferenceManager
import ru.climatlab.service.App

object PreferencesRepository {

    private val TOKEN_KEY = "TOKEN_KEY"

    private val prefs: SharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(App.context)
    }

    fun putToken(token: String) {
        prefs.edit().putString(TOKEN_KEY, token).apply()
    }

    fun getToken(): String {
        return prefs.getString(TOKEN_KEY, "")!!
    }
}
