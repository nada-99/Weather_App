package com.example.weatherapp.database

import com.example.weatherapp.model.FavoriteLocation
import com.example.weatherapp.model.WeatherResponse
import kotlinx.coroutines.flow.Flow

interface LocalSource {
    //Current Weather
    fun getCurrentWeather() : Flow<WeatherResponse>
    suspend fun insertCurrentWeather(weatherResponse: WeatherResponse)
    suspend fun deleteCurrentWeather()

    //Favorite Locations
    suspend fun insertToFav(favoriteLocation: FavoriteLocation)
    suspend fun deleteFavLocation(favoriteLocation: FavoriteLocation)
    fun getFavLocations() : Flow<List<FavoriteLocation>>
    //suspend fun getFavLocations() : List<FavoriteLocation>
}