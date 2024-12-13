package com.gazura.projectcapstone.token

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME = "user_session"
        private const val IS_LOGGED_IN = "is_logged_in"
        private const val TOKEN = "token"
        private const val EMAIL = "email"
        private const val PASSWORD = "password"
        private const val PROFILE_PHOTO_URI = "profile_photo_uri"
    }

    fun saveEmail(email: String) {
        val editor = prefs.edit()
        editor.putString(EMAIL, email)
        editor.apply()
    }

    fun getEmail(): String? {
        return prefs.getString(EMAIL, null)
    }

    fun savePassword(password: String) {
        val editor = prefs.edit()
        editor.putString(PASSWORD, password)
        editor.apply()
    }

    fun getPassword(): String? {
        return prefs.getString(PASSWORD, null)
    }

    fun saveLoginSession(token: String) {
        val editor = prefs.edit()
        editor.putBoolean(IS_LOGGED_IN, true)
        editor.putString(TOKEN, token)
        editor.apply()
    }

    fun isLoggedIn(): Boolean {
        return prefs.getBoolean(IS_LOGGED_IN, false)
    }

    fun getToken(): String? {
        return prefs.getString(TOKEN, null)
    }

    fun clearLoginSession() {
        val editor = prefs.edit()
        editor.remove(IS_LOGGED_IN)
        editor.remove(TOKEN)
        editor.apply()
    }
}