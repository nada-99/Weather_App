package com.example.weatherapp.ui.map

import android.content.Context
import android.content.SharedPreferences
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.example.weatherapp.Constants
import com.example.weatherapp.R
import com.example.weatherapp.databinding.FragmentMapBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.MarkerOptions
import java.util.*

class MapFragment : Fragment() {

    lateinit var binding: FragmentMapBinding
    lateinit var mapSharedPreferences: SharedPreferences
    lateinit var geoCoder: Geocoder
    lateinit var address: String

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
        binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mapSharedPreferences = requireActivity().getSharedPreferences(Constants.SP_Fav, Context.MODE_PRIVATE)
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
                    Log.i("Lat&Long Fav", "onMapClick: ${it.latitude} , ${it.longitude}")

                    val addresses = geoCoder.getFromLocation(it.latitude, it.longitude, 1)
                    val city = addresses!![0].locality
                    val country = addresses[0].countryName
                    address = country+ "/"+ city

                    mapSharedPreferences.edit().putString(Constants.lat, it.latitude.toString()).apply()
                    mapSharedPreferences.edit().putString(Constants.long, it.longitude.toString()).apply()

                    if(address != null) {
                        mapSharedPreferences.edit().putString(Constants.address, address).apply()
                        Log.i("Fav Address", "cityName: ${address}")
                    }else{
                        Toast.makeText(requireContext(),"i can't found country",Toast.LENGTH_LONG)
                            .show()
                        // editorLocationMap.putString("cityNameMap", "").commit()
                    }

                    binding.addToFavBtn.setOnClickListener {
                        Navigation.findNavController(view).navigate(R.id.action_mapFragment_to_favoriteFragment)
                    }
                }
            }

        } )

    }

}