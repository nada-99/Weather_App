package com.example.weatherapp.ui.alerts.viewmodel

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
class AlertViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @get:Rule
    val mainRule = MainRule()

    lateinit var viewModel: AlertViewModel
    lateinit var repository: FakeRepository

    private var myFavocation: MutableList<FavoriteLocation> = mutableListOf()

    private var myAlertList: MutableList<MyAlert> = mutableListOf(
        MyAlert(1,12321230000,"",1266730000,1239980000,"")
    )

    private var weatherResponse =
        WeatherResponse(0.0, 0.0, "", 0, Current(), listOf(), listOf(), listOf(), isFav = 0)

    @Before
    fun setUp() {
        repository = FakeRepository(myAlertList, myFavocation, weatherResponse)
        viewModel = AlertViewModel(repository)
    }

    @Test
    fun getAlerts_returnListOfAlerts() {
        //when
        viewModel.getAllAlerts()
        //then
        val result = viewModel.alertData
        assertThat(result, CoreMatchers.notNullValue())

    }

    @Test
    fun insertAlert_returnRightList() = mainRule.runBlockingTest {
        //When
        viewModel.insertAlertToDB(MyAlert())
        var data: List<MyAlert> = emptyList()
        viewModel.getAllAlerts()
        val result = viewModel.alertData.first()
        when (result) {
            is ResponseState.Loading -> {

            }
            is ResponseState.Success -> {

                data = result.data
            }
            is ResponseState.Failure -> {
            }
            else -> {}
        }
        //Then
        assertThat(data.size, CoreMatchers.`is`(0))
    }

    @Test
    fun deleteAlertFromDB_returnListMinsOne() = mainRule.runBlockingTest {
        //When
        viewModel.deleteAlertFromDB(myAlertList[0])

        var data: List<MyAlert> = emptyList()
        viewModel.getAllAlerts()
        val result = viewModel.alertData.first()
        when (result) {
            is ResponseState.Loading -> {

            }
            is ResponseState.Success -> {

                data = result.data
            }
            is ResponseState.Failure -> {
            }
            else -> {}
        }
        //Then
        MatcherAssert.assertThat(data.size, CoreMatchers.`is`(0))
    }

}