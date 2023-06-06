package com.ooommm.waetherapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ooommm.waetherapp.databinding.ActivityMainBinding
import com.ooommm.waetherapp.fragment.MainFragment


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        supportFragmentManager
            .beginTransaction()
            .replace(R.id.cl_container, MainFragment.newInstance())
            .commit()
    }


}