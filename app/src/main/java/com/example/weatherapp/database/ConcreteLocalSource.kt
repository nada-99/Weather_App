package com.example.weatherapp.database

import android.content.Context
import com.example.weatherapp.model.WeatherResponse
import kotlinx.coroutines.flow.Flow

class ConcreteLocalSource (context: Context) : LocalSource {

    private val weatherDao : WeatherDao by lazy {
        val db : WeatherDataBase = WeatherDataBase.getInstance(context)
        db.getWeatherDao()
    }

    override fun getCurrentWeather(): Flow<WeatherResponse> {
        return weatherDao.getCurrentWeather()
    }

    override suspend fun insertCurrentWeather(weatherResponse: WeatherResponse) {
        return weatherDao.insertCurrentWeather(weatherResponse)
    }

    override suspend fun deleteCurrentWeather() {
        return weatherDao.deleteCurrentWeather()
    }

}