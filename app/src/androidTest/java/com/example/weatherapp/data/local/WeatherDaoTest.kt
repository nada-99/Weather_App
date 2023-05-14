package com.example.weatherapp.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.example.weatherapp.RoomMainRule
import com.example.weatherapp.database.WeatherDao
import com.example.weatherapp.database.WeatherDataBase
import com.example.weatherapp.model.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.hamcrest.core.IsNull
import org.junit.*
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@SmallTest
class WeatherDaoTest {

    lateinit var database : WeatherDataBase
    lateinit var weatherDao: WeatherDao

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
        weatherDao = database.getWeatherDao()
    }
    @After
    fun closeDatabase() = database.close()

    @Test
    fun insertWeather_returnRightWeatherData() = runBlocking {
        // Given a weather object
        val weatherResponse = WeatherResponse(0.0, 0.0, "", 0, Current(), listOf(), listOf(), listOf(), isFav = 0)

        // When inserted into database
        weatherDao.insertCurrentWeather(weatherResponse)

        // Then
        val result = weatherDao.getCurrentWeather()
        Assert.assertThat(result, CoreMatchers.notNullValue())
    }
    @Test
    fun deleteWeather_returnNullWeather() = runBlockingTest {
        // Given a weather object in the database
        val weatherResponse = WeatherResponse(0.0, 0.0, "", 0, Current(), listOf(), listOf(), listOf(), isFav = 0)
        weatherDao.insertCurrentWeather(weatherResponse)

        // When deleted from the database
        weatherDao.deleteCurrentWeather()

        // Then
        val result = weatherDao.getCurrentWeather().firstOrNull()
        MatcherAssert.assertThat(result,IsNull())
    }

    @Test
    fun getAllFavLocations_returnRightFavLocationListSize() = mainRule.runBlockingTest {
        //Given
        val fav1 = FavoriteLocation(1,3.3454,3.567567,"","")
        val fav2 = FavoriteLocation(2,3.3454,3.567567,"","")
        val fav3 = FavoriteLocation(3,3.3454,3.567567,"","")
        weatherDao.insertFavLocation(fav1)
        weatherDao.insertFavLocation(fav2)
        weatherDao.insertFavLocation(fav3)

        //When
        val result = weatherDao.getFavLocations().first()
        //then
        Assert.assertThat(result.size, CoreMatchers.`is`(3))
    }

    @Test
    fun insertFavLocation_insertOneItem_returnTheItem() = mainRule.runBlockingTest {
        //Given
        val fav1 = FavoriteLocation(1,3.3454,3.567567,"","")

        //When
        weatherDao.insertFavLocation(fav1)

        //Then
        val result= weatherDao.getFavLocations().first()
        MatcherAssert.assertThat(result[0], IsNull.notNullValue())
    }

    @Test
    fun getAllAlerts_returnRightAlertsListSize() = mainRule.runBlockingTest {
        //Given
        val alert1 = MyAlert(1,12321230000,"",1266730000,1239980000,"")
        val alert2 = MyAlert(2,1234530000,"",1269230000,1239770000,"")
        val alert3 = MyAlert(3,1236730000,"",1269780000,1236690000,"")
        weatherDao.insertAlert(alert1)
        weatherDao.insertAlert(alert2)
        weatherDao.insertAlert(alert3)

        //When
        val result = weatherDao.getAllAlerts().first()
        //then
        Assert.assertThat(result.size, CoreMatchers.`is`(3))
    }

    @Test
    fun insertAlert_insertOneItem_returnTheItem() = mainRule.runBlockingTest {
        //Given
        val alert1 = MyAlert(1,1232130000,"",1269730000,1239990000,"")

        //When
        weatherDao.insertAlert(alert1)

        //Then
        val result= weatherDao.getAllAlerts().first()
        MatcherAssert.assertThat(result[0], IsNull.notNullValue())
    }

    @Test
    fun deleteAlert_deleteOneItem_returnTheItemMinsOne() = mainRule.runBlockingTest {
        //Given
        val alert1 = MyAlert(1,1232130000,"",1269730000,1239990000,"")
        val alert2 = MyAlert(2,12321000,"",12697000,12399900,"")
        weatherDao.insertAlert(alert1)
        weatherDao.insertAlert(alert2)

        //When
        weatherDao.deleteAlert(alert1)

        //Then
        val result= weatherDao.getAllAlerts().first()
        MatcherAssert.assertThat(result.size, CoreMatchers.`is`(1))
    }

}