package com.example.weatherapp.model

import android.content.Context
import com.example.weatherapp.Constants
import com.example.weatherapp.database.LocalSource
import com.example.weatherapp.network.RemoteSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class Repository private constructor(
    var remoteSource: RemoteSource, var localSource: LocalSource , var context: Context) : RepositoryInterface {

    companion object{
        private var instance : Repository? = null
        fun getInstance(remoteSource: RemoteSource, localSource: LocalSource , context: Context) : Repository {
            return instance?: synchronized(this){
                val temp = Repository(remoteSource , localSource , context)
                instance = temp
                temp
            }
        }
    }

    override suspend fun getWeatherResponseFromApi(latitude: Double, longitude: Double): Flow<WeatherResponse> {
        val sharedPreference = context.getSharedPreferences(Constants.SP_Key, Context.MODE_PRIVATE)
        var lang = sharedPreference.getString(Constants.language, Constants.Language_Enum.en.toString())!!
        var units = sharedPreference.getString(Constants.unit,Constants.Units_Enum.metric.toString())!!
        return flowOf(remoteSource.getWeatherFromApi(latitude, longitude, lang, units))
    }

    override suspend fun getCurrentWeatherFromDB(): Flow<WeatherResponse> {
        return localSource.getCurrentWeather()
    }

    override suspend fun insertCurrentWeatherToDB(weatherResponse: WeatherResponse) {
        return localSource.insertCurrentWeather(weatherResponse)
    }

    override suspend fun deleteCurrentWeatherToDB() {
        return localSource.deleteCurrentWeather()
    }

    override suspend fun getFavLocationsFromDB(): Flow<List<FavoriteLocation>> {
        return localSource.getFavLocations()
    }

    override suspend fun insertFavLocationToDB(favoriteLocation: FavoriteLocation) {
        return localSource.insertToFav(favoriteLocation)
    }

    override suspend fun deleteFavLocationFromDB(favoriteLocation: FavoriteLocation) {
        return localSource.deleteFavLocation(favoriteLocation)
    }


}