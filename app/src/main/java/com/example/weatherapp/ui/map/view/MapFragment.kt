package com.example.weatherapp.ui.map.view

import android.content.Context
import android.content.SharedPreferences
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.example.weatherapp.Constants
import com.example.weatherapp.R
import com.example.weatherapp.database.ConcreteLocalSource
import com.example.weatherapp.databinding.FragmentMapBinding
import com.example.weatherapp.getAddressGeoCoder
import com.example.weatherapp.model.FavoriteLocation
import com.example.weatherapp.model.Repository
import com.example.weatherapp.network.WeatherClient
import com.example.weatherapp.ui.home.viewmodel.HomeViewModel
import com.example.weatherapp.ui.home.viewmodel.HomeViewModelFactory
import com.example.weatherapp.ui.map.viewmodel.MapViewModel
import com.example.weatherapp.ui.map.viewmodel.MapViewModelFactory
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.MarkerOptions
import java.util.*

class MapFragment : Fragment() {

    lateinit var binding: FragmentMapBinding
    lateinit var mapSharedPreferences: SharedPreferences
    lateinit var myFactory: MapViewModelFactory
    lateinit var viewModel: MapViewModel
    lateinit var favoriteLocation: FavoriteLocation
    var lat: Double? = null
    var long: Double? = null

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

        favoriteLocation = FavoriteLocation()
        mapSharedPreferences =
            requireActivity().getSharedPreferences(Constants.SP_Fav, Context.MODE_PRIVATE)

        myFactory = MapViewModelFactory(
            Repository.getInstance(
                WeatherClient.getInstance(),
                ConcreteLocalSource(requireContext()),
                requireContext()
            )
        )
        viewModel = ViewModelProvider(this, myFactory).get(MapViewModel::class.java)

        val supportMapFragment: SupportMapFragment =
            getChildFragmentManager().findFragmentById(R.id.map_fragment) as SupportMapFragment
        supportMapFragment.getMapAsync(object : OnMapReadyCallback {
            override fun onMapReady(googleMap: GoogleMap) {
                googleMap.setOnMapClickListener {
                    val marker = MarkerOptions()
                    marker.position(it)
                    googleMap.clear()
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(it, 5f))
                    googleMap.addMarker(marker)

                    lat = it.latitude
                    long = it.longitude
                    favoriteLocation.latitude = it.latitude
                    favoriteLocation.longitude = it.longitude

                    Log.i(
                        "Lat&Long Fav",
                        "onMapClick: ${favoriteLocation.latitude} , ${favoriteLocation.longitude}"
                    )

                    favoriteLocation.address_en =
                        getAddressGeoCoder(it.latitude, it.longitude, requireContext(), "en")
                    favoriteLocation.address_ar =
                        getAddressGeoCoder(it.latitude, it.longitude, requireContext(), "ar")

                }
            }

        })

        binding.addToFavBtn.setOnClickListener {
            if (lat == null && long == null) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.notValidMap),
                    Toast.LENGTH_LONG
                ).show()
            } else {
                viewModel.insertFavLocationToDB(favoriteLocation)
                Navigation.findNavController(requireView()).navigateUp()
            }
        }

        binding.cancelButton.setOnClickListener {
            requireActivity().onBackPressed()
        }

    }

}