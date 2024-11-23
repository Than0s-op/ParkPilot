package com.application.parkpilot.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.application.parkpilot.R
import com.application.parkpilot.User
import com.application.parkpilot.databinding.ControllerBinding
import com.application.parkpilot.viewModel.Controller

class Controller : AppCompatActivity() {
    private lateinit var binding: ControllerBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // object creation and initialization of views
        binding = ControllerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.bottomNavigationView.itemIconTintList = null

        val viewModel = ViewModelProvider(this)[Controller::class.java]

        setMenuVisibility()


        // load the profile image in search bar if present
        viewModel.loadProfileImage(
            this,
            binding.bottomNavigationView.menu.findItem(R.id.buttonProfile)
        )

        // default fragment
        changeFragment(viewModel.spotList)

        // when search bar menu's items clicked
        binding.bottomNavigationView.setOnItemSelectedListener { clickedItem ->
            if (binding.bottomNavigationView.selectedItemId != clickedItem.itemId) {

                when (clickedItem.itemId) {
                    R.id.buttonList -> {
                        changeFragment(viewModel.spotList)
                        return@setOnItemSelectedListener true
                    }

                    R.id.buttonProfile -> {
                        changeFragment(viewModel.setting)
                        return@setOnItemSelectedListener true
                    }

                    R.id.buttonHistory -> {
                        changeFragment(viewModel.bookingHistory)
                        return@setOnItemSelectedListener true
                    }

                    R.id.buttonMap -> {
                        changeFragment(viewModel.mapFragment)
                        return@setOnItemSelectedListener true
                    }
                }
            }
            return@setOnItemSelectedListener false
        }
    }

    private fun changeFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.frameLayout, fragment)
            commit()
        }
    }

    private fun setMenuVisibility() {
        User.apply {
            when (type) {
                ANONYMOUS -> {
                    binding.bottomNavigationView.menu.findItem(R.id.buttonHistory).isVisible = false
                }

                FINDER -> {

                }
            }
        }
    }
}