package com.example.weatherapp.database

import com.example.weatherapp.model.FavoriteLocation
import com.example.weatherapp.model.MyAlert
import com.example.weatherapp.model.WeatherResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.junit.Assert.*

class ConcreteLocalSourceTest(
    private var myAlertList: MutableList<MyAlert> = mutableListOf(),
    private var myFavLocation: MutableList<FavoriteLocation> = mutableListOf(),
    private var weatherResponse: WeatherResponse = WeatherResponse()
):LocalSource {

    private var currentWeatherResponse: WeatherResponse? = null

    override fun getCurrentWeather(): Flow<WeatherResponse> = flow {
        emit(weatherResponse)
    }

    override suspend fun insertCurrentWeather(weatherResponse: WeatherResponse) {
        currentWeatherResponse = weatherResponse
    }

    override suspend fun deleteCurrentWeather() {
        currentWeatherResponse = null
    }

    override suspend fun insertToFav(favoriteLocation: FavoriteLocation) {
        myFavLocation?.add(favoriteLocation)
    }

    override suspend fun deleteFavLocation(favoriteLocation: FavoriteLocation) {
        myFavLocation?.remove(favoriteLocation)
    }

    override fun getFavLocations(): Flow<List<FavoriteLocation>> = flow{
        emit(myFavLocation)
    }

    override suspend fun insertAlert(myAlert: MyAlert) {
        myAlertList.add(myAlert)
    }

    override suspend fun deleteAlert(myAlert: MyAlert) {
        myAlertList.remove(myAlert)
    }

    override fun getAllAlerts(): Flow<List<MyAlert>> = flow{
        emit(myAlertList)
    }

}