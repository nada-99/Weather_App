package com.example.weatherapp.ui.home.view

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.*
import com.example.weatherapp.databinding.HourlyRowBinding
import com.example.weatherapp.model.Hourly

private lateinit var binding: HourlyRowBinding

class HourlyAdapter(private var hourlyList: List<Hourly>, val context: Context) :
    RecyclerView.Adapter<HourlyAdapter.ViewHolder>() {

    var sharedPreference =
        context.getSharedPreferences(Constants.SP_Key, Context.MODE_PRIVATE)
    var languageFromSP = sharedPreference.getString(Constants.language, "")!!
    var tempUnit = sharedPreference.getString(Constants.unit, "")!!

    fun setListHours(values: List<Hourly?>?) {
        this.hourlyList = values as List<Hourly>
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater: LayoutInflater =
            parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = HourlyRowBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentHour = hourlyList[position]
        holder.binding.iconWeatherIv.setImageResource(getIconOfWeather(currentHour.weather.firstOrNull()?.icon))
        holder.binding.dateHourTv.text = getTimeHourlyFormat(currentHour.dt, languageFromSP)
        if (languageFromSP == "ar") {
            holder.binding.tempHourTv.text =
                "${convertToArabicNumber(currentHour.temp.toInt())} ${getUnit(tempUnit, languageFromSP)}"
        } else {
            holder.binding.tempHourTv.text = "${currentHour.temp.toInt()} ${getUnit(tempUnit, languageFromSP)}"
        }
    }

    override fun getItemCount() = hourlyList.size

    inner class ViewHolder(var binding: HourlyRowBinding) : RecyclerView.ViewHolder(binding.root)
}