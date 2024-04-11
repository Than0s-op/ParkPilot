package com.application.parkpilot.activity

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import coil.load
import com.application.parkpilot.Feedback
import com.application.parkpilot.R
import com.application.parkpilot.User
import com.application.parkpilot.viewModel.FeedbackViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class Feedback : AppCompatActivity(R.layout.feedback) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        val buttonEditFeedback: Button = findViewById(R.id.buttonEditFeedback)
        val swipeRefreshLayout: SwipeRefreshLayout = findViewById(R.id.swipeRefreshLayout)

        val stationUID = intent.getStringExtra("stationUID")!!
        val viewModel = FeedbackViewModel()
        val layoutFeedbackForm = layoutInflater.inflate(R.layout.feedback_form, null, false).apply {
            val imageViewProfilePicture: ImageView = findViewById(R.id.imageViewProfilePicture)!!
            imageViewProfilePicture.load(viewModel.profilePicture())
            viewModel.loadProfileImage(imageViewProfilePicture)
        }

        val dialogInflater =
            MaterialAlertDialogBuilder(this).setView(layoutFeedbackForm).create()

        val buttonOk: Button = layoutFeedbackForm.findViewById(R.id.buttonOk)
        val buttonCancel: Button = layoutFeedbackForm.findViewById(R.id.buttonCancel)
        val ratingBar: RatingBar = layoutFeedbackForm.findViewById(R.id.ratingBar)
        val editTextFeedback: EditText = layoutFeedbackForm.findViewById(R.id.editTextFeedback)

        buttonOk.setOnClickListener {
            if (editTextFeedback.text.isBlank()) {
                Toast.makeText(this, "Message must not be empty", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            viewModel.setFeedback(
                stationUID,
                Feedback(User.UID, ratingBar.rating, editTextFeedback.text.toString())
            )
            dialogInflater.dismiss()
        }

        buttonCancel.setOnClickListener {
            dialogInflater.dismiss()
        }

        ratingBar.setOnRatingBarChangeListener { _, rating, _ ->
            if (rating < 1) ratingBar.rating = 1.0f
        }

        buttonEditFeedback.setOnClickListener {
            viewModel.loadFeedback(stationUID, ratingBar, editTextFeedback)
            dialogInflater.show()
        }

        swipeRefreshLayout.setOnRefreshListener {
            viewModel.loadRecycler(this, stationUID, recyclerView)
            swipeRefreshLayout.isRefreshing = false
        }

        viewModel.loadRecycler(this, stationUID, recyclerView)
    }
}