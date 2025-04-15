package com.example.bloodpressureapp.util

import android.content.Context
import android.content.SharedPreferences

class PreferenceManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    fun getLastSelectedUserId(): Int {
        return prefs.getInt("last_user_id", -1)
    }

    fun setLastSelectedUserId(userId: Int) {
        prefs.edit().putInt("last_user_id", userId).apply()
    }
}