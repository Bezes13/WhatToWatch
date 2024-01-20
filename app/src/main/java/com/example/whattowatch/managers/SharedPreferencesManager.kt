package com.example.whattowatch.managers

import android.content.Context
import android.content.SharedPreferences

class SharedPreferencesManager(val context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)

    fun saveName(name: String, saveId: Int) {
        val editor = sharedPreferences.edit()
        editor.putString(context.getString(saveId), name)
        editor.apply()
    }

    fun readName(saveId: Int): String {
        return sharedPreferences.getString(context.getString(saveId), "") ?: ""
    }
}
