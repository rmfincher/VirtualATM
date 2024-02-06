package com.example.myapplication.ui.receive

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationListener;
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
import com.example.myapplication.databinding.FragmentReceiveBinding
import com.example.myapplication.ui.receive.ReceiveViewModel
import com.example.myapplication.ui.home.HomeViewModel
import androidx.core.content.ContextCompat;
import androidx.core.content.PackageManagerCompat;
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.getSystemService

class ReceiveFragment : Fragment() {

    private var _binding: FragmentReceiveBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private var locationByGps: Location? = null
    private var locationByNetwork: Location? = null
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val receiveViewModel =
            ViewModelProvider(this).get(ReceiveViewModel::class.java)

        _binding = FragmentReceiveBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textReceive
        receiveViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        val sendButton: Button = root.findViewById(R.id.buttonSend)
        val coordsEditText: EditText = root.findViewById(R.id.editTextCoords)

        /* sendButton.setOnClickListener {
            val userCoords = coordsEditText.text.toString().toDouble()

        val exampleRecipient1 = User.builder()
                .coords(userCoords)
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
                .coords(userCoords)
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

        }*/

        fun isLocationPermissionGranted(): Boolean {
            val requestcode = 0
            return if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    requireContext(),
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(
                        android.Manifest.permission.ACCESS_FINE_LOCATION,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION
                    ),
                    requestcode
                )
                false
            } else {
                true
            }
        }


        var currentLocation: Location? = null
        val locationManager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager

        val hasGps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val hasNetwork = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

        val gpsLocationListener: LocationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                locationByGps = location
            }

            override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
            override fun onProviderEnabled(provider: String) {}
            override fun onProviderDisabled(provider: String) {}
        }

        val networkLocationListener: LocationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                locationByNetwork = location
            }

            override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
            override fun onProviderEnabled(provider: String) {}
            override fun onProviderDisabled(provider: String) {}
        }

        if (hasGps) {
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                5000,
                0F,
                gpsLocationListener
            )
        }

        if (hasNetwork) {
            locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                5000,
                0F,
                networkLocationListener
            )
        }


        val lastKnownLocationByGps =
            locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        lastKnownLocationByGps?.let {
            locationByGps = lastKnownLocationByGps
        }

        val lastKnownLocationByNetwork =
            locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
        lastKnownLocationByNetwork?.let {
            locationByNetwork = lastKnownLocationByNetwork
        }

        if (locationByGps != null && locationByNetwork != null) {
            if (locationByGps!!.accuracy > locationByNetwork!!.accuracy) {
                currentLocation = locationByGps
                if (currentLocation != null) {
                    latitude = currentLocation.latitude
                }
                if (currentLocation != null) {
                    longitude = currentLocation.longitude
                }

            } else {
                currentLocation = locationByNetwork
                if (currentLocation != null) {
                    latitude = currentLocation.latitude
                }
                if (currentLocation != null) {
                    longitude = currentLocation.longitude
                }

            }
        }


    return root
}



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }




}
