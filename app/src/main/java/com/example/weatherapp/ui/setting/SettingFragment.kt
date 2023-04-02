package com.example.weatherapp.ui.setting

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.Navigation.findNavController
import com.example.weatherapp.Constants
import com.example.weatherapp.R
import com.example.weatherapp.databinding.FragmentSettingBinding
import com.example.weatherapp.ui.MainActivity
import java.util.*

class SettingFragment : Fragment() {

    lateinit var binding: FragmentSettingBinding
    lateinit var langRadioButton: RadioButton
    lateinit var tempRadioButton: RadioButton
    lateinit var windSpeedRadioButton: RadioButton
    lateinit var notificationRadioButton: RadioButton
    lateinit var locationRadioButton: RadioButton
    lateinit var sharedPreference: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPreference =
            requireActivity().getSharedPreferences(Constants.SP_Key, Context.MODE_PRIVATE)
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
    }

    override fun onStop() {
        super.onStop()
        (activity as AppCompatActivity?)!!.supportActionBar!!.show()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSettingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var context: Context
        var resources: Resources

        checkRadioButton()

        binding.backSetting.setOnClickListener {
            findNavController(view).navigate(R.id.action_settingFragment_to_homeFragment)
        }

        binding.locationRadioGroup.setOnCheckedChangeListener { group, checkedId ->
            locationRadioButton = view.findViewById<View>(checkedId) as RadioButton
            when (locationRadioButton.text) {
                getString(R.string.gps) -> {
                    sharedPreference.edit()
                        .putString(Constants.LocationFrom, Constants.Loction_Enum.gps.toString())
                        .commit()
                    val intent = Intent(requireContext(), MainActivity::class.java)
                    startActivity(intent)

                }
                getString(R.string.map) -> {
                    sharedPreference.edit()
                        .putString(Constants.LocationFrom, Constants.Loction_Enum.map.toString())
                        .commit()
                    Navigation.findNavController(view).navigate(R.id.action_settingFragment_to_mapOrGpsFragment3)
                }
            }
        }

        binding.notificationRadioGroup.setOnCheckedChangeListener { group, checkedId ->
            notificationRadioButton = view.findViewById<View>(checkedId) as RadioButton
            when (notificationRadioButton.text) {
                getString(R.string.enable) -> {
                    sharedPreference.edit()
                        .putString(Constants.notification, Constants.notification_Enum.enable.toString())
                        .commit()
                }
                getString(R.string.disable) -> {
                    sharedPreference.edit()
                        .putString(Constants.notification, Constants.notification_Enum.disable.toString())
                        .commit()
                }
            }
        }

        binding.languageRadioGroup.setOnCheckedChangeListener { group, checkedId ->
            langRadioButton = view.findViewById<View>(checkedId) as RadioButton
            when (langRadioButton.text) {
                getString(R.string.english) -> {
                    sharedPreference.edit()
                        .putString(Constants.language, Constants.Language_Enum.en.toString())
                        .commit()
                    changeLanguage("en")
                }
                getString(R.string.arabic) -> {
                    sharedPreference.edit()
                        .putString(Constants.language, Constants.Language_Enum.ar.toString())
                        .commit()
                    changeLanguage("ar")
                }
            }

        }

        binding.temperatureRadioGroup.setOnCheckedChangeListener { group, checkedId ->
            tempRadioButton = view.findViewById<View>(checkedId) as RadioButton
            when (tempRadioButton.text) {
                getString(R.string.celsius) -> {
                    sharedPreference.edit()
                        .putString(Constants.unit, Constants.Units_Enum.metric.toString())
                        .commit()
                }
                getString(R.string.fahrenheit) -> {
                    sharedPreference.edit()
                        .putString(Constants.unit, Constants.Units_Enum.imperial.toString())
                        .commit()
                }
                getString(R.string.kelvin) -> {
                    sharedPreference.edit()
                        .putString(Constants.unit, Constants.Units_Enum.standard.toString())
                        .commit()
                }
            }
        }

        binding.windSpeedRadioGroup.setOnCheckedChangeListener { group, checkedId ->
            windSpeedRadioButton = view.findViewById<View>(checkedId) as RadioButton
            when (windSpeedRadioButton.text) {
                getString(R.string.meter_sec) -> {
                    sharedPreference.edit()
                        .putString(Constants.windSpeed, Constants.WindSpeed_Enum.meter.toString())
                        .commit()
                }
                getString(R.string.mile_hour) -> {
                    sharedPreference.edit()
                        .putString(Constants.windSpeed, Constants.WindSpeed_Enum.mile.toString())
                        .commit()
                }
            }

        }
    }

    fun checkRadioButton() {
        val sharedPreference =
            requireActivity().getSharedPreferences(Constants.SP_Key, Context.MODE_PRIVATE)
        var lang = sharedPreference.getString(Constants.language, Constants.Language_Enum.en.toString())
        var units = sharedPreference.getString(Constants.unit,Constants.Units_Enum.metric.toString())
        var location = sharedPreference.getString(Constants.LocationFrom,Constants.Loction_Enum.gps.toString())
        var notification = sharedPreference.getString(Constants.notification,Constants.notification_Enum.enable.toString())

        if (location == Constants.Loction_Enum.gps.toString()) {
            binding.locationRadioGroup.check(binding.gpsRadioButton.id)
        }

        if (location == Constants.Loction_Enum.map.toString()) {
            binding.locationRadioGroup.check(binding.mapRadioButton.id)
        }

        if (notification == Constants.notification_Enum.enable.toString()) {
            binding.notificationRadioGroup.check(binding.enableRadioButton.id)
        }

        if (notification == Constants.notification_Enum.disable.toString()) {
            binding.notificationRadioGroup.check(binding.disableRadioButton.id)
        }

        if (lang == Constants.Language_Enum.en.toString()) {
            binding.languageRadioGroup.check(binding.engSubRadioButton.id)
        }
        if (lang == Constants.Language_Enum.ar.toString()) {
            binding.languageRadioGroup.check(binding.arSubRadioButton.id)
        }
        if (units == Constants.Units_Enum.metric.toString()) {
            binding.temperatureRadioGroup.check(binding.celsiusRadioButton.id)
        }
        if (units == Constants.Units_Enum.imperial.toString()) {
            binding.temperatureRadioGroup.check(binding.fahrenheitRadioButton.id)
        }
        if (units == Constants.Units_Enum.standard.toString()) {
            binding.temperatureRadioGroup.check(binding.kelvinRadioButton.id)
        }

    }

    fun changeLanguage(language: String) {
        val metric = resources.displayMetrics
        val configuration = resources.configuration
        configuration.locale = Locale(language)
        Locale.setDefault(Locale(language))
        configuration.setLayoutDirection(Locale(language))
        resources.updateConfiguration(configuration, metric)
        onConfigurationChanged(configuration)
        requireActivity().recreate()
    }


}