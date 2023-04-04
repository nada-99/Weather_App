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

sealed class ResponseState<out T> {
    class Success<T>(val data:T): ResponseState<T>()
    class Failure(val msg:Throwable): ResponseState<Nothing>()
    object Loading: ResponseState<Nothing>()
}
