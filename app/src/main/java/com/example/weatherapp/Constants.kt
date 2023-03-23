package com.example.weatherapp

import java.text.SimpleDateFormat
import java.util.*

fun getFormattedDate(timestamp: Long): String {
    val date = Date(timestamp * 1000)
    val dateFormat = SimpleDateFormat("EEE , M/d/yyyy", Locale.getDefault())
    return dateFormat.format(date)
}

fun getTimeHourlyFormat(timestamp: Long): String{
    val sdf = SimpleDateFormat("h:mm a", Locale.getDefault())
    val date = Date(timestamp * 1000L)
    return sdf.format(date)
}