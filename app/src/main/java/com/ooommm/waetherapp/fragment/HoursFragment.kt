package com.ooommm.waetherapp.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.activityViewModels
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.ooommm.waetherapp.MainViewModel
import com.ooommm.waetherapp.adapters.WeatherAdapter
import com.ooommm.waetherapp.databinding.FragmentHoursBinding
import com.ooommm.waetherapp.model.WeatherModel
import org.json.JSONArray
import org.json.JSONObject


class HoursFragment : Fragment() {

    private lateinit var binding: FragmentHoursBinding
    private lateinit var adapter: WeatherAdapter
    private val model: MainViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHoursBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecycleView()

        model.livaDataCurrent.observe(viewLifecycleOwner) {
            adapter.submitList(getHoursList(it))
        }
    }

    private fun initRecycleView() = with(binding) {
        rcView.layoutManager = LinearLayoutManager(activity)
        adapter = WeatherAdapter(null)
        rcView.adapter = adapter
    }

    private fun getHoursList(wItem: WeatherModel): List<WeatherModel> {
        val list = ArrayList<WeatherModel>()
        val hoursArray = JSONArray(wItem.hours)
        for (i in 0 until hoursArray.length()) {
            val item = WeatherModel(
                city = wItem.city,
                time = (hoursArray[i] as JSONObject)
                    .getString("time"),
                condition = (hoursArray[i] as JSONObject)
                    .getJSONObject("condition")
                    .getString("text"),
                currentTemp = (hoursArray[i] as JSONObject)
                    .getString("temp_c") + "°C",
                maxTemp = "",
                minTemp = "",
                imageUrl = (hoursArray[i] as JSONObject)
                    .getJSONObject("condition")
                    .getString("icon"),
                hours = ""
            )
            list.add(item)
        }
        return list
    }

    companion object {
        @JvmStatic
        fun newInstance() = HoursFragment()
    }
}