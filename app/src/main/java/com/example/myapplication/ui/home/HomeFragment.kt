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
import com.amplifyframework.auth.cognito.result.AWSCognitoAuthSignOutResult
import com.amplifyframework.core.model.query.Where
import com.amplifyframework.datastore.generated.model.User
import com.example.myapplication.ui.SharedViewModel
import android.os.Handler

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var sharedViewModel: SharedViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
                ViewModelProvider(this).get(HomeViewModel::class.java)

        sharedViewModel =
            ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textHome
        homeViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        // Home View Model values
        val signUpButton: Button = root.findViewById(R.id.signUpButton)
        val signInButton: Button = root.findViewById(R.id.signInButton)
        val verifyCodeButton: Button = root.findViewById(R.id.verifyCodeButton)
        val emailEditText: TextView = root.findViewById(R.id.emailEditText)
        val usernameEditText: TextView = root.findViewById(R.id.usernameEditText)
        val passwordEditText: TextView = root.findViewById(R.id.passwordEditText)
        val authCodeEditText: TextView = root.findViewById(R.id.authCodeEditText)

        // Implementing Sign-Up Button logic with Amplify
        signUpButton.setOnClickListener {

            // Check if the user with the given username already exists, if not -> create user
            Amplify.DataStore.query(
                User::class.java,
                Where.matches(User.USERNAME.eq(usernameEditText.text.toString())),
                { result ->
                    if (result.hasNext()) {
                        // User with the same username already exists
                        Log.i("Amplify", "User with the username already exists")
                    } else {
                        // User does not exist, proceed with creating and saving a new user

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

                        try {
                            sharedViewModel.updateUsername(usernameEditText.text.toString())
                        } catch (e: Exception) {
                            Log.e("HomeFragment", "Error updating username", e)
                        }

                        // build the user
                        val createdUser = User.builder()
                            .username(usernameEditText.text.toString())
                            .funds(100.0)
                            .longitude(0.0)
                            .latitude(0.0)
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
                },
                { error ->
                    Log.e("Amplify", "Error querying User", error)
                }
            )
        }

        // Implementing Sign-In Button logic with Amplify
        signInButton.setOnClickListener {

            // sign out user
            Amplify.Auth.signOut { signOutResult ->
                when (signOutResult) {
                    is AWSCognitoAuthSignOutResult.CompleteSignOut -> {
                        // Sign Out completed fully and without errors.
                        Log.i("AuthQuickStart", "Signed out successfully")
                    }

                    is AWSCognitoAuthSignOutResult.PartialSignOut -> {
                        // Sign Out completed with some errors. User is signed out of the device.
                        signOutResult.hostedUIError?.let {
                            Log.e("AuthQuickStart", "HostedUI Error", it.exception)
                            // Optional: Re-launch it.url in a Custom tab to clear Cognito web session.

                        }
                        signOutResult.globalSignOutError?.let {
                            Log.e("AuthQuickStart", "GlobalSignOut Error", it.exception)
                            // Optional: Use escape hatch to retry revocation of it.accessToken.
                        }
                        signOutResult.revokeTokenError?.let {
                            Log.e("AuthQuickStart", "RevokeToken Error", it.exception)
                            // Optional: Use escape hatch to retry revocation of it.refreshToken.
                        }
                    }

                    is AWSCognitoAuthSignOutResult.FailedSignOut -> {
                        // Sign Out failed with an exception, leaving the user signed in.
                        Log.e("AuthQuickStart", "Sign out Failed", signOutResult.exception)
                    }
                }
            }

            Amplify.Auth.signIn(usernameEditText.text.toString(), passwordEditText.text.toString(),
                { result ->
                    if (result.isSignedIn) {
                        Log.i("AuthQuickstart", "Sign in succeeded")

                        try {
                            sharedViewModel.updateUsername(usernameEditText.text.toString())
                        } catch (e: Exception) {
                            Log.e("HomeFragment", "Error updating username", e)
                        }

                    } else {
                        Log.i("AuthQuickstart", "Sign in not complete")
                    }
                },
                { Log.e("AuthQuickstart", "Failed to sign in", it) }
            )
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
