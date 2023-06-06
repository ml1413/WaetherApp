package com.ooommm.waetherapp

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ooommm.waetherapp.model.WeatherModel

class MainViewModel : ViewModel() {
    val livaDataCurrent = MutableLiveData<WeatherModel>()
    val liveDataList = MutableLiveData<List<WeatherModel>>()
}