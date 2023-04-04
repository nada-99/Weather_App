package com.example.weatherapp.data.local

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.weatherapp.RoomMainRule
import com.example.weatherapp.database.ConcreteLocalSource
import com.example.weatherapp.database.LocalSource
import com.example.weatherapp.database.WeatherDao
import com.example.weatherapp.database.WeatherDataBase
import com.example.weatherapp.model.Current
import com.example.weatherapp.model.FavoriteLocation
import com.example.weatherapp.model.MyAlert
import com.example.weatherapp.model.WeatherResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.hamcrest.core.IsNull
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class LocalSourceTest {
    lateinit var database : WeatherDataBase
    lateinit var localSource: ConcreteLocalSource

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @get:Rule
    val mainRule = RoomMainRule()

    @Before
    fun createDataBase(){
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            WeatherDataBase::class.java
        ).allowMainThreadQueries().build()
        val context = ApplicationProvider.getApplicationContext<Context>()
        localSource = ConcreteLocalSource(context)
    }
    @After
    fun closeDatabase() = database.close()

    @Test
    fun insertWeather_returnRightWeatherData() = runBlockingTest {
        // Given a weather object
        val weatherResponse = WeatherResponse(0.0, 0.0, "", 0, Current(), listOf(), listOf(), listOf(), isFav = 0)

        // When inserted into database
        localSource.insertCurrentWeather(weatherResponse)

        // Then
        val result = localSource.getCurrentWeather()
        Assert.assertThat(result, CoreMatchers.notNullValue())
    }

    @Test
    fun deleteWeather_returnNullWeather() = runBlockingTest {
        // Given a weather object in the database
        val weatherResponse = WeatherResponse(0.0, 0.0, "", 0, Current(), listOf(), listOf(), listOf(), isFav = 0)
        localSource.insertCurrentWeather(weatherResponse)

        // When deleted from the database
        localSource.deleteCurrentWeather()

        // Then
        val result = localSource.getCurrentWeather().firstOrNull()
        MatcherAssert.assertThat(result, IsNull())
    }

    @Test
    fun getAllFavLocations_returnRightFavLocationListSize() = mainRule.runBlockingTest {
        //Given
        val fav1 = FavoriteLocation(1,3.3454,3.567567,"","")
        val fav2 = FavoriteLocation(2,3.3454,3.567567,"","")
        val fav3 = FavoriteLocation(3,3.3454,3.567567,"","")
        localSource.insertToFav(fav1)
        localSource.insertToFav(fav2)
        localSource.insertToFav(fav3)

        //When
        val result = localSource.getFavLocations().first()
        //then
        Assert.assertThat(result.size, CoreMatchers.`is`(3))
    }

    @Test
    fun insertFavLocation_insertOneItem_returnTheItem() = mainRule.runBlockingTest {
        //Given
        val fav1 = FavoriteLocation(1,3.3454,3.567567,"","")

        //When
        localSource.insertToFav(fav1)

        //Then
        val result= localSource.getFavLocations().first()
        MatcherAssert.assertThat(result[0], IsNull.notNullValue())
    }

    @Test
    fun getAllAlerts_returnRightAlertsListSize() = mainRule.runBlockingTest {
        //Given
        val alert1 = MyAlert(1,12321230000,"",1266730000,1239980000,"")
        val alert2 = MyAlert(2,1234530000,"",1269230000,1239770000,"")
        val alert3 = MyAlert(3,1236730000,"",1269780000,1236690000,"")
        localSource.insertAlert(alert1)
        localSource.insertAlert(alert2)
        localSource.insertAlert(alert3)

        //When
        val result = localSource.getAllAlerts().first()
        //then
        Assert.assertThat(result.size, CoreMatchers.`is`(4))
    }

    @Test
    fun insertAlert_insertOneItem_returnTheItem() = mainRule.runBlockingTest {
        //Given
        val alert1 = MyAlert(1,1232130000,"",1269730000,1239990000,"")

        //When
        localSource.insertAlert(alert1)

        //Then
        val result= localSource.getAllAlerts().first()
        MatcherAssert.assertThat(result[0], IsNull.notNullValue())
    }

    @Test
    fun deleteAlert_deleteOneItem_returnTheItemMinsOne() = mainRule.runBlockingTest {
        //Given
        val alert1 = MyAlert(3,1232130000,"",1269730000,1239990000,"")
        val alert2 = MyAlert(4,12321000,"",12697000,12399900,"")
        localSource.insertAlert(alert1)
        localSource.insertAlert(alert2)

        //When
        localSource.deleteAlert(alert1)

        //Then
        val result= localSource.getAllAlerts().first()
        MatcherAssert.assertThat(result.size, CoreMatchers.`is`(2))
    }


}