package com.example.weatherapp.ui.home.view

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.*
import com.example.weatherapp.databinding.DailyRowBinding
import com.example.weatherapp.model.Daily

private lateinit var binding: DailyRowBinding

class DailyAdapter(private var dailyList: List<Daily>, val context: Context) :
    RecyclerView.Adapter<DailyAdapter.ViewHolder>() {
    fun setListDaily(values: List<Daily?>?) {
        this.dailyList = values as List<Daily>
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater: LayoutInflater = parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = DailyRowBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentDay = dailyList[position]
        holder.binding.iconWeatherIv.setImageResource(getIconOfWeather(currentDay.weather.firstOrNull()?.icon))
        holder.binding.dateDayTv.text = getDayFormat(currentDay.dt)
        holder.binding.tempDescTv.text = currentDay.weather.firstOrNull()?.description ?: ""
        holder.binding.maxMinTempTv.text = currentDay.temp.max.toInt().toString()+"/"+currentDay.temp.min.toInt().toString()+"°c"
    }
    override fun getItemCount() = dailyList.size
    inner class ViewHolder(var binding: DailyRowBinding) : RecyclerView.ViewHolder(binding.root)
}