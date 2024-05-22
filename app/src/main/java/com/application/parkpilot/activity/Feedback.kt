package com.application.parkpilot.activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.application.parkpilot.Feedback
import com.application.parkpilot.R
import com.application.parkpilot.User
import com.application.parkpilot.databinding.FeedbackBinding
import com.application.parkpilot.databinding.FeedbackFormBinding
import com.application.parkpilot.viewModel.FeedbackViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class Feedback : AppCompatActivity(R.layout.feedback) {
    private lateinit var binding: FeedbackBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FeedbackBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val stationUID = intent.getStringExtra("stationUID")!!
        val viewModel = ViewModelProvider(this)[FeedbackViewModel::class.java]

        setVisibility()

        val formBinding: FeedbackFormBinding = FeedbackFormBinding.inflate(layoutInflater)
        viewModel.loadProfileImage(formBinding.imageViewProfilePicture)

        val dialogInflater =
            MaterialAlertDialogBuilder(this).setView(formBinding.root).create()


        formBinding.buttonOk.setOnClickListener {
            if (formBinding.editTextFeedback.text!!.isBlank()) {
                formBinding.editTextFeedback.error = "Message must not be blank"
                return@setOnClickListener
            }

            viewModel.setFeedback(
                stationUID,
                Feedback(
                    User.UID,
                    formBinding.ratingBar.rating,
                    formBinding.editTextFeedback.text.toString()
                )
            )
            dialogInflater.dismiss()
        }

        formBinding.buttonCancel.setOnClickListener {
            dialogInflater.dismiss()
        }

        formBinding.ratingBar.setOnRatingBarChangeListener { _, rating, _ ->
            if (rating < 1) formBinding.ratingBar.rating = 1.0f
        }

        binding.buttonEditFeedback.setOnClickListener {
            viewModel.loadFeedback(stationUID, formBinding.ratingBar, formBinding.editTextFeedback)
            dialogInflater.show()
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.loadRecycler(this, stationUID, binding.recyclerView)
            binding.swipeRefreshLayout.isRefreshing = false
        }

        viewModel.loadRecycler(this, stationUID, binding.recyclerView)
    }

    private fun setVisibility() {
        User.apply {
            when (type) {
                ANONYMOUS -> {
                    binding.buttonEditFeedback.visibility = View.GONE
                }

                FINDER -> {

                }
            }
        }
    }
}