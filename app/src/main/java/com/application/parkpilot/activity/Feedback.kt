package com.application.parkpilot.activity

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.size
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
    private lateinit var formBinding: FeedbackFormBinding
    private lateinit var viewModel: FeedbackViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FeedbackBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val stationUID = intent.getStringExtra("stationUID")!!
        viewModel = ViewModelProvider(this)[FeedbackViewModel::class.java]

        setVisibility()

        formBinding = FeedbackFormBinding.inflate(layoutInflater)
        viewModel.loadProfileImage(formBinding.imageViewProfilePicture)

        val dialogInflater =
            MaterialAlertDialogBuilder(this)
                .setView(formBinding.root).create()


        formBinding.buttonOk.setOnClickListener {
            if (formBinding.editTextFeedback.text!!.isBlank()) {
                formBinding.editTextFeedback.error = "Message must not be blank"
                return@setOnClickListener
            }
            showReviewSaveProcess()
            viewModel.setFeedback(
                stationUID,
                Feedback(
                    User.UID,
                    formBinding.ratingBar.rating,
                    formBinding.editTextFeedback.text.toString()
                ),
            ) {
                hideReviewSaveProcess()
                dialogInflater.dismiss()
            }
        }

        formBinding.buttonCancel.setOnClickListener {
            dialogInflater.dismiss()
        }

        formBinding.ratingBar.setOnRatingBarChangeListener { _, rating, _ ->
            if (rating < 1) formBinding.ratingBar.rating = 1.0f
        }

        binding.topAppBar.setNavigationOnClickListener {
            finish()
        }

        binding.buttonEditFeedback.setOnClickListener {
            viewModel.loadFeedback(stationUID, formBinding.ratingBar, formBinding.editTextFeedback)
            dialogInflater.show()
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            showShimmer()
            viewModel.loadRecycler(this, stationUID, binding.recyclerView) {
                hideShimmer()
            }
            binding.swipeRefreshLayout.isRefreshing = false
        }

        showShimmer()
        viewModel.loadRecycler(this, stationUID, binding.recyclerView) {
            hideShimmer()
        }
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

    private fun showShimmer() {
        binding.shimmerLayout.visibility = View.VISIBLE
        binding.swipeRefreshLayout.visibility = View.GONE
        binding.noHistoryTextView.visibility = View.GONE
    }

    private fun hideShimmer() {
        binding.shimmerLayout.visibility = View.GONE
        if (viewModel.reviewMap.isEmpty()) {
            binding.recyclerView.visibility = View.GONE
            binding.noHistoryTextView.visibility = View.VISIBLE
        } else {
            binding.recyclerView.visibility = View.VISIBLE
            binding.noHistoryTextView.visibility = View.GONE
        }
    }

    private fun showReviewSaveProcess() {
        formBinding.progressBar.visibility = View.VISIBLE
        formBinding.buttonOk.visibility = View.GONE
        formBinding.buttonCancel.visibility = View.GONE
    }

    private fun hideReviewSaveProcess() {
        formBinding.progressBar.visibility = View.GONE
        formBinding.buttonOk.visibility = View.VISIBLE
        formBinding.buttonCancel.visibility = View.VISIBLE
    }
}