package com.example.weatherapp.model

import kotlinx.coroutines.flow.Flow

interface RepositoryInterface {

    //call api to get weather data
    suspend fun getWeatherResponseFromApi(latitude: Double, longitude: Double): Flow<WeatherResponse>
    //Current Weather From Database
    suspend fun getCurrentWeatherFromDB() : Flow<WeatherResponse>
    suspend fun insertCurrentWeatherToDB(weatherResponse: WeatherResponse)
    suspend fun deleteCurrentWeatherToDB()

    //Favorite Locations From Database
    suspend fun getFavLocationsFromDB() : Flow<List<FavoriteLocation>>
    suspend fun insertFavLocationToDB(favoriteLocation: FavoriteLocation)
    suspend fun deleteFavLocationFromDB(favoriteLocation: FavoriteLocation)

    //Alerts From Database
    suspend fun getAllAlertsFromDB() : Flow<List<MyAlert>>
    suspend fun insertAlertToDB(myAlert: MyAlert)
    suspend fun deleteAlertFromDB(myAlert: MyAlert)

}