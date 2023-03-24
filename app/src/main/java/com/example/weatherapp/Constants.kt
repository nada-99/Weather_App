package com.example.weatherapp

import java.text.SimpleDateFormat
import java.util.*

fun getFormattedDate(timestamp: Long): String {
    val date = Date(timestamp * 1000)
    val dateFormat = SimpleDateFormat("EEE , M/d/yyyy", Locale.getDefault())
    return dateFormat.format(date)
}

fun getTimeHourlyFormat(timestamp: Long): String{
    val sdf = SimpleDateFormat("h:mm a", Locale.getDefault())
    val date = Date(timestamp * 1000L)
    return sdf.format(date)
}

fun getDayFormat(timestamp: Long): String{
    val sdf = SimpleDateFormat("EE", Locale.getDefault())
    val date = Date(timestamp * 1000L)
    return sdf.format(date)
}

fun getIconOfWeather(icon: String?):Int{
    var idOfIcon = 0
    when(icon){
        "01d" -> idOfIcon = R.drawable.clearsky_sun
        "01n" -> idOfIcon = R.drawable.clearsky_moon
        "02d" -> idOfIcon = R.drawable.fewclouds_sun
        "02n" -> idOfIcon = R.drawable.few_clouds_moon
        "03d" , "03n" -> idOfIcon = R.drawable.scattered_clouds
        "04d" , "04n" -> idOfIcon = R.drawable.broken_clouds
        "09d" , "09n" -> idOfIcon = R.drawable.shower_rain
        "10d" , "10n" -> idOfIcon = R.drawable.rain
        "11d" , "11n" -> idOfIcon = R.drawable.thunderstorm
        "13d" , "13n" -> idOfIcon = R.drawable.snow
        "50d" , "50n" -> idOfIcon = R.drawable.mist
    }
    return idOfIcon
}

fun getLottiOfWeather(icon: String?):Int{
    var idOfIcon = 0
    when(icon){
        "01d" -> idOfIcon = R.raw.sun_clearsky
        "01n" -> idOfIcon = R.raw.clearsky_night
        "02d" -> idOfIcon = R.raw.fewcloud_sun
        "02n" -> idOfIcon = R.raw.fewcloud_moon
        "03d" , "03n" -> idOfIcon = R.raw.scattered_clouds
        "04d" , "04n" -> idOfIcon = R.raw.broken_clouds
        "09d" , "09n" -> idOfIcon = R.raw.shower_rain
        "10d" -> idOfIcon = R.raw.rain_sun
        "10n" -> idOfIcon = R.raw.rain_night
        "11d" , "11n" -> idOfIcon = R.raw.thunderstorm
        "13d" -> idOfIcon = R.raw.snow_sun
        "13n" -> idOfIcon = R.raw.snow_night
        "50d" , "50n" -> idOfIcon = R.raw.mist
    }
    return idOfIcon
}