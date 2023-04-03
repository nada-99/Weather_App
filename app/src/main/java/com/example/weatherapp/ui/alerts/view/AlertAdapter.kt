package com.example.weatherapp.ui.alerts.view

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.Constants
import com.example.weatherapp.*
import com.example.weatherapp.databinding.AlertsRowBinding
import com.example.weatherapp.model.MyAlert


private lateinit var binding: AlertsRowBinding

class AlertAdapter(private var alertsList: List<MyAlert>, var myListener: OnClickAlert, val context: Context) :
    RecyclerView.Adapter<AlertAdapter.ViewHolder>() {

    var sharedPreference =
        context.getSharedPreferences(Constants.SP_Key, Context.MODE_PRIVATE)
    var languageFromSP = sharedPreference.getString(Constants.language, "")!!

    fun setListAlerts(values: List<MyAlert?>?) {
        this.alertsList = values as List<MyAlert>
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater: LayoutInflater =
            parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = AlertsRowBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentAlert = alertsList[position]
        holder.binding.startDateTv.text = getTimeToAlert(currentAlert.dateOfNotification,languageFromSP)
        holder.binding.dateStartTv.text = getDateToAlert(currentAlert.startTime,languageFromSP)
        holder.binding.endDateTv.text = getTimeToAlert(currentAlert.dateOfNotification,languageFromSP)
        holder.binding.dateEndTv.text = getDateToAlert(currentAlert.endTime,languageFromSP)
        holder.binding.menuAlertsIv.setOnClickListener {
            myListener.onClickDelete(currentAlert)
        }
    }

    override fun getItemCount(): Int = alertsList.size

    inner class ViewHolder(var binding: AlertsRowBinding) : RecyclerView.ViewHolder(binding.root)
}