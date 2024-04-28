package com.application.parkpilot.adapter.recycler

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isEmpty
import androidx.recyclerview.widget.RecyclerView
import com.application.parkpilot.R
import com.application.parkpilot.StationLocation
import com.application.parkpilot.module.PermissionRequest
import com.application.parkpilot.view.Amenities
import com.application.parkpilot.viewModel.adapter.SpotList
import com.google.android.flexbox.FlexboxLayout
import com.google.android.gms.location.LocationServices
import com.google.android.material.card.MaterialCardView
import com.google.android.material.carousel.CarouselLayoutManager

class SpotList(
    private val context: Context,
    private val layout: Int,
    private val stations: List<StationLocation>
) : RecyclerView.Adapter<com.application.parkpilot.adapter.recycler.SpotList.ViewHolder>() {

    // not recommended in model view view model pattern
    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    private val permissionRequest = PermissionRequest()
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
        if (permissionRequest.hasLocationPermission(context)) {
            holder.viewModel.getDistance(fusedLocationClient, stations[position].coordinates)
        }

        val activity = context as AppCompatActivity
        holder.viewModel.liveDataStationBasic.observe(activity) {
            holder.textViewName.text = it.name
            holder.textViewPrice.text = it.price.toString()
        }
        holder.viewModel.liveDataImages.observe(activity) {
            holder.recyclerView.adapter = Carousel(
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
            } else {
                holder.textViewRating.text = "0.0"
                holder.textViewNumberOfUser.text = "0"
                holder.textViewRating.backgroundTintList = holder.viewModel.getTint(0.0f)
            }
        }
        holder.viewModel.liveDataAmenities.observe(activity) {
            if (holder.flexboxLayout.isEmpty()) {
                for (i in it) {
                    holder.flexboxLayout.addView(Amenities(context, i).textView)
                }
            }
        }
        holder.viewModel.liveDataDistance.observe(activity) {
            holder.textViewDistance.text = it.toString()
        }
        holder.materialCard.setOnClickListener {
            holder.viewModel.startNextActivity(context, stationUID)
        }
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