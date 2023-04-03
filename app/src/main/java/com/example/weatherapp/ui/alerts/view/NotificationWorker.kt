package com.example.weatherapp.ui.alerts.view

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.work.CoroutineWorker
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.example.weatherapp.Constants
import com.example.weatherapp.R
import com.example.weatherapp.database.ConcreteLocalSource
import com.example.weatherapp.model.APIState
import com.example.weatherapp.model.MyAlert
import com.example.weatherapp.model.Repository
import com.example.weatherapp.model.WeatherResponse
import com.example.weatherapp.network.WeatherClient
import com.example.weatherapp.ui.MainActivity
import com.example.weatherapp.ui.home.viewmodel.HomeViewModel
import com.example.weatherapp.ui.home.viewmodel.HomeViewModelFactory
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

const val CHANNEL_ID = "channelID"

class NotificationWorker (private var context: Context, workerParams: WorkerParameters) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val sharedPreference =
            context.getSharedPreferences(Constants.SP_Key, Context.MODE_PRIVATE)
        val lat = sharedPreference.getString(Constants.lat, "")?.toDoubleOrNull()
        val long = sharedPreference.getString(Constants.long, "")?.toDoubleOrNull()
        val notifSharedPreferences = sharedPreference.getString(Constants.notification, "")
        val language = sharedPreference.getString(Constants.language, "")

        val client: WeatherClient = WeatherClient.getInstance()
        var weatherResponse: WeatherResponse
        runBlocking {
            weatherResponse = client.getWeatherFromApi(lat!!, long!!,language,Constants.API_KEY)
        }

        Log.i("DATAAAA", "${weatherResponse.timezone}")

        val alertString = inputData.getString("alert")
        var alert=  Gson().fromJson(alertString, MyAlert::class.java)
        val currentTime = System.currentTimeMillis().div(1000)

        Log.i("DATAAAA", "${weatherResponse.timezone}")
//        if(currentTime >= alert.startTime && currentTime <= alert.endTime) {

        var desc = ""
        if(weatherResponse.alerts.isEmpty()){
            desc = context.getString(R.string.noAlerts)
        }else{
            desc = weatherResponse.alerts.get(0).description
        }
        if (notifSharedPreferences == Constants.notification_Enum.disable.toString() ) {
            showNotification(weatherResponse.timezone, "$desc")
            GlobalScope.launch(Dispatchers.Main) {
                AlertAlarm(context, desc).onCreate()
            }
        }
        Log.i("DATAAAADDD", "${desc}")
//            else {
////                GlobalScope.launch(Dispatchers.Main) {
////                    AlertWindow(context, desc, alert.AlertCityName).onCreate()
////                }
//            }
            return Result.success()
//        }
//        else if(currentTime > alert.endTime){
//                var weatherDataBase = WeatherDataBase.getInstance(context)
//                var room = LocalDataSource.getInstance(weatherDataBase,context)
//                GlobalScope.launch(Dispatchers.Main) {
//                    room.deleteAlertDataBase(alert)
//                }
//                WorkManager.getInstance(context).cancelAllWorkByTag(alert.startDay.toString()+alert.startDay.toString())
//                return Result.success()
//            }
//        else{
//                return Result.failure()
//            }
    }


    private fun showNotification(title:String,desc:String) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "channel_name"
            val descriptionText = "channel_description"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =applicationContext
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