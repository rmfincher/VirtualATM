package com.example.myapplication.ui.deposit

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DepositViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is the Deposit Fragment"
    }
    val text: LiveData<String> = _text
}