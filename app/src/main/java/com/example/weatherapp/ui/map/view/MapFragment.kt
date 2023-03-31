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
    lateinit var geoCoder: Geocoder
    lateinit var address: String
    lateinit var myFactory: MapViewModelFactory
    lateinit var viewModel: MapViewModel
    lateinit var favoriteLocation: FavoriteLocation

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
        mapSharedPreferences = requireActivity().getSharedPreferences(Constants.SP_Fav, Context.MODE_PRIVATE)
        geoCoder = Geocoder(requireContext(), Locale.getDefault())

        myFactory = MapViewModelFactory(
            Repository.getInstance(
                WeatherClient.getInstance(),
                ConcreteLocalSource(requireContext()),
                requireContext()
            )
        )
        viewModel = ViewModelProvider(this, myFactory).get(MapViewModel::class.java)

        val supportMapFragment : SupportMapFragment = getChildFragmentManager().findFragmentById(R.id.map_fragment) as SupportMapFragment
        supportMapFragment.getMapAsync(object : OnMapReadyCallback {
            override fun onMapReady(googleMap: GoogleMap) {
                googleMap.setOnMapClickListener {
                    val marker = MarkerOptions()
                    marker.position(it)
                    googleMap.clear()
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(it,5f))
                    googleMap.addMarker(marker)

                    favoriteLocation.latitude = it.latitude
                    favoriteLocation.longitude = it.longitude
                    Log.i("Lat&Long Fav", "onMapClick: ${favoriteLocation.latitude} , ${favoriteLocation.longitude}")

                    val addresses = geoCoder.getFromLocation(it.latitude, it.longitude, 1)
                    val city = addresses!![0].locality
                    val country = addresses[0].countryName
                    address = country+ "/"+ city

//                    mapSharedPreferences.edit().putString(Constants.lat, it.latitude.toString()).apply()
//                    mapSharedPreferences.edit().putString(Constants.long, it.longitude.toString()).apply()

                    if(city != null) {
                        favoriteLocation.address = address
//                        mapSharedPreferences.edit().putString(Constants.address, address).apply()
                        Log.i("Fav Address", "cityName: ${favoriteLocation.address}")
                    }else{
                        Toast.makeText(requireContext(),"i can't found city",Toast.LENGTH_LONG)
                            .show()
                    }

                    binding.addToFavBtn.setOnClickListener {
                        Navigation.findNavController(view).navigate(R.id.action_mapFragment_to_favoriteFragment)
                        viewModel.insertFavLocationToDB(favoriteLocation)
                    }
                }
            }

        } )

    }

}