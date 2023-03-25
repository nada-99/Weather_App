package com.example.weatherapp.ui.home.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.Constants
import com.example.weatherapp.model.RepositoryInterface
import com.example.weatherapp.network.APIState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class HomeViewModel(private val repo: RepositoryInterface) : ViewModel() {

    private var weatherMutableData = MutableStateFlow<APIState>(APIState.Loading)
    val weatherData = weatherMutableData

    init {
        getWeatherData()
    }

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
}