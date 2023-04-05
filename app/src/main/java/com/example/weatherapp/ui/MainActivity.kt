package com.example.weatherapp.ui

import android.Manifest
import android.annotation.SuppressLint
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
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.Navigation.findNavController
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.example.weatherapp.Constants
import com.example.weatherapp.R
import com.example.weatherapp.database.ConcreteLocalSource
import com.example.weatherapp.isInternetConnected
import com.example.weatherapp.model.Repository
import com.example.weatherapp.model.ResponseState
import com.example.weatherapp.network.WeatherClient
import com.example.weatherapp.ui.home.view.PERMISSION_ID
import com.example.weatherapp.ui.home.viewmodel.HomeViewModel
import com.example.weatherapp.ui.home.viewmodel.HomeViewModelFactory
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Locale


class MainActivity : AppCompatActivity() {

    lateinit var drawerLayout: DrawerLayout
    lateinit var navigationView: NavigationView
    lateinit var navController: NavController
    var lat: Double = 0.0
    var long: Double = 0.0
    lateinit var mFusedLocationClient: FusedLocationProviderClient
    lateinit var sharedPreference: SharedPreferences
    lateinit var myFactory: HomeViewModelFactory
    lateinit var viewModel: HomeViewModel
    lateinit var locationSharedPreference:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sharedPreference =
            applicationContext.getSharedPreferences(Constants.SP_Key, Context.MODE_PRIVATE)
        locationSharedPreference = sharedPreference.getString(Constants.LocationFrom,"")!!

        //GPs
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        myFactory = HomeViewModelFactory(
            Repository.getInstance(
                WeatherClient.getInstance(),
                ConcreteLocalSource(this),
                this
            )
        )
        viewModel = ViewModelProvider(this, myFactory).get(HomeViewModel::class.java)

        if(isInternetConnected(this) && locationSharedPreference == Constants.Loction_Enum.gps.toString()){
            getLastLocation()
//            GlobalScope.launch(Dispatchers.Main) {
//                getLastLocation()
//            }
        }
        //setup navigation drawer
        getSupportActionBar()?.setElevation(0F)
        getSupportActionBar()?.setDisplayShowTitleEnabled(false)

        navigationView = findViewById(R.id.navigation_view)
        drawerLayout = findViewById(R.id.drawerLayout)

        val actionBar = supportActionBar
        actionBar?.setHomeAsUpIndicator(R.drawable.menu_icon_24)
        actionBar!!.setDisplayShowHomeEnabled(true)
        actionBar.setDisplayHomeAsUpEnabled(true)

        navController = findNavController(this, R.id.nav_host_fragment)
        setupWithNavController(navigationView, navController)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START)
            } else {
                drawerLayout.openDrawer(GravityCompat.START)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        if (checkPermissions())
            if(isInternetConnected(this)&& locationSharedPreference == Constants.Loction_Enum.gps.toString()){
                getLastLocation()
            }
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_ID) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
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
                Toast.makeText(this, "Turn on Location", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }

        } else {
            requestPermissions()
        }
    }
    private fun checkPermissions(): Boolean {
        val result = ActivityCompat.checkSelfPermission(
            this, android.Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
            this, android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        return result
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this, arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ), PERMISSION_ID
        )
    }

    private fun isLocationEnable(): Boolean {
        val locationManager: LocationManager =
            this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {
        val mLocationRequest = com.google.android.gms.location.LocationRequest()
        mLocationRequest.setPriority(com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY)
        mLocationRequest.setInterval(0)
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
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

                sharedPreference.edit().putString(Constants.lat, lat.toString()).apply()
                sharedPreference.edit().putString(Constants.long, long.toString()).apply()

/*                lifecycleScope.launch {
                    sharedPreference.edit().putString(Constants.lat, lat.toString()).apply()
                    sharedPreference.edit().putString(Constants.long, long.toString()).apply()
//                    sharedPreference.edit().putString(Constants.address, address).apply()
                    viewModel.weatherData.collectLatest { result ->
                        when (result) {
                            is ResponseState.Loading -> {
                                Log.i("LOADING", "LOADING: ")
                            }
                            is ResponseState.Success -> {
                                viewModel.deleteCurrentWeatherFromDB()
                                viewModel.insertCurrentWeatherToDB(result.data)
                                Log.i("DATAAAA", "${result.data.current.temp}")
                            }
                            else -> {
                                Log.i("ERRRRORR", "ERRRRORR: ")
                            }
                        }
                    }
                }*/

            }

            mFusedLocationClient.removeLocationUpdates(this)
        }
    }

}