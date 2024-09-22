package com.droid.itunesandroid.util

import android.content.Context

class SharedPreferencesHelper(context: Context) {
    
    private val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    // Save last visit date
    fun saveLastVisitDate() {
        sharedPreferences.edit().putLong("last_visit_date", System.currentTimeMillis()).apply()
    }

    // Retrieve last visit date
    fun getLastVisitDate(): Long {
        return sharedPreferences.getLong("last_visit_date", 0L)
    }

    // Save the last screen name
    fun saveLastScreen(screenName: String) {
        sharedPreferences.edit().putString("last_screen", screenName).apply()
    }

    // Retrieve the last screen name
    fun getLastScreen(): String {
        return sharedPreferences.getString("last_screen", "MovieListFragment") ?: "MovieListFragment"
    }
}
