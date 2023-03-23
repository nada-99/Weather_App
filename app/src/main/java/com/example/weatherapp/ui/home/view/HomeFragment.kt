package com.example.weatherapp.ui.home.view

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.R
import com.example.weatherapp.database.ConcreteLocalSource
import com.example.weatherapp.databinding.FragmentHomeBinding
import com.example.weatherapp.getFormattedDate
import com.example.weatherapp.model.Repository
import com.example.weatherapp.model.WeatherResponse
import com.example.weatherapp.network.APIState
import com.example.weatherapp.network.WeatherClient
import com.example.weatherapp.ui.home.viewmodel.HomeViewModel
import com.example.weatherapp.ui.home.viewmodel.HomeViewModelFactory
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HomeFragment : Fragment() {

    lateinit var binding: FragmentHomeBinding
    lateinit var myFactory: HomeViewModelFactory
    lateinit var viewModel: HomeViewModel
    lateinit var weatherData: WeatherResponse
    lateinit var hourlyAdapter: HourlyAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Hourly Adapter
        hourlyAdapter = HourlyAdapter(ArrayList(), requireActivity())
        val layoutManager = LinearLayoutManager(requireActivity(), RecyclerView.HORIZONTAL, false)
        binding.hourlyRecyclerView.layoutManager = layoutManager

        weatherData = WeatherResponse()

        myFactory = HomeViewModelFactory(
            Repository.getInstance(
                WeatherClient.getInstance(),
                ConcreteLocalSource(requireContext())
            )
        )
        viewModel = ViewModelProvider(this, myFactory).get(HomeViewModel::class.java)

        lifecycleScope.launch {
            viewModel.weatherData.collect { result ->
                when (result) {
                    is APIState.Loading -> {
                        Log.i("LOADING", "LOADING: ")
//                        binding.recyclerView.visibility=View.GONE
//                        binding.progressBar.visibility=View.VISIBLE

                    }
                    is APIState.Success -> {
                        fitWeatherDataToUi(result.data)
                        //weatherData = result.data
//                        binding.cityTv.text = weatherData.timezone
//                        binding.dateTv.text = getFormattedDate(weatherData.current.dt)
//                        binding.tempTv.text = weatherData.current.temp.toInt().toString()+"°c"
//                        binding.weatherDesTv.text = weatherData.current.weather.firstOrNull()?.description ?: ""
                        Log.i("DATAAAA", "$weatherData")
                    }
                    else -> {
                        Log.i("ERRRRORR", "ERRRRORR: ")
                    }
                }
            }
        }
        //fitWeatherDataToUi()
    }

    fun fitWeatherDataToUi(weatherDetails: WeatherResponse){
        //first part
        binding.cityTv.text = weatherDetails.timezone
        binding.dateTv.text = getFormattedDate(weatherDetails.current.dt)
        binding.tempTv.text = weatherDetails.current.temp.toInt().toString()+"°c"
        binding.weatherDesTv.text = weatherDetails.current.weather.firstOrNull()?.description ?: ""

        //hourly recyclerview
        hourlyAdapter.setListHours(weatherDetails.hourly)
        hourlyAdapter.notifyDataSetChanged()
        binding.hourlyRecyclerView.adapter = hourlyAdapter

        //last part
        binding.pressureTv.text = weatherDetails.current.pressure.toString()
        binding.humidityTv.text = weatherDetails.current.humidity.toString()
        binding.windTv.text = weatherDetails.current.windSpeed.toString()
        binding.cloudTv.text = weatherDetails.current.clouds.toString()
        binding.ultravioletTv.text = weatherDetails.current.uvi.toString()
        binding.visibilityTv.text = weatherDetails.current.visibility.toString()
    }


}