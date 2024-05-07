package com.example.myapplication.ui.send

import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.databinding.FragmentSendBinding
import android.widget.Button
import android.widget.EditText
import com.example.myapplication.R
import com.amplifyframework.datastore.generated.model.Transaction
import com.amplifyframework.datastore.generated.model.User
import com.amplifyframework.core.Amplify
import android.util.Log
import androidx.annotation.RequiresApi
import com.amplifyframework.core.model.query.Where
import com.example.myapplication.databinding.FragmentHomeBinding
import com.example.myapplication.ui.SharedViewModel
import com.example.myapplication.ui.home.HomeViewModel
import java.text.DecimalFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class SendFragment : Fragment() {

    private var _binding: FragmentSendBinding? = null

    private var _binding2: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val binding2 get() = _binding2!!

    private var currentUsername = ""

    private lateinit var sharedViewModel: SharedViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val sendViewModel =
            ViewModelProvider(this).get(SendViewModel::class.java)

        sharedViewModel =
            ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)

        _binding = FragmentSendBinding.inflate(inflater, container, false)
        val root: View = binding.root

        _binding2 = FragmentHomeBinding.inflate(inflater, container, false)
        val root2: View = binding2.root

        val textView: TextView = binding.textSend
        sendViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        val sendButton: Button = root.findViewById(R.id.buttonSend)
        val clearCacheButton: Button = root.findViewById(R.id.buttonClearCache)
        val refreshButton: Button = root.findViewById(R.id.buttonRefresh)
        val recipientEditText: EditText = root.findViewById(R.id.editTextRecipient)
        val fundsEditText: EditText = root.findViewById(R.id.editTextFunds)
        val longitudeEditText: EditText = root.findViewById(R.id.editTextLongitude)
        val latitudeEditText: EditText = root.findViewById(R.id.editTextLatitude)

        // Clear cache button to clear the local cache
        clearCacheButton.setOnClickListener {

            Amplify.DataStore.clear(
                { Log.i("MyApp", "DataStore cache cleared") },
                { error -> Log.e("MyApp", "Failed to clear DataStore cache", error) }
            )
        }

        // Refresh button to sync local with remote DB and fetchUserFunds - USER MUST PRESS TWICE
        refreshButton.setOnClickListener {

            sharedViewModel.fetchUserFunds(currentUsername)
        }

        // Loads current user's username
        sharedViewModel.username.observe(viewLifecycleOwner) { newUsername ->
            Log.i("SendFragment", "Username Observed: $newUsername")

            currentUsername = newUsername
            // sharedViewModel.fetchUserFunds(currentUsername)
        }

        sendButton.setOnClickListener {

            // Log User username and funds amount
            Amplify.DataStore.query(
                User::class.java,
                Where.matches(User.USERNAME.eq(currentUsername)),
                { result ->
                    if (result.hasNext()) {
                        val user = result.next()

                        // Access user data
                        val username = user.username
                        val funds = user.funds

                        Log.i("Amplify", "Retrieved User Username: $username")
                        Log.i("Amplify", "Retrieved User Funds: $funds")
                    } else {
                        Log.i("Amplify", "Send Fragment (90): User not found")
                    }
                },
                { error ->
                    Log.e("Amplify", "Error querying User", error)
                }
            )

            // Store transaction data
            val recipientUsername = recipientEditText.text.toString()
            val fundsAmountText = fundsEditText.text.toString()

            // Store Drop-off Coordinates
            val userLongitude = longitudeEditText.text.toString().toDouble()
            val userLatitude = latitudeEditText.text.toString().toDouble()

            // Check if fundsAmountText is not empty before attempting conversion
            if (fundsAmountText.isNotEmpty()) {
                try {
                    val fundsAmount = fundsAmountText.toDouble()

                    val newTransaction = Transaction.builder()
                        .senderUsername(currentUsername)
                        .recipientUsername(recipientUsername)
                        .funds(fundsAmount)
                        .build()

                    Amplify.DataStore.save(
                        newTransaction,
                        { success ->
                            Log.i("Amplify", "Saved Transaction: $success")
                        },
                        { error ->
                            Log.e("Amplify", "Error saving Transaction", error)
                        }
                    )

                    // Update current user (sender) location data
                    Amplify.DataStore.query(
                        User::class.java,
                        Where.matches(User.USERNAME.eq(currentUsername)),
                        { result ->
                            if (result.hasNext()) {
                                val user1 = result.next()

                                // Access sender username
                                val user1Name = user1.username

                                Log.i("Amplify", "Retrieved User Username before transfer: $user1Name")

                                // update user with location data
                                val updatedUser = user1.copyOfBuilder()
                                    .longitude(userLongitude)
                                    .latitude(userLatitude)
                                    .build()

                                Amplify.DataStore.save(
                                    updatedUser,
                                    { success ->
                                        Log.i("Amplify", "Updated Sender location: $success")
                                    },
                                    { error ->
                                        Log.e("Amplify", "Error updating Sender Location", error)
                                    }
                                )

                            } else {
                                Log.i("Amplify", "User not found")
                            }
                        },
                        { error ->
                            Log.e("Amplify", "Error querying User", error)
                        }
                    )




                } catch (e: NumberFormatException) {
                    Log.e("Amplify", "Error converting funds amount to Double", e)
                    // Handle the case where the text cannot be converted to a Double
                }
            } else {
                // Handle the case where fundsAmountText is empty
                Log.e("Amplify", "Funds amount is empty")
            }
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
