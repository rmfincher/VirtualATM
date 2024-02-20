package com.example.myapplication.ui.withdraw

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.databinding.FragmentWithdrawBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class WithdrawFragment : Fragment() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var _binding: FragmentWithdrawBinding? = null
    private val binding get() = _binding!!
    private val withdrawViewModel: WithdrawViewModel by viewModels()

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

        // Initialize FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

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
                    }
                }
        }
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
    }
}
