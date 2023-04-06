package com.example.weatherapp.ui.alerts.view

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.R
import com.example.weatherapp.database.ConcreteLocalSource
import com.example.weatherapp.databinding.FragmentAlertsBinding
import com.example.weatherapp.isInternetConnected
import com.example.weatherapp.model.FavState
import com.example.weatherapp.model.MyAlert
import com.example.weatherapp.model.Repository
import com.example.weatherapp.model.ResponseState
import com.example.weatherapp.network.WeatherClient
import com.example.weatherapp.ui.MainActivity
import com.example.weatherapp.ui.alerts.viewmodel.AlertViewModel
import com.example.weatherapp.ui.alerts.viewmodel.AlertViewModelFactory
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAlertsBinding.inflate(inflater, container, false)
        checkOverlayPermission()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val rootView = requireActivity().findViewById<View>(android.R.id.content)
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
        binding.alertsFab.setOnClickListener {
            if(isInternetConnected(requireContext())){
                SelectTimeDialog().show(requireActivity().supportFragmentManager, "Hi Alert Diaolg")
            }else{
                Snackbar.make(rootView, getString(R.string.checkInternet), Snackbar.LENGTH_LONG).show()
            }
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

    private fun checkOverlayPermission() {
        if (!Settings.canDrawOverlays(requireContext())) {
//            val alertDialogBuilder = MaterialAlertDialogBuilder(requireContext())
//            alertDialogBuilder.setTitle(getString(R.string.weatherAlerts))
//                .setMessage(getString(R.string.features))
//                .setPositiveButton(getString(R.string.ok)) { dialog: DialogInterface, i: Int ->
//                    var myIntent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
//                    startActivity(myIntent)
//                    dialog.dismiss()
//                }.setNegativeButton(
//                    getString(R.string.cancel)
//                ) { dialog: DialogInterface, i: Int ->
//                    dialog.dismiss()
//                    startActivity(Intent(requireContext(),MainActivity::class.java))
//                }.show()
            AlertDialog.Builder(requireContext())
                .setTitle(getString(R.string.weatherAlerts))
                .setMessage(getString(R.string.features))
                .setPositiveButton(android.R.string.ok) { _, _ ->
                    var myIntent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
                    startActivity(myIntent)
                }
                .setNegativeButton(android.R.string.cancel, null)
                .show()
        }
    }
}

