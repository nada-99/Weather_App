package com.example.weatherapp.model

import kotlinx.coroutines.flow.Flow

interface RepositoryInterface {

    //call api to get weather data
    suspend fun getWeatherResponseFromApi(
        latitude: Double,
        longitude: Double,
        exclude: String,
        lang: String,
        units: String,
        apiKey: String
    ): Flow<WeatherResponse>
}