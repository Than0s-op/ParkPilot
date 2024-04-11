package com.application.parkpilot.activity

import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.application.parkpilot.R
import com.application.parkpilot.User
import com.application.parkpilot.fragment.BookingHistory
import com.application.parkpilot.fragment.Map
import com.application.parkpilot.fragment.Setting
import com.application.parkpilot.fragment.SpotList
import com.application.parkpilot.viewModel.HomeViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView

class FragmentController : AppCompatActivity(R.layout.fragment_controller) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // object creation and initialization of views

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)

        val viewModel = ViewModelProvider(this)[HomeViewModel::class.java]

        val spotList = SpotList()
        val mapFragment = Map()
        val bookingHistory = BookingHistory()
        val setting = Setting()

        setMenuVisibility(bottomNavigationView.menu)

        // load the profile image in search bar if present
        viewModel.loadProfileImage(this, bottomNavigationView.menu.findItem(R.id.buttonProfile))

        // default fragment
        changeFragment(spotList)

        // when search bar menu's items clicked
        bottomNavigationView.setOnItemSelectedListener { clickedItem ->
            if (bottomNavigationView.selectedItemId != clickedItem.itemId) {

                when (clickedItem.itemId) {
                    R.id.buttonList -> {
                        changeFragment(spotList)
                        return@setOnItemSelectedListener true
                    }

                    R.id.buttonProfile -> {
                        changeFragment(setting)
                        return@setOnItemSelectedListener true
                    }

                    R.id.buttonHistory -> {
                        changeFragment(bookingHistory)
                        return@setOnItemSelectedListener true
                    }

                    R.id.buttonMap -> {
                        changeFragment(mapFragment)
                        return@setOnItemSelectedListener true
                    }

                }
            }
            return@setOnItemSelectedListener false
        }
    }
    private fun changeFragment(fragment: Fragment){
        supportFragmentManager.beginTransaction().apply{
            replace(R.id.frameLayout,fragment)
            commit()
        }
    }
    private fun setMenuVisibility(menu: Menu) {
        User.apply {
            when (type) {
                ANONYMOUS -> {
                    menu.findItem(R.id.buttonHistory).isVisible = false
                }

                FINDER -> {

                }
            }
        }
    }
}