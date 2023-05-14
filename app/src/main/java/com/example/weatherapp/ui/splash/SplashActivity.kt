package com.example.weatherapp.ui.splash

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.weatherapp.Constants
import com.example.weatherapp.R
import com.example.weatherapp.ui.MainActivity
import com.example.weatherapp.ui.initalsetting.InitSettingActivity
import java.util.*

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        if (supportActionBar != null) {
            supportActionBar!!.hide()
        }

        //check if it is the first time to open application
        val sharedPreferences =
            applicationContext.getSharedPreferences(Constants.SP_Key, Context.MODE_PRIVATE)
        val isFirstTime: Boolean = sharedPreferences.getBoolean(Constants.firstTime, true)

        Handler(Looper.getMainLooper()).postDelayed({
            if (isFirstTime) {
                val editor = sharedPreferences.edit()
                editor.putBoolean(Constants.firstTime, false)
                editor.putString(Constants.language, Constants.Language_Enum.en.toString())
                editor.putString(Constants.unit, Constants.Units_Enum.metric.toString())
                editor.putString(Constants.windSpeed, Constants.WindSpeed_Enum.meter.toString())
                editor.putString(Constants.addressCountry, "")
                editor.commit()
                val intent = Intent(this, InitSettingActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
            } else {
                var lang = sharedPreferences.getString(Constants.language, "")
                changeLanguage(lang.toString())
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
            }
        }, 2500)

    }

    fun changeLanguage(language: String) {
        val metric = resources.displayMetrics
        val configuration = resources.configuration
        configuration.locale = Locale(language)
        Locale.setDefault(Locale(language))
        configuration.setLayoutDirection(Locale(language))
        resources.updateConfiguration(configuration, metric)
        onConfigurationChanged(configuration)
    }

}