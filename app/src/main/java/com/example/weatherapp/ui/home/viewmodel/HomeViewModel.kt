package com.example.weatherapp.ui.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.model.RepositoryInterface
import com.example.weatherapp.model.APIState
import com.example.weatherapp.model.ResponseState
import com.example.weatherapp.model.WeatherResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class HomeViewModel(private val repo: RepositoryInterface) : ViewModel() {

    private var weatherMutableData = MutableStateFlow<ResponseState<WeatherResponse>>(ResponseState.Loading)
    var weatherData = weatherMutableData

//    init {
//        getCurrentWeather()
//    }

    //get weather data from api
    fun getWeatherData(lat: Double, long: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.getWeatherResponseFromApi(lat, long)
                .catch {
                        e -> weatherMutableData.value = ResponseState.Failure(e)
                }.collectLatest{
                        data -> weatherMutableData.value = ResponseState.Success(data)
                }
        }
    }

    //DataBase
    fun getCurrentWeather(){
        viewModelScope.launch(Dispatchers.IO) {
            repo.getCurrentWeatherFromDB()
                .catch {
                        e -> weatherMutableData.value = ResponseState.Failure(e)
                }?.collectLatest {
                        data -> weatherMutableData.value = ResponseState.Success(data)
                }
        }
    }

    fun insertCurrentWeatherToDB(weatherResponse: WeatherResponse){
        viewModelScope.launch(Dispatchers.IO) {
            repo.insertCurrentWeatherToDB(weatherResponse)
        }
    }

    fun deleteCurrentWeatherFromDB(){
        viewModelScope.launch(Dispatchers.IO) {
            repo.deleteCurrentWeatherToDB()
        }
    }
}