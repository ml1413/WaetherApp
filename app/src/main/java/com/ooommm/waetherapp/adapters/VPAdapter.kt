package com.ooommm.waetherapp.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class VPAdapter(
    fragmentActivity: FragmentActivity,
    private val listFragment: List<Fragment>
) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int = listFragment.size


    override fun createFragment(position: Int): Fragment = listFragment[position]
}