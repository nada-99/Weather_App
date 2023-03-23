package com.example.weatherapp.model

import com.example.weatherapp.database.LocalSource
import com.example.weatherapp.network.RemoteSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class Repository private constructor(
    var remoteSource: RemoteSource, var localSource: LocalSource) : RepositoryInterface {

    companion object{
        private var instance : Repository? = null
        fun getInstance(remoteSource: RemoteSource, localSource: LocalSource) : Repository {
            return instance?: synchronized(this){
                val temp = Repository(remoteSource , localSource)
                instance = temp
                temp
            }
        }
    }

    override suspend fun getWeatherResponseFromApi(
        latitude: Double,
        longitude: Double,
        exclude: String,
        lang: String,
        units: String,
        apiKey: String
    ): Flow<WeatherResponse> {
        return flowOf(remoteSource.getWeatherFromApi(latitude, longitude, exclude, lang, units, apiKey))
    }


}