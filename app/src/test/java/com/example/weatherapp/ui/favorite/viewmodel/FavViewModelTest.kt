package com.example.weatherapp.ui.favorite.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.weatherapp.MainRule
import com.example.weatherapp.model.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule

import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FavViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @get:Rule
    val mainRule = MainRule()

    lateinit var viewModel: FavViewModel
    lateinit var repository: FakeRepository

    private var myFavocation: MutableList<FavoriteLocation> = mutableListOf(
        FavoriteLocation(1, 3.34543, 3.567517, "", ""),
        FavoriteLocation(2, 3.34541, 3.56757, "", ""),
        FavoriteLocation(3, 3.34545, 3.567567, "", "")
    )

    private var myAlertList: MutableList<MyAlert> = mutableListOf(
        MyAlert (1, 12321230000, "", 1266730000, 1239980000, ""),
        MyAlert(2, 1234530000, "", 1269230000, 1239770000, ""),
        MyAlert(3, 1236730000, "", 1269780000, 1236690000, "")
    )

    private var weatherResponse =
        WeatherResponse(0.0, 0.0, "", 0, Current(), listOf(), listOf(), listOf(), isFav = 0)

    @Before
    fun setUp() {
        repository = FakeRepository(myAlertList, myFavocation, weatherResponse)
        viewModel = FavViewModel(repository)
    }


    @Test
    fun getFavLocations_returnListOfFav() {
        //when
        viewModel.getFavLocations()
        //then
        val result = viewModel.favData
        assertThat(result, CoreMatchers.notNullValue())

    }

    @Test
    fun deleteFavFromDB_returnListMinsOne() = mainRule.runBlockingTest {
        //When
        viewModel.deleteFavFromDB(myFavocation[1])

        var data:List<FavoriteLocation> = emptyList()
        viewModel.getFavLocations()
        val result = viewModel.favData.first()
        when (result) {
            is FavState.Loading -> {

            }
            is FavState.Success -> {

                data = result.data
            }
            is FavState.Failure -> {
            }
            else -> {}
        }
        //Then
        MatcherAssert.assertThat(data.size, CoreMatchers.`is`(2))
    }
}