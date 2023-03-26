package com.example.weatherapp.model

import kotlinx.coroutines.flow.Flow

interface RepositoryInterface {

    //call api to get weather data
    suspend fun getWeatherResponseFromApi(latitude: Double, longitude: Double): Flow<WeatherResponse>
    suspend fun getCurrentWeatherFromDB() : Flow<WeatherResponse>
    suspend fun insertCurrentWeatherToDB(weatherResponse: WeatherResponse)
}