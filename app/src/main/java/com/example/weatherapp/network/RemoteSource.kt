package com.example.weatherapp.network

import com.example.weatherapp.model.WeatherResponse
import retrofit2.http.Query

interface RemoteSource {
    suspend fun getWeatherFromApi(
        latitude: Double,
        longitude: Double,
        lang: String?,
        units: String?
    ): WeatherResponse
}