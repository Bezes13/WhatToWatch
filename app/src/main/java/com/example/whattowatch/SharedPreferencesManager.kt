package com.example.whattowatch

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SharedPreferencesManager(val context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)

    fun saveName(name: String){
        val editor = sharedPreferences.edit()
        editor.putString(context.getString(R.string.user_name), name)
        editor.apply()
    }

    fun readName():String{
        return sharedPreferences.getString(context.getString(R.string.user_name), "")?:""
    }
    fun saveList(key: String, list: List<String>) {
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val json = gson.toJson(list)
        editor.putString(key, json)
        editor.apply()
    }

    fun getList(keyId: Int): List<String> {
        val key = context.getString(keyId)
        val json = sharedPreferences.getString(key, null)
        val type = object : TypeToken<List<String>>() {}.type
        return Gson().fromJson(json, type) ?: emptyList()
    }
}
