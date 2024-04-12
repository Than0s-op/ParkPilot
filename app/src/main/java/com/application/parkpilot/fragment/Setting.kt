package com.application.parkpilot.fragment

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.application.parkpilot.R
import com.application.parkpilot.User
import com.application.parkpilot.viewModel.SettingViewModel

class Setting : Fragment(R.layout.setting) {
    private lateinit var imageViewProfilePicture: ImageView
    private lateinit var textViewUserName: TextView
    private lateinit var buttonEditProfile: Button
    private lateinit var textViewLogin: TextView
    private lateinit var textViewLogout: TextView
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        imageViewProfilePicture = view.findViewById(R.id.imageViewProfilePicture)
        textViewUserName = view.findViewById(R.id.textViewUserName)
        buttonEditProfile = view.findViewById(R.id.buttonEditProfile)
        textViewLogin = view.findViewById(R.id.textViewLogin)
        textViewLogout = view.findViewById(R.id.textViewLogout)

        loadViews()

        val viewModel = SettingViewModel()

        viewModel.loadProfile(requireContext(), imageViewProfilePicture, textViewUserName)

        buttonEditProfile.setOnClickListener {
            viewModel.personalInformation(requireContext())
        }
        textViewLogout.setOnClickListener {
            viewModel.logout(requireContext())
        }
        textViewLogin.setOnClickListener {
            viewModel.login(requireContext())
        }
    }

    private fun loadViews() {
        when (User.type) {
            User.ANONYMOUS -> {
                buttonEditProfile.visibility = View.GONE
                textViewLogout.visibility = View.GONE
            }

            User.FINDER -> {
                textViewLogin.visibility = View.GONE
            }
        }
    }
}