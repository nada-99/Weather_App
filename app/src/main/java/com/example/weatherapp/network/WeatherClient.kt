package com.example.weatherapp.network

import com.example.weatherapp.model.WeatherResponse

class WeatherClient private constructor() : RemoteSource {

    val weatherService : WeatherService by lazy {
        RetrofitHelper.getInstance().create(WeatherService::class.java)
    }

    override suspend fun getWeatherFromApi(
        latitude: Double,
        longitude: Double,
        lang: String,
        units: String,
    ): WeatherResponse {
        return weatherService.getWeather(latitude,longitude, lang, units)
    }

    companion object{
        private var instance : WeatherClient? = null
        fun getInstance() : WeatherClient{
            return instance?: synchronized(this){
                val temp = WeatherClient()
                instance = temp
                temp
            }
        }
    }

}