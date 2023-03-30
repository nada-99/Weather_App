package com.example.weatherapp.ui.favorite.view

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.databinding.FavoriteRowBinding
import com.example.weatherapp.model.Daily
import com.example.weatherapp.model.FavoriteLocation

private lateinit var binding: FavoriteRowBinding

class FavAdapter(private var favList: List<FavoriteLocation>, var myListener: OnClickFavorite, val context: Context) :
    RecyclerView.Adapter<FavAdapter.ViewHolder>() {

    fun setListFav(values: List<FavoriteLocation?>?) {
        this.favList = values as List<FavoriteLocation>
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater: LayoutInflater = parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = FavoriteRowBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentFav = favList[position]
        holder.binding.cityNameTv.text = currentFav.address
        holder.binding.menuFavIv.setOnClickListener{
            myListener.onClick(currentFav)
        }
    }

    override fun getItemCount() = favList.size

    inner class ViewHolder(var binding: FavoriteRowBinding) : RecyclerView.ViewHolder(binding.root)

}