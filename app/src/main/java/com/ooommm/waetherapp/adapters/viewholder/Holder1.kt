package com.ooommm.waetherapp.adapters.viewholder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.ooommm.waetherapp.adapters.Listener
import com.ooommm.waetherapp.databinding.ListItemBinding
import com.ooommm.waetherapp.model.WeatherModel
import com.squareup.picasso.Picasso

class Holder1(view: View, val listener: Listener?) : RecyclerView.ViewHolder(view) {
    val binding = ListItemBinding.bind(view)
    var itemTemp: WeatherModel? = null

    init {
        itemView.setOnClickListener {
            itemTemp?.let { it1 -> listener?.onClick(it1) }
        }
    }

    fun bind(item: WeatherModel) = with(binding) {
        itemTemp = item
        tvData.text = item.time
        tvCondition.text = item.condition
        tvTemp.text = item.currentTemp.ifEmpty { "${item.minTemp}°C / ${item.maxTemp}°C" }
        Picasso.get().load("https:" + item.imageUrl).into(imView)
    }

}