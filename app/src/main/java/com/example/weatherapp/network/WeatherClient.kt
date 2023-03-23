package com.example.weatherapp.network

import com.example.weatherapp.model.WeatherResponse

class WeatherClient private constructor() : RemoteSource {

    val weatherService : WeatherService by lazy {
        RetrofitHelper.getInstance().create(WeatherService::class.java)
    }

    override suspend fun getWeatherFromApi(
        latitude: Double,
        longitude: Double,
        exclude: String,
        lang: String,
        units: String,
        apiKey: String
    ): WeatherResponse {
        return weatherService.getWeather(latitude,longitude, exclude, lang, units, apiKey)
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