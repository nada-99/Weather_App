package com.example.weatherapp.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
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

}