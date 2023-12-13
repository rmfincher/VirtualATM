package com.example.myapplication.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData
import android.util.Log
import androidx.lifecycle.LiveData
import com.amplifyframework.core.Amplify


class SharedViewModel : ViewModel() {
    // balance data
    private val _balance = MutableLiveData<Double>()
    val balance: LiveData<Double> get() = _balance

    // username data
    private val _username = MutableLiveData<String>()
    val username: LiveData<String> get() = _username

    // function to update balance
    fun updateBalance(newBalance: Double) {
        _balance.postValue(newBalance)
        Log.i("SharedViewModel", "Balance updated: $newBalance")
    }

    // function to update username
    fun updateUsername(newUsername: String) {
        _username.postValue(newUsername)
        Log.i("SharedViewModel", "Username updated: $newUsername")
    }


}