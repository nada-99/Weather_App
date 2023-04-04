package com.example.weatherapp.network

import com.example.weatherapp.model.WeatherResponse

class RemoteSourceTest (private var weatherResponse: WeatherResponse):RemoteSource{
    override suspend fun getWeatherFromApi(
        latitude: Double,
        longitude: Double,
        lang: String?,
        units: String?
    ): WeatherResponse {
        return weatherResponse
    }

}