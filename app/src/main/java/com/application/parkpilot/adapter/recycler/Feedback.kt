package com.application.parkpilot.adapter.recycler

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.application.parkpilot.Feedback
import com.application.parkpilot.R
import com.application.parkpilot.module.firebase.Storage
import com.application.parkpilot.module.firebase.database.UserBasic
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class Feedback(
    private val context: Context, private val layout: Int, private val feedback: List<Pair<String, Feedback>>
) : RecyclerView.Adapter<com.application.parkpilot.adapter.recycler.Feedback.ViewHolder>() {
    private val userBasic = UserBasic()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        CoroutineScope(Dispatchers.Main).launch {
            val userProfile = userBasic.getProfile(feedback[position].second.UID)
            userProfile?.let {
                holder.imageViewUserPicture.load(Storage().userProfilePhotoGet(feedback[position].second.UID))
                holder.textViewUserName.text = userProfile.userName
            }
        }
        holder.ratingBar.rating = feedback[position].second.rating
        holder.textViewMessage.text = feedback[position].second.message
    }

    override fun getItemCount(): Int {
        return feedback.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageViewUserPicture: ImageView = itemView.findViewById(R.id.imageViewUserPicture)
        val textViewUserName:TextView = itemView.findViewById(R.id.textViewUserName)
        val ratingBar: RatingBar = itemView.findViewById(R.id.ratingBar)
        val textViewMessage:TextView = itemView.findViewById(R.id.textViewMessage)
    }

}