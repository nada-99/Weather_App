package com.example.weatherapp.ui.initalsetting

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.example.weatherapp.Constants
import com.example.weatherapp.R
import com.example.weatherapp.database.ConcreteLocalSource
import com.example.weatherapp.isInternetConnected
import com.example.weatherapp.model.APIState
import com.example.weatherapp.model.Repository
import com.example.weatherapp.model.ResponseState
import com.example.weatherapp.network.WeatherClient
import com.example.weatherapp.ui.MainActivity
import com.example.weatherapp.ui.home.view.PERMISSION_ID
import com.example.weatherapp.ui.home.viewmodel.HomeViewModel
import com.example.weatherapp.ui.home.viewmodel.HomeViewModelFactory
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import java.util.*

class InitSettingFragment : Fragment() {

    lateinit var mFusedLocationClient: FusedLocationProviderClient
    lateinit var sharedPreference: SharedPreferences
    var lat: Double = 0.0
    var long: Double = 0.0
    lateinit var dialog : Dialog
    lateinit var myFactory: HomeViewModelFactory
    lateinit var viewModel: HomeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
        return inflater.inflate(R.layout.fragment_init_setting, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val rootView = requireActivity().findViewById<View>(android.R.id.content)

        myFactory = HomeViewModelFactory(
            Repository.getInstance(
                WeatherClient.getInstance(),
                ConcreteLocalSource(requireContext()),
                requireContext()
            )
        )
        viewModel = ViewModelProvider(this, myFactory).get(HomeViewModel::class.java)

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        sharedPreference =
            requireActivity().getSharedPreferences(Constants.SP_Key, Context.MODE_PRIVATE)

//        mapManager = MapManager(requireContext())

        dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.init_setting_dialog)
        val gpsRadioBtn = dialog.findViewById<RadioButton>(R.id.gps_dialog_radioBtn)
        val mapRadioBtn = dialog.findViewById<RadioButton>(R.id.map_dialog_radioBtn)
        val enableRadioBtn = dialog.findViewById<RadioButton>(R.id.enable_dialog_radioBtn)
        val disableRadioBtn = dialog.findViewById<RadioButton>(R.id.disable_dialog_radioBtn)
        val saveBtn = dialog.findViewById<Button>(R.id.saveDialog_btn)
        var locationButtonChecked = false
        var notificationButtonChecked = false

        saveBtn.setOnClickListener {
            if (gpsRadioBtn.isChecked) {
                locationButtonChecked = true
                sharedPreference.edit().putString(Constants.LocationFrom, Constants.Loction_Enum.gps.toString()).apply()
                getLastLocation()

            } else if (mapRadioBtn.isChecked) {
                locationButtonChecked = true
                sharedPreference.edit().putString(Constants.LocationFrom, Constants.Loction_Enum.map.toString()).apply()
                Navigation.findNavController(view).navigate(R.id.action_initSettingFragment_to_mapOrGpsFragment)
            }

            if (enableRadioBtn.isChecked) {
                notificationButtonChecked = true
                sharedPreference.edit().putString(Constants.notification, Constants.notification_Enum.enable.toString()).apply()
            }else if (disableRadioBtn.isChecked) {
                notificationButtonChecked = true
                dialog.dismiss()
                sharedPreference.edit().putString(Constants.notification, Constants.notification_Enum.disable.toString()).apply()
            }

            if(notificationButtonChecked && locationButtonChecked){
                dialog.dismiss()
            }else{
                Toast.makeText(context, "Please select initial setting", Toast.LENGTH_SHORT).show()
            }
        }
        if(isInternetConnected(requireContext())){
            dialog.show()
        }else{
            Snackbar.make(rootView, getString(R.string.checkInternet), Snackbar.LENGTH_LONG).show()
        }


    }

    override fun onResume() {
        super.onResume()
        if(checkPermissions()){
            getLastLocation()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    @SuppressLint("MissingPermission")
    fun getLastLocation() {
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
        val result = ActivityCompat.checkSelfPermission(
            requireContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
            requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        if(result){
            dialog.dismiss()
            val intent = Intent(requireContext(), MainActivity::class.java)
            startActivity(intent)
        }

        return result
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf<String>(android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION
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
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        mFusedLocationClient.requestLocationUpdates(
            mLocationRequest, mLocationCallback,
            Looper.myLooper()
        )
    }

    private val mLocationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val mLastLocation: Location? = locationResult.lastLocation

            if (mLastLocation != null) {
                lat = mLastLocation.latitude
                long = mLastLocation.longitude
                Log.i("Lat & Long", "Lat & Long : $lat, $long")

                viewModel.getWeatherData(lat!!, long!!)

                lifecycleScope.launch {
                    sharedPreference.edit().putString(Constants.lat, lat.toString()).apply()
                    sharedPreference.edit().putString(Constants.long, long.toString()).apply()
                    viewModel.weatherData.collect { result ->
                        when (result) {
                            is ResponseState.Loading -> {
                                Log.i("LOADING", "LOADING: ")
                            }
                            is ResponseState.Success -> {
                                viewModel.insertCurrentWeatherToDB(result.data)
                                Log.i("DATAAAA", "${result.data.current.temp}")
                            }
                            else -> {
                                Log.i("ERRRRORR", "ERRRRORR: ")
                            }
                        }
                    }
                }

            }
            mFusedLocationClient.removeLocationUpdates(this)
        }
    }
}