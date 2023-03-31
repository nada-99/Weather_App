package com.example.weatherapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.weatherapp.model.FavoriteLocation
import com.example.weatherapp.model.WeatherResponse
import com.example.weatherapp.model.WeatherTypeConverter

@Database(entities = arrayOf(WeatherResponse::class,FavoriteLocation::class), exportSchema = false, version = 5 )
@TypeConverters(WeatherTypeConverter::class)
abstract class WeatherDataBase : RoomDatabase() {
    abstract fun getWeatherDao(): WeatherDao
    companion object {
        @Volatile
        private var INSTANCE: WeatherDataBase? = null
        fun getInstance(ctx: Context): WeatherDataBase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    ctx.applicationContext, WeatherDataBase::class.java, "weather_database")
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}
