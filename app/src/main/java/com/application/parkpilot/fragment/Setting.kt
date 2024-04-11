package com.application.parkpilot.fragment

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.application.parkpilot.R
import com.application.parkpilot.User
import com.application.parkpilot.viewModel.ProfileViewModel

class Setting : Fragment(R.layout.profile) {
    private lateinit var imageViewProfilePicture: ImageView
    private lateinit var textViewUserName: TextView
    private lateinit var textViewPersonalInformation: TextView
    private lateinit var textViewLogin: TextView
    private lateinit var textViewLogout: TextView
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        imageViewProfilePicture = view.findViewById(R.id.imageViewProfilePicture)
        textViewUserName = view.findViewById(R.id.textViewUserName)
        textViewPersonalInformation = view.findViewById(R.id.textViewPersonalInformation)
        textViewLogin = view.findViewById(R.id.textViewLogin)
        textViewLogout = view.findViewById(R.id.textViewLogout)

        loadViews()

        val viewModel = ProfileViewModel()

        viewModel.loadProfile(requireContext(), imageViewProfilePicture, textViewUserName)

        textViewPersonalInformation.setOnClickListener {
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
                textViewPersonalInformation.visibility = View.GONE
                textViewLogout.visibility = View.GONE
            }

            User.FINDER -> {
                textViewLogin.visibility = View.GONE
            }
        }
    }
}