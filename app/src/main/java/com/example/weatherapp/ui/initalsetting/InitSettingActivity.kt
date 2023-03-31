package com.example.weatherapp.ui.initalsetting

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.example.weatherapp.R

class InitSettingActivity : AppCompatActivity() {

    lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_init_setting)

        navController = Navigation.findNavController(this, R.id.nav_init_fragment)
        NavigationUI.setupActionBarWithNavController(this, navController)
    }
}