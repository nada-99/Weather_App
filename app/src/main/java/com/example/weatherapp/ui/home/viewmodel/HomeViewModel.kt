package com.example.weatherapp.ui.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.model.RepositoryInterface
import com.example.weatherapp.model.APIState
import com.example.weatherapp.model.WeatherResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HomeViewModel(private val repo: RepositoryInterface) : ViewModel() {

    private var weatherMutableData = MutableStateFlow<APIState>(APIState.Loading)
    val weatherData = weatherMutableData.asStateFlow()

//    init {
//        getCurrentWeather()
//    }

    //get weather data from api
    fun getWeatherData() {
        viewModelScope.launch(Dispatchers.IO) {
            repo.getWeatherResponseFromApi(31.2497, 30.0626)
                .catch {
                        e -> weatherMutableData.value = APIState.Failure(e)
                }?.collect {
                        data -> weatherMutableData.value = APIState.Success(data)
                }
        }
    }

    //DataBase
    fun getCurrentWeather(){
        viewModelScope.launch(Dispatchers.IO) {
            repo.getCurrentWeatherFromDB()
                .catch {
                        e -> weatherMutableData.value = APIState.Failure(e)
                }?.collectLatest {
                        data -> weatherMutableData.value = APIState.Success(data)
                }
        }
    }

    fun insertCurrentWeatherToDB(weatherResponse: WeatherResponse){
        viewModelScope.launch(Dispatchers.IO) {
            repo.insertCurrentWeatherToDB(weatherResponse)
            //getCurrentWeather()
        }
    }
}