package com.example.weatherapp.ui.initalsetting

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.weatherapp.Constants
import com.example.weatherapp.R
import com.example.weatherapp.databinding.FragmentMapOrGpsBinding
import com.example.weatherapp.ui.MainActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.MarkerOptions
import java.util.*

class MapOrGpsFragment : Fragment() {

    lateinit var binding: FragmentMapOrGpsBinding
    lateinit var mapSharedPreferences: SharedPreferences
    lateinit var geoCoder: Geocoder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
    }

    override fun onStop() {
        super.onStop()
        (activity as AppCompatActivity?)!!.supportActionBar!!.show()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMapOrGpsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mapSharedPreferences = requireActivity().getSharedPreferences(Constants.SP_Key, Context.MODE_PRIVATE)
        geoCoder = Geocoder(requireContext(), Locale.getDefault())

        val supportMapFragment : SupportMapFragment = getChildFragmentManager().findFragmentById(R.id.map_fragment) as SupportMapFragment
        supportMapFragment.getMapAsync(object : OnMapReadyCallback {
            override fun onMapReady(googleMap: GoogleMap) {
                googleMap.setOnMapClickListener {
                    val marker = MarkerOptions()
                    marker.position(it)
                    googleMap.clear()
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(it,5f))
                    googleMap.addMarker(marker)

                    mapSharedPreferences.edit().putString(Constants.lat, it.latitude.toString()).apply()
                    mapSharedPreferences.edit().putString(Constants.long, it.longitude.toString()).apply()

                    Log.i("Lat&Long Fav", "onMapClick: ${it.latitude} , ${it.longitude}")

                    val addresses = geoCoder.getFromLocation(it.latitude, it.longitude, 1)
                    val city = addresses!![0].locality


                    if(city == null) {
                        Toast.makeText(requireContext(), "i can't found city", Toast.LENGTH_LONG)
                            .show()
                    }

                    binding.addToFavBtn.setOnClickListener {
                        val intent = Intent(requireContext(), MainActivity::class.java)
                        startActivity(intent)
                    }
                }
            }

        } )
    }
}