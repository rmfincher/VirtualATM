package com.example.myapplication.ui.send

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
import com.amplifyframework.core.model.query.Where
import com.example.myapplication.databinding.FragmentHomeBinding
import com.example.myapplication.ui.SharedViewModel
import com.example.myapplication.ui.home.HomeViewModel
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Date

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
        val recipientEditText: EditText = root.findViewById(R.id.editTextRecipient)
        val fundsEditText: EditText = root.findViewById(R.id.editTextFunds)
        val usernameEditText: EditText = root2.findViewById(R.id.usernameEditText)

        val dateFilterButton: Button = root.findViewById(R.id.buttonFilter)
        dateFilterButton.setOnClickListener {
            showTransactionsByDate()
        }

        sharedViewModel.balance.observe(viewLifecycleOwner) { newBalance ->
            Log.i("SendFragment", "Balance updated: $newBalance")
            // Update UI with the new balance value
            val formattedBalance = DecimalFormat("#.##").format(newBalance)
            binding.TextShowBalance.text = formattedBalance
        }

        sharedViewModel.username.observe(viewLifecycleOwner) { newUsername ->
            Log.i("SendFragment", "Username Observed: $newUsername")

            currentUsername = newUsername
        }

        sendButton.setOnClickListener {
            val fundsAmount = fundsEditText.text.toString().toDouble()

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

            // Update Recipient balance data
            val recipientUsername = recipientEditText.text.toString()
            val fundsAmountText = fundsEditText.text.toString()

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

                    // Update Recipient balance data
                    Amplify.DataStore.query(
                        User::class.java,
                        Where.matches(User.USERNAME.eq(recipientUsername)),
                        { result ->
                            if (result.hasNext()) {
                                val recipientUser = result.next()

                                // Access user data
                                val recipUsername = recipientUser.username
                                val recipientFunds = recipientUser.funds

                                Log.i("Amplify", "Retrieved Recipient Username before transfer: $recipUsername")
                                Log.i("Amplify", "Retrieved Recipient Funds before transfer: $recipientFunds")

                                // Update recipientFunds based on the fundsAmount
                                val newRecipientFunds = recipientFunds + fundsAmount

                                val updatedRecipientUser = recipientUser.copyOfBuilder()
                                    .funds(newRecipientFunds)
                                    .build()

                                Amplify.DataStore.save(
                                    updatedRecipientUser,
                                    { success ->
                                        Log.i("Amplify", "Updated Recipient Funds: $newRecipientFunds")
                                    },
                                    { error ->
                                        Log.e("Amplify", "Error updating Recipient Funds", error)
                                    }
                                )

                                // Log the updated recipient funds
                                Log.i("Amplify", "Updated Recipient Funds: $newRecipientFunds")

                            } else {
                                Log.i("Amplify", "Recipient not found")
                            }
                        },
                        { error ->
                            Log.e("Amplify", "Error querying Recipient", error)
                        }
                    )

                    // update user balance data
                    Amplify.DataStore.query(
                        User::class.java,
                        Where.matches(User.USERNAME.eq(currentUsername)),
                        { result ->
                            if (result.hasNext()) {
                                val user1 = result.next()

                                // Access user data
                                val user1Name = user1.username
                                val userFunds = user1.funds

                                Log.i("Amplify", "Retrieved User Username before transfer: $user1Name")
                                Log.i("Amplify", "Retrieved User Funds before transfer: $userFunds")

                                // Update userFunds based on the fundsAmount
                                val newUserFunds = userFunds - fundsAmount

                                try {
                                    sharedViewModel.updateBalance(newUserFunds)
                                } catch (e: Exception) {
                                    Log.e("HomeFragment", "Error updating balance", e)
                                }

                                val updatedUser = user1.copyOfBuilder()
                                    .funds(newUserFunds)
                                    .build()

                                Amplify.DataStore.save(
                                    updatedUser,
                                    { success ->
                                        Log.i("Amplify", "Updated User Funds: $newUserFunds")
                                    },
                                    { error ->
                                        Log.e("Amplify", "Error updating User Funds", error)
                                    }
                                )

                                // Log the updated user funds
                                Log.i("Amplify", "Updated User Funds: $newUserFunds")

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
            showTransactionsByDate()
        }
        return root
    }

    private fun showTransactionsByDate() {
        val currentDate = SimpleDateFormat("yyyy-MM-dd").format(Date())

        Amplify.DataStore.query(
            Transaction::class.java,
            Where.matches(Transaction.date.eq(currentDate)),
            { result ->
                while (result.hasNext()) {
                    val transaction = result.next()

                    val senderUsername = transaction.senderUsername
                    val recipientUsername = transaction.recipientUsername
                    val fundsAmount = transaction.funds
                    val transactionDate = transaction.date

                    Log.i("Amplify", "Transaction - Sender: $senderUsername, Recipient: $recipientUsername, Funds: $fundsAmount, Date: $transactionDate")
                }

                if (!result.hasNext()) {
                    Log.i("Amplify", "No transactions found for the selected date.")
                }
            },
            { error ->
                Log.e("Amplify", "Error querying transactions by date", error)
            }
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

