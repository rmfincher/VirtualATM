package com.example.myapplication.ui.receive

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ReceiveViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Receive your money by going to the set location"
    }
    val text: LiveData<String> = _text
}