package com.example.weatherapp.ui.setting

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation.findNavController
import com.example.weatherapp.Constants
import com.example.weatherapp.R
import com.example.weatherapp.databinding.FragmentSettingBinding

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

        checkRadioButton()

        binding.backSetting.setOnClickListener {
            findNavController(view).navigate(R.id.action_settingFragment_to_homeFragment)
        }

        binding.languageRadioGroup.setOnCheckedChangeListener { group, checkedId ->
            langRadioButton = view.findViewById<View>(checkedId) as RadioButton
            when (langRadioButton.text) {
                getString(R.string.english) -> {
                    sharedPreference.edit()
                        .putString(Constants.language, Constants.Language_Enum.en.toString())
                        .commit()
                }
                getString(R.string.arabic) -> {
                    sharedPreference.edit()
                        .putString(Constants.language, Constants.Language_Enum.ar.toString())
                        .commit()
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
    }

    fun checkRadioButton() {
        val sharedPreference =
            requireActivity().getSharedPreferences(Constants.SP_Key, Context.MODE_PRIVATE)
        var lang = sharedPreference.getString(Constants.language, Constants.Language_Enum.en.toString())
        var units = sharedPreference.getString(Constants.unit,Constants.Units_Enum.metric.toString())
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


}