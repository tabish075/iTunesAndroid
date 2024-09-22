package com.droid.itunesandroid.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.droid.itunesandroid.util.SharedPreferencesHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SharedViewModel @Inject constructor(
    val sharedPrefsHelper: SharedPreferencesHelper
) : ViewModel() {

    fun saveLastVisitDate() {
        sharedPrefsHelper.saveLastVisitDate()
    }

    fun getLastVisitDate(): Long {
        return sharedPrefsHelper.getLastVisitDate()
    }

    fun saveLastScreen(screenName: String) {
        sharedPrefsHelper.saveLastScreen(screenName)
    }

    fun getLastScreen(): String {
        return sharedPrefsHelper.getLastScreen()
    }
}
