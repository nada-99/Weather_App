package com.example.weatherapp.network

import com.example.weatherapp.model.WeatherResponse

sealed class APIState {
    class Success(val data:WeatherResponse):APIState()
    class Failure(val msg:Throwable):APIState()
    object Loading:APIState()
}