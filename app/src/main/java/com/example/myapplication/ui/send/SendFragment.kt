package com.example.myapplication.ui.send

import android.os.Bundle
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
import com.example.myapplication.ui.home.HomeViewModel

class SendFragment : Fragment() {

    private var _binding: FragmentSendBinding? = null

    private var _binding2: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val binding2 get() = _binding2!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val sendViewModel =
            ViewModelProvider(this).get(SendViewModel::class.java)

        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentSendBinding.inflate(inflater, container, false)
        val root: View = binding.root

        _binding2 = FragmentHomeBinding.inflate(inflater, container, false)
        val root2: View = binding2.root

        val textView: TextView = binding.textSend
        sendViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        val balanceTextView: TextView = root.findViewById(R.id.TextShowBalance)
        val sendButton: Button = root.findViewById(R.id.buttonSend)
        val recipientEditText: EditText = root.findViewById(R.id.editTextRecipient)
        val fundsEditText: EditText = root.findViewById(R.id.editTextFunds)
        val TextShowBalance: TextView = root.findViewById(R.id.TextShowBalance)
        val usernameEditText: EditText = root2.findViewById(R.id.usernameEditText)

        sendViewModel.balance.observe(viewLifecycleOwner) { balance ->
            // Update UI with the new balance value
            balanceTextView.text = "Balance: $balance"
        }

        sendButton.setOnClickListener {
            val recipientUsername = recipientEditText.text.toString()
            val fundsAmount = fundsEditText.text.toString().toDouble()

            val exampleRecipient1 = User.builder()
                .username(recipientUsername)
                .funds(fundsAmount)
                .build()

            Amplify.DataStore.save(
                exampleRecipient1,
                { success ->
                    Log.i("Amplify", "Saved User: $success")
                },
                { error ->
                    Log.e("Amplify", "Error saving User", error)
                }
            )

            val newTransaction = Transaction.builder()
                .senderUsername(usernameEditText.text.toString())
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
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}