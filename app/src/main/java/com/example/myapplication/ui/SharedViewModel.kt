package com.example.myapplication.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData
import android.util.Log
import androidx.lifecycle.LiveData
import com.amplifyframework.core.Amplify
import com.amplifyframework.core.model.query.Where
import com.amplifyframework.datastore.generated.model.User


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

    // Function to fetch user balance
    fun fetchUserFunds(name: String) {
        Amplify.DataStore.query(
            User::class.java,
            Where.matches(User.USERNAME.eq(name)),
            { result ->
                if (result.hasNext()) {
                    val user1 = result.next()

                    // Access current user username and funds
                    // _username.postValue(user1.username)
                    // _balance.postValue(user1.funds)
                    updateBalance(user1.funds)

                    Log.i("Amplify", "Retrieved User for SharedViewModel: ${user1.username}")
                    Log.i("Amplify", "Retrieved User Funds for SharedViewModel: ${user1.funds}")

                } else {
                    Log.i("Amplify", "User not found for SharedViewModel; user searched in query: $name")
                }
            },
            { error ->
                Log.e("Amplify", "Error querying User", error)
            }
        )
    }

    // function to update username
    fun updateUsername(newUsername: String) {
        _username.postValue(newUsername)
        Log.i("SharedViewModel", "Username updated: $newUsername")
    }


}