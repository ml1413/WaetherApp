package com.ooommm.waetherapp.adapters

import com.ooommm.waetherapp.model.WeatherModel

interface Listener {
    fun onClick(item: WeatherModel)
}