package com.example.weatherapp.model

sealed class APIState {
    class Success(val data:WeatherResponse): APIState()
    class Failure(val msg:Throwable): APIState()
    object Loading: APIState()
}