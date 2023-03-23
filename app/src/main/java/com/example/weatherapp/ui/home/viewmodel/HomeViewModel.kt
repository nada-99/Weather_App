package com.example.weatherapp.ui.home.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.model.RepositoryInterface
import com.example.weatherapp.model.WeatherResponse
import com.example.weatherapp.network.APIState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class HomeViewModel(private val repo: RepositoryInterface) : ViewModel() {

//    private var weatherMutableData: MutableLiveData<WeatherResponse> =
//        MutableLiveData<WeatherResponse>()
//    val weatherData: LiveData<WeatherResponse> = weatherMutableData

//    private var weatherMutableData: MutableStateFlow<WeatherResponse?> =
//        MutableStateFlow<WeatherResponse?>(null)
//    val weatherData: StateFlow<WeatherResponse?> = weatherMutableData

    private var weatherMutableData = MutableStateFlow<APIState>(APIState.Loading)
    val weatherData =weatherMutableData

    init {
        getWeatherData()
    }

    fun getWeatherData() {

        viewModelScope.launch(Dispatchers.IO) {

            repo.getWeatherResponseFromApi(
                39.31,
                -74.5,
                "minutely",
                "en",
                "metric",
                "40dac0af7018969cbb541943f944ba29"
            ).catch {
                    e->weatherMutableData.value=APIState.Failure(e) }?.collect{
                    data->weatherMutableData.value=APIState.Success(data)
            }
        }

//        viewModelScope.launch(Dispatchers.Default) {
//            repo.getWeatherResponseFromApi(
//                39.31,
//                -74.5,
//                "minutely",
//                "en",
//                "metric",
//                "40dac0af7018969cbb541943f944ba29"
//            ).collect {
//                weatherMutableData.postValue(it)
//            }
//        }
//        viewModelScope.launch {
//            val result = repo.getWeatherResponseFromApi(
//                39.31,
//                -74.5,
//                "minutely",
//                "en",
//                "metric",
//                "40dac0af7018969cbb541943f944ba29"
//            )
//            weatherMutableData.value = result
//        }
    }
}