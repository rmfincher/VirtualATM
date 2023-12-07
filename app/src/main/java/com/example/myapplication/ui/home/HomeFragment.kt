package com.example.myapplication.ui.home

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.databinding.FragmentHomeBinding
import android.widget.Button
import com.example.myapplication.R
import com.amplifyframework.core.Amplify
import com.amplifyframework.auth.options.AuthSignUpOptions
import com.amplifyframework.auth.AuthUserAttributeKey
import android.util.Log
import com.amplifyframework.core.model.query.Where
import com.amplifyframework.datastore.generated.model.User
import com.example.myapplication.databinding.FragmentSendBinding
import com.example.myapplication.ui.send.SendViewModel
import java.lang.Thread.sleep

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    private var _binding2: FragmentSendBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
                ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textHome
        homeViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        val signUpButton: Button = root.findViewById(R.id.signUpButton)
        val signInButton: Button = root.findViewById(R.id.signInButton)
        val verifyCodeButton: Button = root.findViewById(R.id.verifyCodeButton)
        val emailEditText: TextView = root.findViewById(R.id.emailEditText)
        val usernameEditText: TextView = root.findViewById(R.id.usernameEditText)
        val passwordEditText: TextView = root.findViewById(R.id.passwordEditText)
        val authCodeEditText: TextView = root.findViewById(R.id.authCodeEditText)

        // Implementing Sign-Up Button logic with Amplify
        signUpButton.setOnClickListener {
            val options = AuthSignUpOptions.builder()
                .userAttribute(AuthUserAttributeKey.email(), emailEditText.text.toString())
                .build()
            Amplify.Auth.signUp(usernameEditText.text.toString(), passwordEditText.text.toString(), options,
                { Log.i("AuthQuickStart", "Sign up succeeded: $it") },
                { Log.e ("AuthQuickStart", "Sign up failed", it) }
            )

            // Wait for user to receive authentication code in email and enter (60 seconds)
            verifyCodeButton.setOnClickListener {
                Amplify.Auth.confirmSignUp(
                    usernameEditText.text.toString(), authCodeEditText.text.toString(),
                    { result ->
                        if (result.isSignUpComplete) {
                            Log.i("AuthQuickstart", "Confirm signUp succeeded")
                        } else {
                            Log.i("AuthQuickstart","Confirm sign up not complete")
                        }
                    },
                    { Log.e("AuthQuickstart", "Failed to confirm sign up", it) }
                )
            }

            // Create user in database
            val createdUser = User.builder()
                .username(usernameEditText.text.toString())
                .funds(100.0)
                .build()

            Amplify.DataStore.save(
                createdUser,
                { success ->
                    Log.i("Amplify", "Saved User: $success")
                },
                { error ->
                    Log.e("Amplify", "Error saving User", error)
                }
            )
        }

        // Implementing Sign-In Button logic with Amplify
        signInButton.setOnClickListener {
            Amplify.Auth.signIn(usernameEditText.text.toString(), passwordEditText.text.toString(),
                { result ->
                    if (result.isSignedIn) {
                        Log.i("AuthQuickstart", "Sign in succeeded")
                    } else {
                        Log.i("AuthQuickstart", "Sign in not complete")
                    }
                },
                { Log.e("AuthQuickstart", "Failed to sign in", it) }
            )

            Amplify.DataStore.query(
                User::class.java,
                Where.matches(User.USERNAME.eq(usernameEditText.text.toString())),
                { result ->
                    if (result.hasNext()) {
                        val user = result.next()

                        // Access user data
                        val username = user.username
                        val funds = user.funds

                        Log.i("Amplify", "Retrieved User Data: $user")
                        Log.i("Amplify", "User Funds: $funds")
                    } else {
                        Log.i("Amplify", "User not found")
                    }
                },
                { error ->
                    Log.e("Amplify", "Error querying User", error)
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