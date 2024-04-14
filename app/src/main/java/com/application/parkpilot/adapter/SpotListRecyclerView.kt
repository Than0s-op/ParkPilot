package com.application.parkpilot.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isEmpty
import androidx.core.view.isNotEmpty
import androidx.recyclerview.widget.RecyclerView
import com.application.parkpilot.R
import com.application.parkpilot.StationLocation
import com.application.parkpilot.activity.SpotDetail
import com.application.parkpilot.module.firebase.Storage
import com.application.parkpilot.module.firebase.database.Feedback
import com.application.parkpilot.module.firebase.database.StationAdvance
import com.application.parkpilot.module.firebase.database.StationBasic
import com.application.parkpilot.view.Amenities
import com.application.parkpilot.viewModel.adapter.SpotList
import com.google.android.flexbox.FlexboxLayout
import com.google.android.material.card.MaterialCardView
import com.google.android.material.carousel.CarouselLayoutManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SpotListRecyclerView(
    private val context: Context,
    private val layout: Int,
    private val stations: List<StationLocation>
) : RecyclerView.Adapter<SpotListRecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val stationUID = stations[position].stationUid!!
        holder.viewModel.loadBasic(stationUID)
        holder.viewModel.loadAmenities(stationUID)
        holder.viewModel.loadRating(stationUID)
        holder.viewModel.loadImages(stationUID)

        val activity = context as AppCompatActivity
        holder.viewModel.liveDataStationBasic.observe(activity) {
            holder.textViewName.text = it.name
            holder.textViewPrice.text = it.price.toString()
        }
        holder.viewModel.liveDataImages.observe(activity) {
            holder.recyclerView.adapter = CarouselRecyclerView(
                context,
                R.layout.round_carousel,
                it
            )
        }
        holder.viewModel.liveDataFeedback.observe(activity) {
            if (it.second != 0) {
                val rating = it.first / it.second
                holder.textViewRating.text = String.format("%.1f", rating)
                holder.textViewNumberOfUser.text = it.second.toString()
                holder.textViewRating.backgroundTintList = holder.viewModel.getTint(rating)
            }
            else{
                holder.textViewRating.text = "0.0"
                holder.textViewNumberOfUser.text = "0"
                holder.textViewRating.backgroundTintList = holder.viewModel.getTint(0.0f)
            }
        }
        holder.viewModel.liveDataAmenities.observe(activity) {
            if(holder.flexboxLayout.isEmpty()) {
                for (i in it) {
                    holder.flexboxLayout.addView(Amenities(context, i).textView)
                }
            }
        }
        holder.materialCard.setOnClickListener {
            holder.viewModel.startNextActivity(context, stationUID)
        }
        holder.textViewDistance.text = "N/A"
    }

    override fun getItemCount(): Int {
        return stations.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val viewModel = SpotList()
        val materialCard: MaterialCardView = itemView.findViewById(R.id.materialCardView)
        val textViewName: TextView = itemView.findViewById(R.id.textViewName)
        val textViewRating: TextView = itemView.findViewById(R.id.textViewRating)
        val textViewNumberOfUser: TextView = itemView.findViewById(R.id.textViewNumberOfUser)
        val textViewDistance: TextView = itemView.findViewById(R.id.textViewDistance)
        val textViewPrice: TextView = itemView.findViewById(R.id.textViewPrice)
        val flexboxLayout: FlexboxLayout = itemView.findViewById(R.id.flexboxLayout)
        val recyclerView: RecyclerView =
            itemView.findViewById<RecyclerView>(R.id.recycleView).apply {
                layoutManager = CarouselLayoutManager()
            }
    }
}