package com.application.parkpilot.viewModel

import android.content.Context
import android.net.Uri
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
import com.application.parkpilot.Feedback as FeedbackData
import com.application.parkpilot.module.firebase.database.UserBasic
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch

class FeedbackViewModel: ViewModel() {
    fun loadRecycler(context: Context,stationUid:String, recyclerView: RecyclerView) {
        val feedback = Feedback()
        viewModelScope.launch {
            val list = feedback.feedGet(stationUid)
            recyclerView.layoutManager = LinearLayoutManager(context)
            recyclerView.adapter = FeedbackRecyclerView(context, R.layout.feedback, list)
        }
    }

    fun setFeedback(stationUid:String,feedback:FeedbackData){
        val fireStoreFeedback = Feedback()
        viewModelScope.launch{
            // required parking spot UID
            fireStoreFeedback.feedSet(feedback,stationUid)
        }
    }

    fun profilePicture(): Uri? {
        val userBasic = UserBasic()
        val profilePicture:Uri? = null
        viewModelScope.launch {
            userBasic.getProfile(User.UID)
        }
        return profilePicture
    }

    fun loadFeedback(){

    }
}