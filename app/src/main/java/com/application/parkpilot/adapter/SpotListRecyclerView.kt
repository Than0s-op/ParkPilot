package com.application.parkpilot.adapter

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.application.parkpilot.R
import com.application.parkpilot.StationLocation
import com.application.parkpilot.activity.SpotDetailActivity
import com.application.parkpilot.module.PhotoLoader
import com.application.parkpilot.module.firebase.Storage
import com.application.parkpilot.module.firebase.database.StationBasic
import com.application.parkpilot.module.firebase.database.UserBasic
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
        CoroutineScope(Dispatchers.Main).launch {
            val stationUID = stations[position].stationUid!!
            val stationBasic = StationBasic().basicGet(stationUID)
            stationBasic?.let {
                holder.textViewName.text = stationBasic.name
                holder.textViewRating.text = "0.0"
                holder.textViewDistance.text = "N/A"
                holder.textViewPrice.text = stationBasic.price?.toString()
                holder.buttonDetail.setOnClickListener {
                    val intent = Intent(context, SpotDetailActivity::class.java).apply {
                        putExtra("stationUID", stationUID)
                    }
                    context.startActivity(intent)
                }
                holder.recyclerView.layoutManager = CarouselLayoutManager()
                val imageList = Storage().parkSpotPhotoGet(stationUID).let { uriList ->
                    val drawableList = ArrayList<Drawable>()
                    val photoLoader = PhotoLoader()
                    for (uri in uriList) {
                        drawableList.add(
                            photoLoader.getImage(
                                context,
                                uri,
                                isCircleCrop = false
                            ).drawable!!
                        )
                    }
                    drawableList
                }
                holder.recyclerView.adapter =
                    CarouselRecyclerView(context, R.layout.round_carousel, imageList)
            }
        }
    }

    override fun getItemCount(): Int {
        return stations.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val recyclerView: RecyclerView = itemView.findViewById(R.id.recycleView)
        val textViewName: TextView = itemView.findViewById(R.id.textViewName)
        val textViewRating: TextView = itemView.findViewById(R.id.textViewRating)
        val textViewDistance: TextView = itemView.findViewById(R.id.textViewDistance)
        val textViewPrice: TextView = itemView.findViewById(R.id.textViewPrice)
        val buttonDetail: Button = itemView.findViewById(R.id.buttonDetail)
    }

}