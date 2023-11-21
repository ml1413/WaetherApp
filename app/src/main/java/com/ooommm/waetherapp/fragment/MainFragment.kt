package com.ooommm.waetherapp.fragment

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.media.audiofx.Equalizer.Settings
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.material.tabs.TabLayoutMediator
import com.ooommm.waetherapp.DialogManager
import com.ooommm.waetherapp.DialogSearchByName
import com.ooommm.waetherapp.MainViewModel
import com.ooommm.waetherapp.adapters.VPAdapter
import com.ooommm.waetherapp.databinding.FragmentMainBinding
import com.ooommm.waetherapp.model.WeatherModel
import com.squareup.picasso.Picasso
import org.json.JSONObject

private const val API_KEY = "ec25a1b2e674407295082419230206"

class MainFragment : Fragment() {
    private lateinit var fLocationClient: FusedLocationProviderClient
    private val fragmentList = listOf(
        HoursFragment.newInstance(),
        DaysFragment.newInstance()
    )
    private val tabNameList = listOf(
        "Hours",
        "Days"
    )
    private lateinit var pLauncher: ActivityResultLauncher<String>
    private lateinit var binding: FragmentMainBinding
    private val model: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkPermission()
        init()
        updateCurrentCard()
    }

    private fun init() {

        fLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        val adapter =
            VPAdapter(fragmentActivity = activity as FragmentActivity, listFragment = fragmentList)
        binding.vp2.adapter = adapter

        TabLayoutMediator(binding.tabLayout, binding.vp2) { tab, position ->
            tab.text = tabNameList[position]
        }.attach()

        binding.ibSync.setOnClickListener {
            binding.tabLayout.selectTab(binding.tabLayout.getTabAt(0))
            checkLocation()
        }
        binding.ibSearch.setOnClickListener {
            DialogSearchByName.searchByNameDialog(requireContext(),
                object : DialogSearchByName.Listener {
                    override fun onClick(city: String) {
                        Toast.makeText(requireContext(), city, Toast.LENGTH_SHORT).show()
                        requestWeatherData(cityOrLocation = city)
                    }
                })
        }

    }

    override fun onResume() {
        super.onResume()
        checkLocation()
    }

    private fun checkLocation() {
        if (isLocationEnabled()) {
            getLocation()
        } else {
            DialogManager.locationSettingsDialog(requireContext(), object : DialogManager.Listener {
                override fun onClick() {
                    startActivity(Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS))

                }
            })
        }
    }

    private fun getLocation() {
        val ct = CancellationTokenSource()
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, ct.token)
            .addOnCompleteListener {
                requestWeatherData(cityOrLocation = "${it.result.latitude},${it.result.longitude}")
            }
    }

    private fun isLocationEnabled(): Boolean {
        val lm = activity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    private fun updateCurrentCard() = with(binding) {
        model.livaDataCurrent.observe(viewLifecycleOwner) {

            val maxMinTemp = "${it.minTemp}°C / ${it.maxTemp}°C"
            val urlImage = "https:${it.imageUrl}"
            tvData.text = it.time
            tvCity.text = it.city
            tvCurrentTemp.text = it.currentTemp.ifEmpty { maxMinTemp }
            tvCondition.text = it.condition
            tvMinMax.text = if (it.currentTemp.isEmpty()) "" else maxMinTemp
            Picasso.get().load(urlImage).into(ivWeather)

        }
    }

    private fun checkPermission() {
        if (!isPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION)) {
            permissionListener()
            pLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun permissionListener() {
        pLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            Toast.makeText(activity, it.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    private fun requestWeatherData(cityOrLocation: String) {
        val url = "https://api.weatherapi.com/v1/forecast.json?key=" +
                API_KEY +//API KEY
                "&q=" +
                cityOrLocation +//CITY
                "&days=" +
                "7" +
                "&aqi=no&alerts=no"


        val request = StringRequest(
            Request.Method.GET,
            url,
            { result ->
                parsWeatherData(result)
                Log.d("TAG1", "requestWeatherData: rusult $result")

            },
            { error ->
                Log.d("TAG1", "requestWeatherData: Error $error")
            }
        )

        val queue = Volley.newRequestQueue(context)
        queue.add(request)
    }

    private fun parsWeatherData(result: String) {
        val mainObject = JSONObject(result)
        val list = parseDays(mainObject)
        parseCurrentData(mainObject, list[0])
    }

    private fun parseDays(jsonObject: JSONObject): List<WeatherModel> {
        val list = ArrayList<WeatherModel>()
        val daysArray = jsonObject
            .getJSONObject("forecast")
            .getJSONArray("forecastday")

        val name = jsonObject
            .getJSONObject("location")
            .getString("name")

        for (i in 1 until daysArray.length()) {
            val day = daysArray[i] as JSONObject
            val item = WeatherModel(
                city = name,
                time = day
                    .getString("date"),
                condition = day
                    .getJSONObject("day")
                    .getJSONObject("condition")
                    .getString("text"),
                currentTemp = "",
                maxTemp = day
                    .getJSONObject("day")
                    .getString("maxtemp_c").toFloat().toInt().toString(),
                minTemp = day
                    .getJSONObject("day")
                    .getString("mintemp_c").toFloat().toInt().toString(),
                imageUrl = day
                    .getJSONObject("day")
                    .getJSONObject("condition")
                    .getString("icon"),
                hours = day
                    .getJSONArray("hour").toString()
            )
            list.add(item)
        }
        model.liveDataList.value = list
        return list
    }

    private fun parseCurrentData(jsonObject: JSONObject, weatherItem: WeatherModel) {
        val item = WeatherModel(
            city = jsonObject
                .getJSONObject("location")
                .getString("name"),
            time = jsonObject
                .getJSONObject("current")
                .getString("last_updated"),
            condition = jsonObject
                .getJSONObject("current")
                .getJSONObject("condition")
                .getString("text"),
            currentTemp = jsonObject
                .getJSONObject("current")
                .getString("temp_c"),
            maxTemp = weatherItem.maxTemp,
            minTemp = weatherItem.minTemp,
            imageUrl = jsonObject
                .getJSONObject("current")
                .getJSONObject("condition")
                .getString("icon"),
            hours = weatherItem.hours

        )
        model.livaDataCurrent.value = item
    }


    companion object {
        @JvmStatic
        fun newInstance(): MainFragment {
            return MainFragment()
        }
    }


}