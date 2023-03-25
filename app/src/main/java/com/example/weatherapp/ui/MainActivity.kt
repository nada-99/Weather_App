package com.example.weatherapp.ui

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.Navigation.findNavController
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.example.weatherapp.Constants
import com.example.weatherapp.R
import com.google.android.material.navigation.NavigationView


class MainActivity : AppCompatActivity() {

    lateinit var drawerLayout: DrawerLayout
    lateinit var navigationView: NavigationView
    lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //setup navigation drawer
        getSupportActionBar()?.setElevation(0F)
        getSupportActionBar()?.setDisplayShowTitleEnabled(false)

        navigationView = findViewById(R.id.navigation_view)
        drawerLayout = findViewById(R.id.drawerLayout)

        val actionBar = supportActionBar
        actionBar?.setHomeAsUpIndicator(R.drawable.menu)
        actionBar!!.setDisplayShowHomeEnabled(true)
        actionBar.setDisplayHomeAsUpEnabled(true)

        navController = findNavController(this, R.id.nav_host_fragment)
        setupWithNavController(navigationView, navController)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START)
            } else {
                drawerLayout.openDrawer(GravityCompat.START)
            }
        }
        return super.onOptionsItemSelected(item)
    }

}