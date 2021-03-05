package com.app.tawktest.utils

import android.content.Context
import android.content.SharedPreferences
import com.app.tawktest.R

class PreferenceManager(context: Context) {
    private val editor: SharedPreferences.Editor

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        context.getString(R.string.app_name),
        Context.MODE_PRIVATE
    )

    fun saveString(key: String?, value: String?) {
        editor.putString(key, value)
        editor.apply()
    }

     fun getString(key: String?): String? {
        return sharedPreferences.getString(key, "")
    }

    fun saveBoolean(key: String?, value: Boolean) {
        editor.putBoolean(key, value)
        editor.apply()
    }

    fun getBoolean(key: String?): Boolean {
        return sharedPreferences.getBoolean(key, false)
    }

    fun clearPreferences() {
        editor.clear()
        editor.apply()
    }

    fun saveInt(keyTrainerId: String?, trainerId: Int?) {
        editor.putInt(keyTrainerId, trainerId!!)
        editor.apply()
    }

    init {
        editor = sharedPreferences.edit()
        editor.apply()
    }
}