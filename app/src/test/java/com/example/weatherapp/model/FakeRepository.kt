package com.example.weatherapp.model

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeRepository(
    private var myAlertList: MutableList<MyAlert> = mutableListOf(),
    private var myFavLocation: MutableList<FavoriteLocation> = mutableListOf(),
    private var weatherResponse: WeatherResponse = WeatherResponse()
) : RepositoryInterface {

    private var currentWeatherResponse: WeatherResponse? = null

    override suspend fun getWeatherResponseFromApi(
        latitude: Double,
        longitude: Double
    ): Flow<WeatherResponse> = flow {
        emit(weatherResponse)
    }

    override suspend fun getCurrentWeatherFromDB(): Flow<WeatherResponse> = flow {
        emit(weatherResponse)
    }

    override suspend fun insertCurrentWeatherToDB(weatherResponse: WeatherResponse) {
        currentWeatherResponse = weatherResponse
    }

    override suspend fun deleteCurrentWeatherToDB() {
        currentWeatherResponse = null
    }

    override suspend fun getFavLocationsFromDB(): Flow<List<FavoriteLocation>> = flow {
        emit(myFavLocation)
    }

    override suspend fun insertFavLocationToDB(favoriteLocation: FavoriteLocation) {
        myFavLocation.add(favoriteLocation)
    }

    override suspend fun deleteFavLocationFromDB(favoriteLocation: FavoriteLocation) {
        myFavLocation.remove(favoriteLocation)
    }

    override suspend fun getAllAlertsFromDB(): Flow<List<MyAlert>> = flow{
        emit(myAlertList)
    }

    override suspend fun insertAlertToDB(myAlert: MyAlert) {
        myAlertList.add(myAlert)
    }

    override suspend fun deleteAlertFromDB(myAlert: MyAlert) {
        myAlertList.remove(myAlert)
    }
}