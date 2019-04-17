package ru.climatlab.service.data

import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.google.gson.Gson
import ru.climatlab.service.App
import ru.climatlab.service.data.model.CurrentUserResponse

object PreferencesRepository {

    private val TOKEN_KEY = "TOKEN_KEY"
    private val NOTIFICATION_DEVICE_TOKEN_KEY = "NOTIFICATION_DEVICE_TOKEN_KEY"

    private val prefs: SharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(App.context)
    }

    fun putCurrentUserInfo(currentUser: CurrentUserResponse?) {
        prefs.edit().putString(TOKEN_KEY, Gson().toJson(currentUser)).apply()
    }

    fun getCurrentUserInfo(): CurrentUserResponse? {
        return Gson().fromJson(prefs.getString(TOKEN_KEY, null), CurrentUserResponse::class.java)
    }

    fun putIsNeedUpdateToken(isNeedUpdateToken: Boolean) {
        prefs.edit().putBoolean(NOTIFICATION_DEVICE_TOKEN_KEY, isNeedUpdateToken).apply()
    }

    fun getIsNeedUpdateToken(): Boolean {
        return prefs.getBoolean(NOTIFICATION_DEVICE_TOKEN_KEY, true)
    }
}
