package com.application.parkpilot.activity

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RatingBar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
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
        val buttonFeedback: FloatingActionButton = findViewById(R.id.buttonFeedback)

        val stationUID = intent.getStringExtra("stationUID")!!
        val viewModel = FeedbackViewModel()
        val layoutFeedbackForm = layoutInflater.inflate(R.layout.feedback_form, null, false).apply {
            val imageViewProfilePicture: ImageView = findViewById(R.id.imageViewProfilePicture)!!

            imageViewProfilePicture.load(viewModel.profilePicture())
            viewModel.loadProfileImage(imageViewProfilePicture)
        }

        val dialogInflater =
            MaterialAlertDialogBuilder(this).setView(layoutFeedbackForm).create()

        val buttonOk:Button = layoutFeedbackForm.findViewById(R.id.buttonOk)
        val buttonCancel: Button = layoutFeedbackForm.findViewById(R.id.buttonCancel)
        val ratingBar:RatingBar = layoutFeedbackForm.findViewById(R.id.ratingBar)
        val editTextFeedback: EditText = layoutFeedbackForm.findViewById(R.id.editTextFeedback)

        buttonOk.setOnClickListener {
            viewModel.setFeedback(stationUID,Feedback(User.UID,ratingBar.rating,editTextFeedback.text.toString()))
            dialogInflater.dismiss()
        }
        buttonCancel.setOnClickListener {
            dialogInflater.dismiss()
        }

        viewModel.loadRecycler(this,stationUID, recyclerView)

        buttonFeedback.setOnClickListener {
            viewModel.loadFeedback(stationUID,ratingBar,editTextFeedback)
            dialogInflater.show()
        }
    }
}