package com.example.weatherapp.ui.alerts.view

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.util.Log
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.lifecycleScope
import androidx.work.CoroutineWorker
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.example.weatherapp.Constants
import com.example.weatherapp.R
import com.example.weatherapp.database.ConcreteLocalSource
import com.example.weatherapp.model.MyAlert
import com.example.weatherapp.model.Repository
import com.example.weatherapp.model.ResponseState
import com.example.weatherapp.model.WeatherResponse
import com.example.weatherapp.network.WeatherClient
import com.example.weatherapp.ui.alerts.viewmodel.AlertViewModel
import com.example.weatherapp.ui.alerts.viewmodel.AlertViewModelFactory
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date

const val CHANNEL_ID = "channelID"

class NotificationWorker(private var context: Context, workerParams: WorkerParameters) :
    CoroutineWorker(context, workerParams) {

    lateinit var viewModel: AlertViewModel
    lateinit var myFactory: AlertViewModelFactory

    override suspend fun doWork(): Result {
//        var weatherResponse: WeatherResponse = WeatherResponse()
        val sharedPreference =
            context.getSharedPreferences(Constants.SP_Key, Context.MODE_PRIVATE)
        val lat = sharedPreference.getString(Constants.lat, "")?.toDoubleOrNull()
        val long = sharedPreference.getString(Constants.long, "")?.toDoubleOrNull()
        val notifSharedPreferences = sharedPreference.getString(Constants.notification, "")
        val language = sharedPreference.getString(Constants.language, "")

        myFactory = AlertViewModelFactory(
            Repository.getInstance(
                WeatherClient.getInstance(),
                ConcreteLocalSource(context),
                context
            )
        )
        viewModel = ViewModelProvider(ViewModelStore(), myFactory).get(AlertViewModel::class.java)
//        viewModel.getCurrentWeather()
//        GlobalScope.launch {
//            viewModel.weatherData.collectLatest { result ->
//                when (result) {
//                    is ResponseState.Loading -> {
//                        Log.i("LOADING", "LOADING: ")
//                    }
//                    is ResponseState.Success -> {
//                        weatherResponse = result.data
//                        Log.i("DATAAAA234", "${result.data.address}")
//                    }
//                    else -> {
//                        Log.i("ERRRRORR", "ERRRRORR: ")
//                    }
//                }
//            }
//        }

//        val client: WeatherClient = WeatherClient.getInstance()
//        var weatherResponse: WeatherResponse
//        runBlocking {
//            weatherResponse = client.getWeatherFromApi(lat!!, long!!, language, Constants.API_KEY)
//        }
//        var localSource = ConcreteLocalSource(context)
//        var weatherResponse: WeatherResponse
//        runBlocking {
//            weatherResponse = localSource.getCurrentWeather().first()
//        }

        var weatherResponse = ConcreteLocalSource(context).getCurrentWeather().first()
        Log.i("DATAAAA22", "${weatherResponse.timezone}")

        val alertString = inputData.getString("alert")
        var alert = Gson().fromJson(alertString, MyAlert::class.java)

        Log.i("DATAAAA22", "${weatherResponse.timezone}")
        val calendar = Calendar.getInstance()
        val currentTime2 = calendar.time.time
        val currentDateInMillis =  Date().time
        if((currentDateInMillis >= alert.startTime) &&(currentDateInMillis <= alert.endTime+300000)&&(currentTime2 > alert.dateOfNotification)){
            var desc = ""
            if (weatherResponse.alerts.isEmpty()) {
                desc = context.getString(R.string.noAlerts)
            } else {
                desc = weatherResponse.alerts.get(0).description
            }
            if (notifSharedPreferences == Constants.notification_Enum.enable.toString()) {
                showNotification(weatherResponse.timezone, "$desc")
                GlobalScope.launch(Dispatchers.Main) {
                    AlertAlarm(context, desc).onCreate()
                }
            }else{
                showNotification(weatherResponse.timezone, "$desc")
            }
            Log.i("DATAAAADDD", "${desc}")
            return Result.success()

        }else{
            println("Finnnnish")
           // Log.i("Finnnnish", "Finnnish")
            var repo = Repository(WeatherClient.getInstance(),ConcreteLocalSource(context),context)
            repo.deleteAlertFromDB(alert)
            //WorkManager.getInstance(context).cancelAllWorkByTag(alert.startTime.toString()+alert.endTime.toString())
            return Result.failure()
        }
    }


    private fun showNotification(title: String, desc: String) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "channel_name"
            val descriptionText = "channel_description"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager = applicationContext
                .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        val alarmSound: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val builder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.alarm_notif)
            .setContentTitle(title)
            .setContentText(desc)
            .setSound(alarmSound)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        with(NotificationManagerCompat.from(applicationContext)) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            notify(1, builder.build())
        }
    }
}