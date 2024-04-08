package com.application.parkpilot.adapter

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.application.parkpilot.R
import com.application.parkpilot.StationLocation
import com.application.parkpilot.activity.SpotDetailActivity
import com.application.parkpilot.module.firebase.Storage
import com.application.parkpilot.module.firebase.database.Feedback
import com.application.parkpilot.module.firebase.database.StationBasic
import com.google.android.material.carousel.CarouselLayoutManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SpotListRecyclerView(
    private val context: Context,
    private val layout: Int,
    private val stations: List<StationLocation>
) : RecyclerView.Adapter<SpotListRecyclerView.ViewHolder>() {

    val storage = Storage()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        CoroutineScope(Dispatchers.Main).launch {
            val stationUID = stations[position].stationUid!!
            val stationBasic = StationBasic().basicGet(stationUID)
            stationBasic?.let {
                holder.textViewName.text = stationBasic.name
                holder.textViewDistance.text = "N/A"
                holder.textViewPrice.text = stationBasic.price?.toString()
                holder.buttonDetail.setOnClickListener {
                    val intent = Intent(context, SpotDetailActivity::class.java).apply {
                        putExtra("stationUID", stationUID)
                    }
                    context.startActivity(intent)
                }
                holder.recyclerView.adapter =
                    CarouselRecyclerView(
                        context,
                        R.layout.round_carousel,
                        storage.parkSpotPhotoGet(stationUID)
                    )
            }
            // ratting
            val feedbacks = Feedback().feedGet(stationUID)
            var totalRatting = 0.0f
            for (i in feedbacks) {
                totalRatting += i.value.rating
            }
            if (feedbacks.isNotEmpty()) {
                val ratting = totalRatting / feedbacks.size
                holder.textViewRating.text = String.format("%.1f", ratting)
                holder.textViewRating.backgroundTintList = getTint(ratting)
            } else holder.textViewRating.text = "N/A"
        }
    }

    private fun getTint(ratting: Float): ColorStateList {
        return if (ratting <= 2.5)
            ColorStateList.valueOf(Color.parseColor("#ff4545"))
        else if (ratting < 4)
            ColorStateList.valueOf(Color.parseColor("#ffe234"))
        else
            ColorStateList.valueOf(Color.parseColor("#57e32c"))
    }

    override fun getItemCount(): Int {
        return stations.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val recyclerView: RecyclerView =
            itemView.findViewById<RecyclerView>(R.id.recycleView).apply {
                layoutManager = CarouselLayoutManager()
            }
        val textViewName: TextView = itemView.findViewById(R.id.textViewName)
        val textViewRating: TextView = itemView.findViewById(R.id.textViewRating)
        val textViewDistance: TextView = itemView.findViewById(R.id.textViewDistance)
        val textViewPrice: TextView = itemView.findViewById(R.id.textViewPrice)
        val buttonDetail: Button = itemView.findViewById(R.id.buttonDetail)

    }

}