package com.example.weatherapp

import android.content.Context
import android.location.Geocoder
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

object Constants {
    const val BASE_URL = "https://api.openweathermap.org/data/3.0/"
    const val API_KEY = "40dac0af7018969cbb541943f944ba29"
    const val SP_Key = "my_weather"
    const val SP_Fav = "FromFav"
    const val firstTime = "firstTime"
    const val language = "language"
    const val temperature = "temperature"
    const val windSpeed = "windSpeed"
    const val exclude = "minutely"
    const val lat = "lat"
    const val long = "long"
    const val unit = "unit"
    const val address = "address"
    const val LocationFrom = "locationFrom"
    const val notification = "notification"
    const val latMap = "latMap"
    const val longMap = "longMap"

    enum class Loction_Enum() { map, gps }
    enum class Language_Enum() { en, ar }
    enum class Units_Enum() { standard, metric, imperial }
    enum class WindSpeed_Enum() { meter,mile }
    enum class notification_Enum() { enable,disable }
}

fun getFormattedDate(timestamp: Long, language: String): String {
    val date = Date(timestamp * 1000)
    val dateFormat = SimpleDateFormat("EEE , M/d/yyyy", Locale(language))
    return dateFormat.format(date)
}

fun getTimeHourlyFormat(timestamp: Long, language: String): String {
    val sdf = SimpleDateFormat("h:mm a", Locale(language))
    val date = Date(timestamp * 1000L)
    return sdf.format(date)
}

fun getDayFormat(timestamp: Long, language: String): String {
    val sdf = SimpleDateFormat("EE", Locale(language))
    val date = Date(timestamp * 1000L)
    return sdf.format(date)
}

fun getDateToAlert(timestamp: Long, language: String): String{
    return SimpleDateFormat("M/d/yyyy",Locale(language)).format(timestamp)
}

fun getTimeToAlert(timestamp: Long, language: String): String{
    return SimpleDateFormat("h:mm a",Locale(language)).format(timestamp)
}

fun getIconOfWeather(icon: String?): Int {
    var idOfIcon = 0
    when (icon) {
        "01d" -> idOfIcon = R.drawable.clearsky_sun
        "01n" -> idOfIcon = R.drawable.clearsky_moon
        "02d" -> idOfIcon = R.drawable.fewclouds_sun
        "02n" -> idOfIcon = R.drawable.few_clouds_moon
        "03d", "03n" -> idOfIcon = R.drawable.scattered_clouds
        "04d", "04n" -> idOfIcon = R.drawable.broken_clouds
        "09d", "09n" -> idOfIcon = R.drawable.shower_rain
        "10d", "10n" -> idOfIcon = R.drawable.rain
        "11d", "11n" -> idOfIcon = R.drawable.thunderstorm
        "13d", "13n" -> idOfIcon = R.drawable.snow
        "50d", "50n" -> idOfIcon = R.drawable.mist
    }
    return idOfIcon
}

fun getLottiOfWeather(icon: String?): Int {
    var idOfIcon = 0
    when (icon) {
        "01d" -> idOfIcon = R.raw.sun_clearsky
        "01n" -> idOfIcon = R.raw.clearsky_night
        "02d" -> idOfIcon = R.raw.fewcloud_sun
        "02n" -> idOfIcon = R.raw.fewcloud_moon
        "03d", "03n" -> idOfIcon = R.raw.scattered_clouds
        "04d", "04n" -> idOfIcon = R.raw.broken_clouds
        "09d", "09n" -> idOfIcon = R.raw.shower_rain
        "10d" -> idOfIcon = R.raw.rain_sun
        "10n" -> idOfIcon = R.raw.rain_night
        "11d", "11n" -> idOfIcon = R.raw.thunderstorm
        "13d" -> idOfIcon = R.raw.snow_sun
        "13n" -> idOfIcon = R.raw.snow_night
        "50d", "50n" -> idOfIcon = R.raw.mist
    }
    return idOfIcon
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

fun convertToArabicNumber(number: Int): String {
    val nf = NumberFormat.getInstance(Locale("ar"))
    return nf.format(number)
}
fun getAddressGeoCoder(latitude: Double?, longitude: Double?, context: Context , language: String): String {
    var address = ""
    val geocoder = Geocoder(context,  Locale(language))
    val addresses = geocoder.getFromLocation(latitude!!, longitude!!, 1)
    if (addresses != null && addresses.size > 0) {
        val city = addresses!![0].locality
        val country = addresses[0].countryName
//        val state = addresses[0].adminArea
        val knownName = addresses[0].featureName
        if(city == null){
            address = country
        }else{
            address = country+ "/"+ city
        }
        if(city == null && country == null)
            address = ""
    }
    return address
}

fun getUnit(unit:String,language: String):String{
    var result = ""
    if (language == "en" && unit == Constants.Units_Enum.metric.toString()){
        result = "°c"
    }
    if (language == "en" && unit == Constants.Units_Enum.imperial.toString()){
        result = "°F"
    }
    if (language == "en" && unit == Constants.Units_Enum.standard.toString()){
        result = "K"
    }
    if (language == "ar" && unit == Constants.Units_Enum.metric.toString()){
        result = "°م"
    }
    if (language == "ar" && unit == Constants.Units_Enum.imperial.toString()){
        result = "°ف"
    }
    if (language == "ar" && unit == Constants.Units_Enum.standard.toString()){
        result = "°ك"
    }
    return result
}

fun getCurrentSpeed(context: Context): String {
    val sharedPreference =  context.getSharedPreferences(Constants.SP_Key, Context.MODE_PRIVATE)
    return when (  sharedPreference.getString(Constants.unit,Constants.Units_Enum.standard.toString())) {
        Constants.Units_Enum.metric.toString() -> {
            context.getString(R.string.meter_sec)
        }
        Constants.Units_Enum.imperial.toString()-> {
            context.getString(R.string.mile_hour)
        }
        Constants.Units_Enum.standard.toString() -> {
            context.getString(R.string.meter_sec)
        }
        else -> {
            context.getString(R.string.meter_sec)
        }
    }
}

fun getWindSpeed(windSpeedUnit:String , unit:String , windSpeed:Double , context: Context):String{
    var result = ""
    if(windSpeedUnit == Constants.WindSpeed_Enum.meter.toString() && unit == Constants.Units_Enum.imperial.toString()){
        result = (0.44704*(windSpeed).toInt()).toString() + context.getString(R.string.meter_sec)
    }else if(windSpeedUnit == Constants.WindSpeed_Enum.mile.toString() && (unit == Constants.Units_Enum.metric.toString() || unit == Constants.Units_Enum.standard.toString())){
        result = (2.23694*(windSpeed).toInt()).toString()+ context.getString(R.string.mile_hour)
    }else{
        result = windSpeed.toInt().toString() + getCurrentSpeed(context)
    }
    return result
}