package com.example.weatherapp.ui.alerts.view

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.example.weatherapp.*
import com.example.weatherapp.database.ConcreteLocalSource
import com.example.weatherapp.databinding.FragmentSeclectTimeDialogBinding
import com.example.weatherapp.model.MyAlert
import com.example.weatherapp.model.Repository
import com.example.weatherapp.network.WeatherClient
import com.example.weatherapp.ui.alerts.viewmodel.AlertViewModel
import com.example.weatherapp.ui.alerts.viewmodel.AlertViewModelFactory
import java.text.SimpleDateFormat
import java.util.*

class SelectTimeDialog : DialogFragment() {

    lateinit var binding: FragmentSeclectTimeDialogBinding
    lateinit var myAlert: MyAlert
    lateinit var sharedPreference : SharedPreferences
    lateinit var myFactory: AlertViewModelFactory
    lateinit var viewModel: AlertViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT,)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSeclectTimeDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        myAlert = MyAlert()
        sharedPreference =
            requireContext().getSharedPreferences(Constants.SP_Key, Context.MODE_PRIVATE)
        var languageFromSP = sharedPreference.getString(Constants.language, "")!!

        myFactory = AlertViewModelFactory(
            Repository.getInstance(
                WeatherClient.getInstance(),
                ConcreteLocalSource(requireContext()),
                requireContext()
            )
        )
        viewModel = ViewModelProvider(this, myFactory).get(AlertViewModel::class.java)

        val currentDate = SimpleDateFormat("dd/MM/yyyy", Locale(languageFromSP)).format(Date())
        val currentTime = SimpleDateFormat("h:mm a", Locale(languageFromSP)).format(Date())
        val dateFormat = SimpleDateFormat("h:mm a", Locale(languageFromSP))
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.HOUR, 1)
        val futureTime = calendar.time
        val futureTimeString = dateFormat.format(futureTime)

        binding.fromTimeTv.text = currentTime
        binding.fromDateTv.text = currentDate
        binding.toDateTv.text = currentDate
        binding.toTimeTv.text = futureTimeString

        binding.fromCardView.setOnClickListener {
            setAlarm{
                val startTime = it
                binding.fromTimeTv.text = getTimeToAlert(startTime,languageFromSP)
                binding.fromDateTv.text = getDateToAlert(startTime,languageFromSP)
                myAlert.startTime = startTime
            }
        }

        binding.toCardView.setOnClickListener {
            setAlarm{
                val endTime = it
                binding.toTimeTv.text = getTimeToAlert(endTime,languageFromSP)
                binding.toDateTv.text = getDateToAlert(endTime,languageFromSP)
                myAlert.endTime = endTime
            }
        }

        binding.saveButton.setOnClickListener {
            if(myAlert.endTime > myAlert.startTime){
                viewModel.insertAlertToDB(myAlert)
                dialog!!.dismiss()
            }else{
                Toast.makeText(
                    requireContext(),
                    getString(R.string.endTimeAlert),
                    Toast.LENGTH_LONG
                )
                    .show()
            }
        }

        binding.cancelButton.setOnClickListener{
            dialog!!.dismiss()
        }

    }

    private fun setAlarm(callback: (Long) -> Unit) {
        Calendar.getInstance().apply {
            this.set(Calendar.SECOND, 0)
            this.set(Calendar.MILLISECOND, 0)
            val datePickerDialog = DatePickerDialog(
                requireContext(),
                0,
                { _, year, month, day ->
                    this.set(Calendar.YEAR, year)
                    this.set(Calendar.MONTH, month)
                    this.set(Calendar.DAY_OF_MONTH, day)
                    TimePickerDialog(
                        requireContext(),
                        0,
                        { _, hour, minute ->
                            this.set(Calendar.HOUR_OF_DAY, hour)
                            this.set(Calendar.MINUTE, minute)
                            callback(this.timeInMillis)
                        },
                        this.get(Calendar.HOUR_OF_DAY),
                        this.get(Calendar.MINUTE),
                        false
                    ).show()
                },
                this.get(Calendar.YEAR),
                this.get(Calendar.MONTH),
                this.get(Calendar.DAY_OF_MONTH)
            )
            datePickerDialog.datePicker.minDate = Calendar.getInstance().timeInMillis
            datePickerDialog.show()
        }
    }

}