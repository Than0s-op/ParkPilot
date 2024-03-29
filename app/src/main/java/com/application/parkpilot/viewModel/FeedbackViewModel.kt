package com.application.parkpilot.viewModel

import android.content.Context
import android.net.Uri
import android.widget.EditText
import android.widget.ImageView
import android.widget.RatingBar
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.application.parkpilot.R
import com.application.parkpilot.User
import com.application.parkpilot.adapter.FeedbackRecyclerView
import com.application.parkpilot.module.firebase.Storage
import com.application.parkpilot.module.firebase.database.Feedback
import com.application.parkpilot.Feedback as FeedbackData
import com.application.parkpilot.module.firebase.database.UserBasic
import kotlinx.coroutines.launch

class FeedbackViewModel: ViewModel() {
    fun loadRecycler(context: Context,stationUid:String, recyclerView: RecyclerView) {
        val feedback = Feedback()
        viewModelScope.launch {
            val list = feedback.feedGet(stationUid)
            recyclerView.layoutManager = LinearLayoutManager(context)
            recyclerView.adapter = FeedbackRecyclerView(context, R.layout.feedback_item, list.toList())
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

    fun loadFeedback(stationUID: String, ratingBar: RatingBar, editTextFeedback: EditText) {
        viewModelScope.launch {
            val feedback = Feedback().feedGet(stationUID)[User.UID]
            feedback?.rating?.let{
                ratingBar.rating = it
            }
            feedback?.message?.let{
                editTextFeedback.setText(it)
            }
        }
    }

    fun loadProfileImage(imageViewProfilePicture: ImageView) {
        viewModelScope.launch {
            imageViewProfilePicture.load(Storage().userProfilePhotoGet(User.UID))
        }
    }
}