package com.example.weatherapp.ui.setting

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.example.weatherapp.Constants
import com.example.weatherapp.ui.MainActivity
import com.example.weatherapp.ui.home.view.PERMISSION_ID
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices

class MapManager(private var context: Context) {

    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private lateinit var sharedPreference: SharedPreferences
    private var lat: Double = 0.0
    private var long: Double = 0.0


    @SuppressLint("MissingPermission")
    fun getLastLocation() {
        if (checkPermissions()) {
            if (isLocationEnable()) {
                requestNewLocationData()
            } else {
                Toast.makeText(context, "Turn on Location", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                context.startActivity(intent)
            }
        } else {
            requestPermissions()
        }
    }

    fun checkPermissions(): Boolean {
        val result = ActivityCompat.checkSelfPermission(
            context, android.Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
            context, android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        if(result){
            //dialog.dismiss()
            val intent = Intent(context, MainActivity::class.java)
            context.startActivity(intent)
        }

        return result
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            context as Activity,
            arrayOf<String>(android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION
            ), PERMISSION_ID
        )
        //requireActivity().recreate()
    }

    private fun isLocationEnable(): Boolean {
        val locationManager: LocationManager =
            context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {
        val mLocationRequest = com.google.android.gms.location.LocationRequest()
        mLocationRequest.setPriority(com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY)
        mLocationRequest.setInterval(0)
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
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
                sharedPreference =
                    context.getSharedPreferences(Constants.SP_Key, Context.MODE_PRIVATE)

                sharedPreference.edit().putString(Constants.lat, lat.toString()).apply()
                sharedPreference.edit().putString(Constants.long, long.toString()).apply()
            }
            mFusedLocationClient.removeLocationUpdates(this)
        }
    }
}