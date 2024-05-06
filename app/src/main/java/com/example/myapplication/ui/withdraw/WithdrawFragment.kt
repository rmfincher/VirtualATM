package com.example.myapplication.ui.withdraw

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.amplifyframework.core.Amplify
import com.amplifyframework.core.model.query.Where
import com.amplifyframework.datastore.generated.model.User
import com.example.myapplication.databinding.FragmentWithdrawBinding
import com.example.myapplication.ui.SharedViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class WithdrawFragment : Fragment() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var _binding: FragmentWithdrawBinding? = null
    private val binding get() = _binding!!
    private val withdrawViewModel: WithdrawViewModel by viewModels()
    private lateinit var sharedViewModel: SharedViewModel
    private var currentUsername = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWithdrawBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textSlideshow
        withdrawViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        // Observe latitude and longitude
        withdrawViewModel.latitude.observe(viewLifecycleOwner) { latitude ->
            binding.latitudeTextView.text = latitude.toString()
        }

        withdrawViewModel.longitude.observe(viewLifecycleOwner) { longitude ->
            binding.longitudeTextView.text = longitude.toString()
        }

        binding.refreshButton.setOnClickListener {
            // Request location update
            requestLocation()
        }

        sharedViewModel =
            ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)

        // Initialize FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        // Observe current username
        sharedViewModel.username.observe(viewLifecycleOwner) { newUsername ->
            Log.i("SendFragment", "Username Observed: $newUsername")

            currentUsername = newUsername
        }

        // Check for location permissions
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        } else {
            // Permission granted, get location
            requestLocation()
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    private fun requestLocation() {
        // Check if permissions are granted
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // Get last known location
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    // Got last known location. In some rare situations, this can be null.
                    if (location != null) {
                        // Update ViewModel with coordinates
                        withdrawViewModel.updateLocation(location.latitude, location.longitude)

                        Amplify.DataStore.query(
                            User::class.java,
                            Where.matches(User.USERNAME.eq(currentUsername)),
                            { result ->
                                if (result.hasNext()) {
                                    val user1 = result.next()

                                    val updatedUser = user1.copyOfBuilder()
                                        .longitude(location.longitude)
                                        .latitude(location.latitude)
                                        .build()

                                    Amplify.DataStore.save(
                                        updatedUser,
                                        { success ->
                                            Log.i("Amplify", "Updated User longitude: ${location.longitude}")
                                            Log.i("Amplify", "Updated User latitude: ${location.latitude}")
                                        },
                                        { error ->
                                            Log.e("Amplify", "Error updating User Location", error)
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
                    }
                }
        }
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
    }
}
