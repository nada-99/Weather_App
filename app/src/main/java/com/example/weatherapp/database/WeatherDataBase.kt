package com.example.weatherapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.weatherapp.model.Current

//@Database(entities = arrayOf(Current::class), version = 1 )
abstract class WeatherDataBase {
//    : RoomDatabase() {
//    abstract fun getWeatherDao(): WeatherDao
//    companion object {
//        @Volatile
//        private var INSTANCE: WeatherDataBase? = null
//        fun getInstance(ctx: Context): WeatherDataBase {
//            return INSTANCE ?: synchronized(this) {
//                val instance = Room.databaseBuilder(
//                    ctx.applicationContext, WeatherDataBase::class.java, "weather_database")
//                    .build()
//
//                INSTANCE = instance
//                instance
//            }
//        }
//    }
}
