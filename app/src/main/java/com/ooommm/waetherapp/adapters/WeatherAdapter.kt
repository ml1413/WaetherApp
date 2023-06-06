package com.ooommm.waetherapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.ooommm.waetherapp.R
import com.ooommm.waetherapp.adapters.viewholder.Holder1
import com.ooommm.waetherapp.model.WeatherModel

class WeatherAdapter(val listener: Listener?) : ListAdapter<WeatherModel, Holder1>(Comparator()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder1 {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        return Holder1(view, listener)
    }

    override fun onBindViewHolder(holder: Holder1, position: Int) {
        holder.bind(getItem(position))
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    class Comparator : DiffUtil.ItemCallback<WeatherModel>() {
        override fun areItemsTheSame(oldItem: WeatherModel, newItem: WeatherModel): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: WeatherModel, newItem: WeatherModel): Boolean {
            return oldItem == newItem
        }
    }
}












