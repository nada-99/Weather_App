package com.example.weatherapp.model

import com.example.weatherapp.MainRule
import com.example.weatherapp.database.ConcreteLocalSourceTest
import com.example.weatherapp.network.RemoteSourceTest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.hamcrest.core.IsEqual
import org.hamcrest.core.IsNull
import org.junit.Assert
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule

import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class RepositoryTest {

    @ExperimentalCoroutinesApi
    @get:Rule
    val mainRule = MainRule()

    lateinit var fakeRemoteDataSource: RemoteSourceTest
    lateinit var fakeLocalDataSource: ConcreteLocalSourceTest
    lateinit var repo: FakeRepository

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
        fakeRemoteDataSource = RemoteSourceTest(weatherResponse)
        fakeLocalDataSource = ConcreteLocalSourceTest(myAlertList)
        repo = FakeRepository(myAlertList, myFavocation, weatherResponse)
    }

    @Test
    fun getWeatherResponse_returnWeatherFromApi() = mainRule.runBlockingTest{
        // Given
        val latitude  = 37.7749
        val longitude = -122.4194
        val weatherResponse = WeatherResponse()
        // When
        repo.getWeatherResponseFromApi(latitude,longitude).collect{
            val tasks =it
            // Then
            assertThat(tasks, IsEqual(weatherResponse))
        }

    }

    @Test
    fun insertCurrentWeatherToDB() = mainRule.runBlockingTest {
//        // Given a weather object
//        val weatherResponse = WeatherResponse(0.0, 0.0, "", 0, Current(), listOf(), listOf(), listOf(), isFav = 0)

        // When inserted into database
        repo.insertCurrentWeatherToDB(weatherResponse)

        // Then
        val result = repo.getCurrentWeatherFromDB()
        Assert.assertThat(result, CoreMatchers.notNullValue())
    }

    @Test
    fun deleteCurrentWeatherToDB_returnNullWeather() = mainRule.runBlockingTest{
        // When deleted from the database
        repo.deleteCurrentWeatherToDB()

        // Then
        val result = repo.getCurrentWeatherFromDB().firstOrNull()
        MatcherAssert.assertThat(result, IsNull())
    }

    @Test
    fun getFavLocationsFromDB_returnListOfFav() = mainRule.runBlockingTest {
//        //Given
//        val fav1 = FavoriteLocation(1,3.3454,3.567567,"","")
//        val fav2 = FavoriteLocation(2,3.3454,3.567567,"","")
//        val fav3 = FavoriteLocation(3,3.3454,3.567567,"","")
//        repo.insertFavLocationToDB(fav1)
//        repo.insertFavLocationToDB(fav2)
//        repo.insertFavLocationToDB(fav3)

        //When
        val result = repo.getFavLocationsFromDB().first()
        //then
        Assert.assertThat(result.size, CoreMatchers.`is`(3))
    }

    @Test
    fun insertFavLocationToDB_increaseSizeOfListByone() = mainRule.runBlockingTest {
        //Given
        val fav1 = FavoriteLocation(1,3.3454,3.567567,"","")

        //When
        repo.insertFavLocationToDB(fav1)

        //Then
        val result= repo.getFavLocationsFromDB().first()
        MatcherAssert.assertThat(result.size, CoreMatchers.`is`(4))
    }

    @Test
    fun deleteFavLocationFromDB_returnListMinsOne() = mainRule.runBlockingTest {

        //When
        repo.deleteFavLocationFromDB(FavoriteLocation(1,3.3454,3.567567,"",""))

        //Then
        val result= repo.getFavLocationsFromDB().first()
        MatcherAssert.assertThat(result.size, CoreMatchers.`is`(3))
    }

    @Test
    fun getAllAlertsFromDB_returnListOfAlerts() = mainRule.runBlockingTest {
        //When
        val result = repo.getAllAlertsFromDB().first()
        //then
        Assert.assertThat(result.size, CoreMatchers.`is`(3))
    }

    @Test
    fun insertAlertToDB_increaseSizeOfListByone() = mainRule.runBlockingTest {
        //Given
        val alert1 = MyAlert(1,12320000,"",12690000,12390000,"")

        //When
        repo.insertAlertToDB(alert1)

        //Then
        val result= repo.getFavLocationsFromDB().first()
        MatcherAssert.assertThat(result.size, CoreMatchers.`is`(3))
    }

}