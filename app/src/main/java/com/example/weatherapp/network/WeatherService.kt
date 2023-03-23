package com.example.weatherapp.network

import com.example.weatherapp.model.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {
    @GET("onecall")
    suspend fun getWeather(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("exclude") exclude: String,
        @Query("lang") lang: String,
        @Query("units") units: String,
        @Query("appid") apiKey: String
    ): WeatherResponse
}