package com.example.myapplication.ui.send

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SendViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Send Money to Your Friends!"
    }
    private val _balance = MutableLiveData<Double>()

    val text: LiveData<String> = _text
    val balance: LiveData<Double> get() = _balance

    // Function to update the current balance
    fun updateBalance(newBalance: Double) {
        _balance.value = newBalance
    }
}