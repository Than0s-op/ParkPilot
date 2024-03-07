package com.application.parkpilot.viewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.application.parkpilot.R
import com.application.parkpilot.User
import com.application.parkpilot.adapter.FeedbackRecyclerView
import com.application.parkpilot.adapter.HistoryRecyclerView
import com.application.parkpilot.module.firebase.database.Feedback
import com.application.parkpilot.module.firebase.database.QRCode
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch

class FeedbackViewModel: ViewModel() {
    fun loadRecycler(context: Context, recyclerView: RecyclerView) {
        val feedback = Feedback()
        viewModelScope.launch {
            val list = feedback.feedGet()
            recyclerView.layoutManager = LinearLayoutManager(context)
            recyclerView.adapter = FeedbackRecyclerView(context, R.layout.feedback, list)
        }
    }
}