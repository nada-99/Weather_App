package com.example.weatherapp.ui.home

import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.weatherapp.R
import com.example.weatherapp.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    lateinit var binding : FragmentHomeBinding
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

////        val mDrawerLayout : DrawerLayout = view.findViewById(R.id.drawerLayout)
//        binding.menuIv.setOnClickListener {
//            mDrawerLayout.openDrawer(GravityCompat.START)
//        }
    }
}