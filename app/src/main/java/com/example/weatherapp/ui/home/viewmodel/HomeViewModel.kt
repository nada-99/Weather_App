package com.example.weatherapp.ui.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.model.RepositoryInterface
import com.example.weatherapp.model.APIState
import com.example.weatherapp.model.WeatherResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class HomeViewModel(private val repo: RepositoryInterface) : ViewModel() {

    private var weatherMutableData = MutableStateFlow<APIState>(APIState.Loading)
    var weatherData = weatherMutableData

//    init {
//        getCurrentWeather()
//    }

    //get weather data from api
    fun getWeatherData(lat: Double, long: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.getWeatherResponseFromApi(lat, long)
                .catch {
                        e -> weatherMutableData.value = APIState.Failure(e)
                }.collect{
                        data -> weatherMutableData.value = APIState.Success(data)
                }
            //weatherData = weatherMutableData
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
            weatherData = weatherMutableData
        }
    }

    fun insertCurrentWeatherToDB(weatherResponse: WeatherResponse){
        viewModelScope.launch(Dispatchers.IO) {
            repo.insertCurrentWeatherToDB(weatherResponse)
            //getCurrentWeather()
        }
    }

    fun deleteCurrentWeatherFromDB(){
        viewModelScope.launch(Dispatchers.IO) {
            repo.deleteCurrentWeatherToDB()
            //getCurrentWeather()
        }
    }
}