package com.example.weatherapp.model

sealed class APIState {
    class Success(val data:WeatherResponse): APIState()
    class Failure(val msg:Throwable): APIState()
    object Loading: APIState()
}

sealed class FavState {
    class Success(val data:List<FavoriteLocation>): FavState()
    class Failure(val msg:Throwable): FavState()
    object Loading: FavState()
}