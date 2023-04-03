package com.example.weatherapp.ui.alerts.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.R
import com.example.weatherapp.database.ConcreteLocalSource
import com.example.weatherapp.databinding.FragmentAlertsBinding
import com.example.weatherapp.model.FavState
import com.example.weatherapp.model.MyAlert
import com.example.weatherapp.model.Repository
import com.example.weatherapp.model.ResponseState
import com.example.weatherapp.network.WeatherClient
import com.example.weatherapp.ui.alerts.viewmodel.AlertViewModel
import com.example.weatherapp.ui.alerts.viewmodel.AlertViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class AlertsFragment : Fragment() , OnClickAlert{

    lateinit var binding: FragmentAlertsBinding
    lateinit var myFactory: AlertViewModelFactory
    lateinit var viewModel: AlertViewModel
    lateinit var alertAdapter: AlertAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
        binding = FragmentAlertsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        alertAdapter = AlertAdapter(ArrayList(),this, requireActivity())
        val layoutManager =
            LinearLayoutManager(requireActivity(), RecyclerView.VERTICAL, false)
        binding.alertsRecyclerview.layoutManager = layoutManager

        myFactory = AlertViewModelFactory(
            Repository.getInstance(
                WeatherClient.getInstance(),
                ConcreteLocalSource(requireContext()),
                requireContext()
            )
        )
        viewModel = ViewModelProvider(this, myFactory).get(AlertViewModel::class.java)

        lifecycleScope.launch {
            viewModel.alertData.collectLatest { result ->
                when (result) {
                    is ResponseState.Loading -> {
                        Log.i("LOADING", "LOADING: ")
                    }
                    is ResponseState.Success -> {
                        binding.alertIconIv.visibility = View.GONE
                        binding.addPlacesTv.visibility = View.GONE
                        alertAdapter.setListAlerts(result.data)
                        alertAdapter.notifyDataSetChanged()
                        binding.alertsRecyclerview.adapter = alertAdapter
                        Log.i("LOADINGFavv", "LOADING: ${result.data} ")
                    }
                    else -> {
                        Log.i("ERRRRORR", "ERRRRORR: ")
                    }
                }
            }
        }

        binding.backAlerts.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_alertsFragment_to_homeFragment)
        }

        binding.alertsFab.setOnClickListener {
            SelectTimeDialog().show(requireActivity().supportFragmentManager, "Hi Alert Diaolg")
        }
    }

    override fun onClickDelete(myAlert: MyAlert) {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.delete_confirm))
            .setMessage(getString(R.string.delete_qustion))
            .setPositiveButton(android.R.string.ok) { _, _ ->
                viewModel.deleteAlertFromDB(myAlert)
            }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
    }
}

