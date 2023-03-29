package com.example.weatherapp.ui.home.view

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieDrawable
import com.example.weatherapp.Constants
import com.example.weatherapp.database.ConcreteLocalSource
import com.example.weatherapp.databinding.FragmentHomeBinding
import com.example.weatherapp.getFormattedDate
import com.example.weatherapp.getLottiOfWeather
import com.example.weatherapp.model.Repository
import com.example.weatherapp.model.WeatherResponse
import com.example.weatherapp.model.APIState
import com.example.weatherapp.network.WeatherClient
import com.example.weatherapp.ui.home.viewmodel.HomeViewModel
import com.example.weatherapp.ui.home.viewmodel.HomeViewModelFactory
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

const val PERMISSION_ID = 44

class HomeFragment : Fragment() {

    lateinit var binding: FragmentHomeBinding
    lateinit var myFactory: HomeViewModelFactory
    lateinit var viewModel: HomeViewModel
    lateinit var hourlyAdapter: HourlyAdapter
    lateinit var dailyAdapter: DailyAdapter
    lateinit var sharedPreference: SharedPreferences
//    lateinit var lat_ :String
//    lateinit var long_ :String

    //    var latitude: Double = 0.0
//    var longitude: Double = 0.0
//    lateinit var mFusedLocationClient: FusedLocationProviderClient
//    lateinit var geoCoder: Geocoder
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPreference =
            requireActivity().getSharedPreferences(Constants.SP_Key, Context.MODE_PRIVATE)
        var lat_ = sharedPreference.getString(Constants.lat, "")?.toDoubleOrNull()
        var long_ = sharedPreference.getString(Constants.long, "")?.toDoubleOrNull()

        //Hourly Adapter
        hourlyAdapter = HourlyAdapter(ArrayList(), requireActivity())
        val layoutManager = LinearLayoutManager(requireActivity(), RecyclerView.HORIZONTAL, false)
        binding.hourlyRecyclerView.layoutManager = layoutManager

        //daily Adapter
        dailyAdapter = DailyAdapter(ArrayList(), requireActivity())
        val layoutManagerDaily =
            LinearLayoutManager(requireActivity(), RecyclerView.VERTICAL, false)
        binding.dailyRecyclerView.layoutManager = layoutManagerDaily

        myFactory = HomeViewModelFactory(
            Repository.getInstance(
                WeatherClient.getInstance(),
                ConcreteLocalSource(requireContext()),
                requireContext()
            )
        )
        viewModel = ViewModelProvider(this, myFactory).get(HomeViewModel::class.java)


        if (isInternetConnected(requireContext())) {
            //call Api
            //getLastLocation()
        } else {
            //call DataBase
            //viewModel.getCurrentWeather()
            Toast.makeText(context, "Please Check your internet connection", Toast.LENGTH_SHORT)
                .show()

//            Snackbar.make(
//                view,
//                "Please Check your internet connection",
//                Snackbar.LENGTH_LONG
//            ).show()
        }
//        val sharedPreference =
//            requireContext().getSharedPreferences(Constants.SP_Key, Context.MODE_PRIVATE)
//        var latitude = sharedPreference.getString(Constants.lat, "")!!
//        var longitude = sharedPreference.getString(Constants.long, "")!!
        Log.i("LLLat", "$lat_ + $long_")

        if (lat_ != null && long_ != null) {
            viewModel.getWeatherData(lat_, long_)
        }

        lifecycleScope.launch {
            viewModel.weatherData.collect { result ->
                when (result) {
                    is APIState.Loading -> {
                        Log.i("LOADING", "LOADING: ")
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is APIState.Success -> {
                        binding.progressBar.visibility = View.GONE
                        fitWeatherDataToUi(result.data)
                        viewModel.deleteCurrentWeatherFromDB()
                        viewModel.insertCurrentWeatherToDB(result.data)
                        Log.i("DATAAAA", "${result.data.current.temp}")
                    }
                    else -> {
                        binding.progressBar.visibility = View.GONE
                        Log.i("ERRRRORR", "ERRRRORR: ")
                    }
                }
            }
        }
    }

    fun fitWeatherDataToUi(weatherDetails: WeatherResponse) {
        //first part
        var address = sharedPreference.getString(Constants.address, "")
        binding.cityTv.text = address
        //binding.cityTv.text = weatherDetails.timezone
        binding.dateTv.text = getFormattedDate(weatherDetails.current.dt)
        binding.tempTv.text = weatherDetails.current.temp.toInt().toString() + "°c"
        binding.weatherDesTv.text = weatherDetails.current.weather.firstOrNull()?.description ?: ""
        binding.weatherDesLotti.setAnimation(getLottiOfWeather(weatherDetails.current.weather.firstOrNull()?.icon))
        binding.weatherDesLotti.repeatCount = LottieDrawable.INFINITE

        //hourly recyclerview
        hourlyAdapter.setListHours(weatherDetails.hourly.subList(0, 25))
        hourlyAdapter.notifyDataSetChanged()
        binding.hourlyRecyclerView.adapter = hourlyAdapter

        //Daily recyclerview
        dailyAdapter.setListDaily(weatherDetails.daily)
        dailyAdapter.notifyDataSetChanged()
        binding.dailyRecyclerView.adapter = dailyAdapter

        //last part
        binding.pressureTv.text = weatherDetails.current.pressure.toString()
        binding.humidityTv.text = weatherDetails.current.humidity.toString()
        binding.windTv.text = weatherDetails.current.windSpeed.toString()
        binding.cloudTv.text = weatherDetails.current.clouds.toString()
        binding.ultravioletTv.text = weatherDetails.current.uvi.toString()
        binding.visibilityTv.text = weatherDetails.current.visibility.toString()
    }

    fun isInternetConnected(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork ?: return false
            val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
            return when {
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            val networkInfo = connectivityManager.activeNetworkInfo ?: return false
            return networkInfo.isConnected
        }
    }

    /*override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_ID) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getLastLocation()
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        if (checkPermissions()) {
            if (isLocationEnable()) {
                requestNewLocationData()
            } else {
                Toast.makeText(requireContext(), "Turn on Location", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }

        } else {
            requestPermissions()
        }
    }

    private fun checkPermissions(): Boolean {
        var result = false
        if ((ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED) ||
            (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED)
        ) {
            result = true
        }
        return result
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf<String>(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION
            ), PERMISSION_ID
        )
        requireActivity().recreate()
    }

    private fun isLocationEnable(): Boolean {
        val locationManager: LocationManager =
            requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {
        val mLocationRequest = com.google.android.gms.location.LocationRequest()
        mLocationRequest.setPriority(com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY)
        mLocationRequest.setInterval(0)
        mLocationRequest.setFastestInterval(0)
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        mFusedLocationClient.requestLocationUpdates(
            mLocationRequest, mLocationCallback,
            Looper.myLooper()
        )
    }

    private val mLocationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val mLastLocation: Location? = locationResult.lastLocation

            if (mLastLocation != null) {
                latitude  = mLastLocation.latitude
                longitude = mLastLocation.longitude

                val sharedPreference =  requireActivity().getSharedPreferences(Constants.SP_Key, Context.MODE_PRIVATE)
                sharedPreference.edit().putString(Constants.lat,longitude.toString()).commit()
                sharedPreference.edit().putString(Constants.long,latitude.toString()).commit()
            }
            viewModel.getWeatherData(latitude.toString(),longitude.toString())

            lifecycleScope.launch {
                viewModel.weatherData.collectLatest { result ->
                    when (result) {
                        is APIState.Loading -> {
                            Log.i("LOADING", "LOADING: ")
                            binding.progressBar.visibility = View.VISIBLE
                        }
                        is APIState.Success -> {
                            binding.progressBar.visibility = View.GONE
                            fitWeatherDataToUi(result.data)
                            viewModel.deleteCurrentWeatherFromDB()
                            viewModel.insertCurrentWeatherToDB(result.data)
//                            Log.i("DATAAAA", "${result.data.current.temp}")
                        }
                        else -> {
                            binding.progressBar.visibility = View.GONE
//                            Log.i("ERRRRORR", "ERRRRORR: ")
                        }
                    }
                }
            }

            mFusedLocationClient.removeLocationUpdates(this)
        }
    }*/

}