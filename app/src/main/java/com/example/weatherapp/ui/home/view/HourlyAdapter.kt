package com.example.weatherapp.ui.home.view

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.weatherapp.databinding.HourlyRowBinding
import com.example.weatherapp.getTimeHourlyFormat
import com.example.weatherapp.model.Hourly

private lateinit var binding: HourlyRowBinding

class HourlyAdapter(private var hourlyList: List<Hourly>, val context: Context) :
    RecyclerView.Adapter<HourlyAdapter.ViewHolder>() {

    fun setListHours(values: List<Hourly?>?) {
        this.hourlyList = values as List<Hourly>
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater: LayoutInflater = parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = HourlyRowBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentHour = hourlyList[position]
        Glide.with(context)
            .load("@drawable/few_clouds_moon")
            .into(holder.binding.iconWeatherIv)
        holder.binding.dateHourTv.text = getTimeHourlyFormat(currentHour.dt)
        holder.binding.tempHourTv.text = currentHour.temp.toInt().toString()+"°c"
    }

    override fun getItemCount() = hourlyList.size

    inner class ViewHolder(var binding: HourlyRowBinding) : RecyclerView.ViewHolder(binding.root)
}