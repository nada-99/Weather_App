package com.example.weatherapp.database

import androidx.room.*
import com.example.weatherapp.model.FavoriteLocation
import com.example.weatherapp.model.MyAlert
import com.example.weatherapp.model.WeatherResponse
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherDao {
    //current weather
    @Query("SELECT * FROM weatherResponse WHERE isFav=0 LIMIT 1")
    fun getCurrentWeather(): Flow<WeatherResponse>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCurrentWeather(weatherResponse : WeatherResponse)
    @Query("DELETE FROM weatherResponse where isFav=0")
    suspend fun deleteCurrentWeather()

    //Favorite
    @Query("SELECT * FROM favoriteLocation")
    fun getFavLocations(): Flow<List<FavoriteLocation>>
    //suspend fun getFavLocations(): List<Product>
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertFavLocation(favoriteLocation: FavoriteLocation)
    @Delete
    suspend fun deleteFavLocation(favoriteLocation: FavoriteLocation)

    //Alerts
    @Query("SELECT * FROM myAlert")
    fun getAllAlerts(): Flow<List<MyAlert>>
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAlert(myAlert: MyAlert)
    @Delete
    suspend fun deleteAlert(myAlert: MyAlert)


}