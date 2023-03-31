package com.example.weatherapp.ui.favorite.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class FavViewModel(private val repo: RepositoryInterface) : ViewModel() {

    private var favMutableData = MutableStateFlow<FavState>(FavState.Loading)
    var favData = favMutableData

    init {
        getFavLocations()
    }

    fun getFavLocations(){
        viewModelScope.launch(Dispatchers.IO) {
            repo.getFavLocationsFromDB()
                .catch {
                        e -> favMutableData.value = FavState.Failure(e)
                }?.collectLatest {
                        data -> favMutableData.value = FavState.Success(data)
                }
        }
    }

    fun deleteFavFromDB(favoriteLocation: FavoriteLocation){
        viewModelScope.launch(Dispatchers.IO) {
            repo.deleteFavLocationFromDB(favoriteLocation)
        }
    }
}