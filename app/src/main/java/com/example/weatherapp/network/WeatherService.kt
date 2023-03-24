package com.example.weatherapp.network

import com.example.weatherapp.Constants
import com.example.weatherapp.model.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {
    @GET("onecall")
    suspend fun getWeather(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("lang") lang: String,
        @Query("units") units: String,
        @Query("exclude") exclude: String = Constants.exclude,
        @Query("appid") apiKey: String = Constants.API_KEY
    ): WeatherResponse
}