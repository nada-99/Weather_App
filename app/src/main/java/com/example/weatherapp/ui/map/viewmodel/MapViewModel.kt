package com.example.weatherapp.ui.map.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.model.APIState
import com.example.weatherapp.model.FavoriteLocation
import com.example.weatherapp.model.RepositoryInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class MapViewModel(private val repo: RepositoryInterface) : ViewModel() {

    private var mapMutableData = MutableStateFlow<APIState>(APIState.Loading)
    var mapData = mapMutableData

    fun insertFavLocationToDB(favoriteLocation: FavoriteLocation){
        viewModelScope.launch(Dispatchers.IO) {
            repo.insertFavLocationToDB(favoriteLocation)
        }
    }
}