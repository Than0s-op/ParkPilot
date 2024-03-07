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
            val ratingBar:RatingBar = findViewById(R.id.ratingBar)
            val editTextFeedback: EditText = findViewById(R.id.editTextFeedback)
            val buttonCancel: Button = findViewById(R.id.buttonCancel)
            val buttonOk:Button = findViewById(R.id.buttonOk)

            imageViewProfilePicture.load(viewModel.profilePicture())
            buttonOk.setOnClickListener {
                viewModel.setFeedback(stationUID,Feedback(User.UID,ratingBar.rating,editTextFeedback.text.toString()))
            }
            buttonCancel.setOnClickListener {

            }
        }

        val dialogInflater =
            MaterialAlertDialogBuilder(this).setView(layoutFeedbackForm).create()

        viewModel.loadRecycler(this,stationUID, recyclerView)

        buttonFeedback.setOnClickListener {
            dialogInflater.show()
        }
    }
}