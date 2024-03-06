package com.application.parkpilot.activity

import android.os.Bundle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.application.parkpilot.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class Feedback: AppCompatActivity(R.layout.feedback) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        val buttonFeedback: FloatingActionButton = findViewById(R.id.buttonFeedback)

        buttonFeedback.setOnClickListener{
            MaterialAlertDialogBuilder(this).setView(R.layout.feedback_form)
        }
    }
}