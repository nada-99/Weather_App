package com.example.weatherapp.ui.home.view

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieDrawable
import com.example.weatherapp.*
import com.example.weatherapp.database.ConcreteLocalSource
import com.example.weatherapp.databinding.FragmentHomeBinding
import com.example.weatherapp.model.*
import com.example.weatherapp.network.WeatherClient
import com.example.weatherapp.ui.home.viewmodel.HomeViewModel
import com.example.weatherapp.ui.home.viewmodel.HomeViewModelFactory
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.intellij.lang.annotations.Language
import java.math.RoundingMode
import java.sql.SQLTransactionRollbackException
import java.util.Locale
import kotlin.collections.ArrayList

const val PERMISSION_ID = 44
class HomeFragment : Fragment() {

    lateinit var binding: FragmentHomeBinding
    lateinit var myFactory: HomeViewModelFactory
    lateinit var viewModel: HomeViewModel
    lateinit var hourlyAdapter: HourlyAdapter
    lateinit var dailyAdapter: DailyAdapter
    lateinit var sharedPreference: SharedPreferences
    var lat : Double? = null
    var long : Double? = null
    lateinit var languageFromSP : String
    lateinit var tempUnit : String
    lateinit var windUnit :String
    lateinit var locationSharedPreference:String

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
        val rootView = requireActivity().findViewById<View>(android.R.id.content)

        sharedPreference =
            requireActivity().getSharedPreferences(Constants.SP_Key, Context.MODE_PRIVATE)
        languageFromSP = sharedPreference.getString(Constants.language, "")!!
        locationSharedPreference = sharedPreference.getString(Constants.LocationFrom,"")!!
        tempUnit = sharedPreference.getString(Constants.unit , "")!!
        windUnit = sharedPreference.getString(Constants.windSpeed , "")!!

        myFactory = HomeViewModelFactory(
            Repository.getInstance(
                WeatherClient.getInstance(),
                ConcreteLocalSource(requireContext()),
                requireContext()
            )
        )
        viewModel = ViewModelProvider(this, myFactory).get(HomeViewModel::class.java)

        val favNavigationArgs : HomeFragmentArgs by navArgs()

        if(favNavigationArgs.favComing){
            lat = favNavigationArgs.favLat?.toDoubleOrNull()
            long = favNavigationArgs.favLong?.toDoubleOrNull()
            Log.i("Arrrrrgs", "$lat + $long")
        }else{
            if(locationSharedPreference == Constants.Loction_Enum.gps.toString()){
                lat = sharedPreference.getString(Constants.lat, "")?.toDoubleOrNull()
                long = sharedPreference.getString(Constants.long, "")?.toDoubleOrNull()
                Log.i("GPPPPPPS", "$lat , $long")
            }else{
                lat = sharedPreference.getString(Constants.latMap, "")?.toDoubleOrNull()
                long = sharedPreference.getString(Constants.longMap, "")?.toDoubleOrNull()
                Log.i("GPPPPPPS", "$lat , $long")
            }
        }

        //Hourly Adapter
        hourlyAdapter = HourlyAdapter(ArrayList(), requireActivity())
        val layoutManager = LinearLayoutManager(requireActivity(), RecyclerView.HORIZONTAL, false)
        binding.hourlyRecyclerView.layoutManager = layoutManager

        //daily Adapter
        dailyAdapter = DailyAdapter(ArrayList(), requireActivity())
        val layoutManagerDaily =
            LinearLayoutManager(requireActivity(), RecyclerView.VERTICAL, false)
        binding.dailyRecyclerView.layoutManager = layoutManagerDaily


        if (isInternetConnected(requireContext())) {
            Log.i("inttttttttternett", "hiiiiiiii")
            //call Api
           if (lat != null && long != null) {
               viewModel.getWeatherData(lat!!,long!!)
               lifecycleScope.launch {
                   viewModel.weatherData.collectLatest { result ->
                       when (result) {
                           is ResponseState.Loading -> {
                               Log.i("LOADING", "LOADING: ")
                               binding.progressBar.visibility = View.VISIBLE
                           }
                           is ResponseState.Success -> {
                               binding.progressBar.visibility = View.GONE
                               fitWeatherDataToUi(result.data)
                               viewModel.deleteCurrentWeatherFromDB()
                               viewModel.insertCurrentWeatherToDB(result.data)
                               Log.i("DATAAAA", "${result.data.current.temp}")
                           }
                           else -> {
                               binding.progressBar.visibility = View.GONE
                               Log.i("ERRRRORR", "ERRRRORR: ")
                           }
                       }
                   }
               }
           }

        } else {
            Log.i("DataBassse", "hiiiiiiii")
            //call DataBase
            viewModel.getCurrentWeather()
            Snackbar.make(rootView, getString(R.string.checkInternet), Snackbar.LENGTH_LONG).show()

//            Toast.makeText(context, "Please Check your internet connection", Toast.LENGTH_SHORT)
//                .show()
            lifecycleScope.launch {
                viewModel.weatherData.collectLatest { result ->
                    when (result) {
                        is ResponseState.Loading -> {
                            Log.i("LOADING", "LOADING: ")
                            binding.progressBar.visibility = View.VISIBLE
                        }
                        is ResponseState.Success -> {
                            binding.progressBar.visibility = View.GONE
                            fitWeatherDataToUi(result.data)
                            Log.i("DATAAAA", "${result.data.current.temp}")
                        }
                        else -> {
                            binding.progressBar.visibility = View.GONE
                            Log.i("ERRRRORR", "ERRRRORR: ")
                        }
                    }
                }
            }
        }

        Log.i("LLLat", "$lat + $long")

//        if (lat != null && long != null) {
//            viewModel.getWeatherData(lat!!, long!!)
//        }


    }

    fun fitWeatherDataToUi(weatherDetails: WeatherResponse) {
        //first part
        binding.cityTv.text = getAddressGeoCoder(lat,long,requireContext(),languageFromSP)
        binding.dateTv.text = getFormattedDate(weatherDetails.current.dt,languageFromSP)
        if(languageFromSP == "ar"){
            binding.tempTv.text = "${convertToArabicNumber(weatherDetails.current.temp.toInt())} ${getUnit(tempUnit,languageFromSP)}"
            //last part
            binding.pressureTv.text = convertToArabicNumber(weatherDetails.current.pressure)
            binding.humidityTv.text = convertToArabicNumber(weatherDetails.current.humidity) +"%"
//            binding.windTv.text = convertToArabicNumber((getWindSpeed(windUnit , tempUnit ,weatherDetails.current.windSpeed,requireContext())).toInt())
            binding.windTv.text = convertToArabicNumber(weatherDetails.current.windSpeed.toInt()) + getCurrentSpeed(requireContext())
            binding.cloudTv.text = convertToArabicNumber(weatherDetails.current.clouds)+"%"
            binding.ultravioletTv.text = convertToArabicNumber(weatherDetails.current.uvi.toInt())
            binding.visibilityTv.text = convertToArabicNumber(weatherDetails.current.visibility)+ getString(R.string.m)
        }else{
            binding.tempTv.text = weatherDetails.current.temp.toInt().toString() + " ${getUnit(tempUnit,languageFromSP)}"
            //last part
            binding.pressureTv.text = weatherDetails.current.pressure.toString()
            binding.humidityTv.text = weatherDetails.current.humidity.toString() +"%"
//            binding.windTv.text = getWindSpeed(windUnit , tempUnit ,weatherDetails.current.windSpeed,requireContext())
            binding.windTv.text = weatherDetails.current.windSpeed.toBigDecimal().setScale(2, RoundingMode.UP).toString()+ getCurrentSpeed(requireContext())
            binding.cloudTv.text = weatherDetails.current.clouds.toString()+"%"
            binding.ultravioletTv.text = weatherDetails.current.uvi.toString()
            binding.visibilityTv.text = weatherDetails.current.visibility.toString()+ getString(R.string.m)
        }

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
    }

}