package com.application.parkpilot.viewModel

import android.content.Context
import android.net.Uri
import android.widget.EditText
import android.widget.ImageView
import android.widget.RatingBar
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.application.parkpilot.Feedback as DC_Feedback
import com.application.parkpilot.R
import com.application.parkpilot.User
import com.application.parkpilot.adapter.recycler.Feedback as RV_Feedback
import com.application.parkpilot.module.firebase.Storage
import com.application.parkpilot.module.firebase.database.Feedback as FS_Feedback
import com.application.parkpilot.module.firebase.database.UserBasic
import kotlinx.coroutines.launch

class FeedbackViewModel : ViewModel() {
    fun loadRecycler(context: Context, stationUid: String, recyclerView: RecyclerView) {
        val feedback = FS_Feedback()
        viewModelScope.launch {
            val list = feedback.feedGet(stationUid)
            val layoutManger = LinearLayoutManager(context)
            recyclerView.layoutManager = layoutManger
            recyclerView.adapter =
                com.application.parkpilot.adapter.recycler.Feedback(
                    context,
                    R.layout.feedback_item,
                    list.toList()
                )
            recyclerView.addItemDecoration(DividerItemDecoration(context, layoutManger.orientation))
        }
    }

    fun setFeedback(stationUid: String, feedback: com.application.parkpilot.Feedback) {
        val fireStoreFeedback = FS_Feedback()
        viewModelScope.launch {
            // required parking spot UID
            fireStoreFeedback.feedSet(feedback, stationUid)
        }
    }

    fun profilePicture(): Uri? {
        val userBasic = UserBasic()
        val profilePicture: Uri? = null
        viewModelScope.launch {
            userBasic.getProfile(User.UID)
        }
        return profilePicture
    }

    fun loadFeedback(stationUID: String, ratingBar: RatingBar, editTextFeedback: EditText) {
        viewModelScope.launch {
            val feedback = FS_Feedback().feedGet(stationUID)[User.UID]
            feedback?.rating?.let {
                ratingBar.rating = it
            }
            feedback?.message?.let {
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