package com.example.weatherapp.ui.alerts.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class AlertViewModel (private val repo: RepositoryInterface) : ViewModel(){
    private var alertMutableData = MutableStateFlow<ResponseState<List<MyAlert>>>(ResponseState.Loading)
    var alertData = alertMutableData
    private var weatherMutableData = MutableStateFlow<ResponseState<WeatherResponse>>(ResponseState.Loading)
    var weatherData = weatherMutableData

    init {
        getAllAlerts()
    }

    fun getAllAlerts(){
        viewModelScope.launch(Dispatchers.IO) {
            repo.getAllAlertsFromDB()
                .catch {
                        e -> alertMutableData.value = ResponseState.Failure(e)
                }?.collectLatest {
                        data -> alertMutableData.value = ResponseState.Success(data)
                }
        }
    }

    fun insertAlertToDB(myAlert: MyAlert){
        viewModelScope.launch(Dispatchers.IO) {
            repo.insertAlertToDB(myAlert)
        }
    }

    fun deleteAlertFromDB(myAlert: MyAlert){
        viewModelScope.launch(Dispatchers.IO) {
            repo.deleteAlertFromDB(myAlert)
        }
    }

    //get current weather from database
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
}