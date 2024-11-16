package com.application.parkpilot.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.application.parkpilot.R
import com.application.parkpilot.User
import com.application.parkpilot.databinding.SettingBinding
import com.application.parkpilot.viewModel.SettingViewModel

class Setting : Fragment(R.layout.setting) {
    private lateinit var binding: SettingBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = SettingBinding.bind(view)

        loadViews()

        val viewModel = SettingViewModel()

        viewModel.loadProfile(
            requireContext(),
            binding.imageViewProfilePicture,
            binding.textViewUserName
        ) {
            hideShimmer()
        }

        binding.buttonEditProfile.setOnClickListener {
            viewModel.personalInformation(requireContext())
        }
        binding.textViewLogout.setOnClickListener {
            viewModel.logout(requireContext())
        }
        binding.textViewLogin.setOnClickListener {
            viewModel.login(requireContext())
        }
    }

    private fun hideShimmer(){
        binding.shimmerLayout.visibility = View.GONE
        binding.textViewUserName.visibility = View.VISIBLE
        binding.imageViewProfilePicture.visibility = View.VISIBLE
    }

    private fun loadViews() {
        when (User.type) {
            User.ANONYMOUS -> {
                binding.buttonEditProfile.visibility = View.GONE
                binding.textViewLogout.visibility = View.GONE
            }

            User.FINDER -> {
                binding.textViewLogin.visibility = View.GONE
            }
        }
    }
}