package com.example.weatherapp.ui.favorite.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
import com.example.weatherapp.databinding.FragmentFavoriteBinding
import com.example.weatherapp.isInternetConnected
import com.example.weatherapp.model.FavState
import com.example.weatherapp.model.FavoriteLocation
import com.example.weatherapp.model.Repository
import com.example.weatherapp.model.ResponseState
import com.example.weatherapp.network.WeatherClient
import com.example.weatherapp.ui.favorite.viewmodel.FavViewModel
import com.example.weatherapp.ui.favorite.viewmodel.FavViewModelFactory
import com.example.weatherapp.ui.home.view.DailyAdapter
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class FavoriteFragment : Fragment(), OnClickFavorite {

    lateinit var binding: FragmentFavoriteBinding
    lateinit var myFactory: FavViewModelFactory
    lateinit var viewModel: FavViewModel
    lateinit var favAdapter: FavAdapter
    lateinit var rootView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rootView = requireActivity().findViewById<View>(android.R.id.content)

        favAdapter = FavAdapter(ArrayList(), this, requireActivity())
        val layoutManager =
            LinearLayoutManager(requireActivity(), RecyclerView.VERTICAL, false)
        binding.favRecyclerview.layoutManager = layoutManager

        myFactory = FavViewModelFactory(
            Repository.getInstance(
                WeatherClient.getInstance(),
                ConcreteLocalSource(requireContext()),
                requireContext()
            )
        )
        viewModel = ViewModelProvider(this, myFactory).get(FavViewModel::class.java)

        lifecycleScope.launch {
            viewModel.favData.collectLatest { result ->
                when (result) {
                    is ResponseState.Loading -> {
                        Log.i("LOADING", "LOADING: ")
                    }
                    is ResponseState.Success -> {
                        if(result.data.isNullOrEmpty()){
                            binding.favIconIv.visibility = View.VISIBLE
                            binding.addPlacesTv.visibility = View.VISIBLE
                            binding.favRecyclerview.visibility = View.GONE
                        }else{
                            binding.favRecyclerview.visibility = View.VISIBLE
                            binding.favIconIv.visibility = View.GONE
                            binding.addPlacesTv.visibility = View.GONE
                            favAdapter.setListFav(result.data)
                            favAdapter.notifyDataSetChanged()
                            binding.favRecyclerview.adapter = favAdapter
                            Log.i("LOADINGFavv", "LOADING: ${result.data} ")
                        }
                    }
                    else -> {
                        Log.i("ERRRRORR", "ERRRRORR: ")
                    }
                }
            }
        }
        binding.favFab.setOnClickListener {
            if (isInternetConnected(requireContext())) {
                Navigation.findNavController(view)
                    .navigate(R.id.action_favoriteFragment_to_mapFragment)
            } else {
                Snackbar.make(rootView, getString(R.string.checkInternet), Snackbar.LENGTH_LONG)
                    .show()
            }
        }
    }

    override fun onClick(favoriteLocation: FavoriteLocation) {
        if (isInternetConnected(requireContext())) {
            Navigation.findNavController(requireView())
                .navigate(FavoriteFragmentDirections.actionFavoriteFragmentToHomeFragment().apply {
                    favComing = true
                    favLat = favoriteLocation.latitude.toString()
                    favLong = favoriteLocation.longitude.toString()
                    Log.i("ArrrrrgsFavv", "$favLat + $favLong")
                    //favoriteArgs = favoriteLocation
                })
        } else {
            Snackbar.make(rootView, getString(R.string.checkInternet), Snackbar.LENGTH_LONG).show()
        }

    }

    override fun onClickDelete(favoriteLocation: FavoriteLocation) {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.delete_confirm))
            .setMessage(getString(R.string.delete_qustion))
            .setPositiveButton(android.R.string.ok) { _, _ ->
                viewModel.deleteFavFromDB(favoriteLocation)
            }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
    }

}