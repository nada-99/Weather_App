package com.example.weatherapp.ui.home.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieDrawable
import com.example.weatherapp.database.ConcreteLocalSource
import com.example.weatherapp.databinding.FragmentHomeBinding
import com.example.weatherapp.getFormattedDate
import com.example.weatherapp.getLottiOfWeather
import com.example.weatherapp.model.Repository
import com.example.weatherapp.model.WeatherResponse
import com.example.weatherapp.network.APIState
import com.example.weatherapp.network.WeatherClient
import com.example.weatherapp.ui.home.viewmodel.HomeViewModel
import com.example.weatherapp.ui.home.viewmodel.HomeViewModelFactory
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    lateinit var binding: FragmentHomeBinding
    lateinit var myFactory: HomeViewModelFactory
    lateinit var viewModel: HomeViewModel
    lateinit var hourlyAdapter: HourlyAdapter
    lateinit var dailyAdapter: DailyAdapter
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

        //daily Adapter
        dailyAdapter = DailyAdapter(ArrayList(), requireActivity())
        val layoutManagerDaily =
            LinearLayoutManager(requireActivity(), RecyclerView.VERTICAL, false)
        binding.dailyRecyclerView.layoutManager = layoutManagerDaily

        myFactory = HomeViewModelFactory(
            Repository.getInstance(
                WeatherClient.getInstance(),
                ConcreteLocalSource(requireContext()),
                requireContext()
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
//                        Log.i("DATAAAA", "$weatherData")
                    }
                    else -> {
                        Log.i("ERRRRORR", "ERRRRORR: ")
                    }
                }
            }
        }
    }

    fun fitWeatherDataToUi(weatherDetails: WeatherResponse) {
        //first part
        binding.cityTv.text = weatherDetails.timezone
        binding.dateTv.text = getFormattedDate(weatherDetails.current.dt)
        binding.tempTv.text = weatherDetails.current.temp.toInt().toString() + "°c"
        binding.weatherDesTv.text = weatherDetails.current.weather.firstOrNull()?.description ?: ""
        binding.weatherDesLotti.setAnimation(getLottiOfWeather(weatherDetails.current.weather.firstOrNull()?.icon))
        binding.weatherDesLotti.repeatCount = LottieDrawable.INFINITE

        //hourly recyclerview
        hourlyAdapter.setListHours(weatherDetails.hourly.subList(0, 25))
        hourlyAdapter.notifyDataSetChanged()
        binding.hourlyRecyclerView.adapter = hourlyAdapter

        //Daily recyclerview
        dailyAdapter.setListDaily(weatherDetails.daily)
        dailyAdapter.notifyDataSetChanged()
        binding.dailyRecyclerView.adapter = dailyAdapter

        //last part
        binding.pressureTv.text = weatherDetails.current.pressure.toString()
        binding.humidityTv.text = weatherDetails.current.humidity.toString()
        binding.windTv.text = weatherDetails.current.windSpeed.toString()
        binding.cloudTv.text = weatherDetails.current.clouds.toString()
        binding.ultravioletTv.text = weatherDetails.current.uvi.toString()
        binding.visibilityTv.text = weatherDetails.current.visibility.toString()
    }


}