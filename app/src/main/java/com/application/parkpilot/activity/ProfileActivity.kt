package com.application.parkpilot.activity

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.application.parkpilot.R
import com.application.parkpilot.User
import com.application.parkpilot.viewModel.ProfileViewModel

class ProfileActivity : AppCompatActivity(R.layout.profile) {

    private lateinit var imageViewProfilePicture: ImageView
    private lateinit var textViewUserName: TextView
    private lateinit var textViewPersonalInformation: TextView
    private lateinit var textViewSpotDetail: TextView
    private lateinit var textViewLogin: TextView
    private lateinit var textViewLogout: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        imageViewProfilePicture = findViewById(R.id.imageViewProfilePicture)
        textViewUserName = findViewById(R.id.textViewUserName)
        textViewPersonalInformation = findViewById(R.id.textViewPersonalInformation)
        textViewSpotDetail = findViewById(R.id.textViewSpotDetail)
        textViewLogin = findViewById(R.id.textViewLogin)
        textViewLogout = findViewById(R.id.textViewLogout)

        loadViews()

        val viewModel = ProfileViewModel()

        viewModel.loadProfile(this,imageViewProfilePicture,textViewUserName)

        textViewPersonalInformation.setOnClickListener{
            viewModel.personalInformation(this)
        }
        textViewSpotDetail.setOnClickListener {
            viewModel.spotDetail(this)
        }
        textViewLogout.setOnClickListener {
            viewModel.logout(this)
        }
        textViewLogin.setOnClickListener {
            viewModel.login(this)
        }
    }

    private fun loadViews() {
        when (User.type) {
            User.ANONYMOUS -> {
                textViewPersonalInformation.visibility = View.GONE
                textViewSpotDetail.visibility = View.GONE
                textViewLogout.visibility = View.GONE
            }

            User.OWNER -> {
                textViewLogin.visibility = View.GONE
            }

            User.FINDER -> {
                textViewSpotDetail.visibility = View.GONE
                textViewLogin.visibility = View.GONE
            }
        }
    }
}