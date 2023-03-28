package com.example.weatherapp.database

import com.example.weatherapp.model.WeatherResponse
import kotlinx.coroutines.flow.Flow

interface LocalSource {
    fun getCurrentWeather() : Flow<WeatherResponse>
    suspend fun insertCurrentWeather(weatherResponse: WeatherResponse)
    suspend fun deleteCurrentWeather()
}