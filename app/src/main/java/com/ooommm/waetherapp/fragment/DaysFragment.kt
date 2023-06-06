package com.ooommm.waetherapp.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.ooommm.waetherapp.MainViewModel
import com.ooommm.waetherapp.adapters.Listener
import com.ooommm.waetherapp.adapters.WeatherAdapter
import com.ooommm.waetherapp.databinding.FragmentDaysBinding
import com.ooommm.waetherapp.model.WeatherModel

class DaysFragment : Fragment(), Listener {
    private lateinit var adapter: WeatherAdapter
    private lateinit var binding: FragmentDaysBinding
    private val model: MainViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDaysBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        model.liveDataList.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
    }

    private fun init() {
        adapter = WeatherAdapter(this@DaysFragment)
        binding.rcViewDays.layoutManager = LinearLayoutManager(activity)
        binding.rcViewDays.adapter = adapter
    }

    companion object {
        @JvmStatic
        fun newInstance() = DaysFragment()
    }

    override fun onClick(item: WeatherModel) {
        model.livaDataCurrent.value = item
    }
}