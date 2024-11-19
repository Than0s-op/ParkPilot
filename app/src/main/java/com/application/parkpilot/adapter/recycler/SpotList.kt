package com.application.parkpilot.adapter.recycler

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.application.parkpilot.R
import com.application.parkpilot.SpotListCardDetails
import com.application.parkpilot.activity.SpotDetail
import com.application.parkpilot.view.Amenities
import com.google.android.flexbox.FlexboxLayout
import com.google.android.material.card.MaterialCardView
import com.google.android.material.carousel.CarouselLayoutManager
import kotlin.math.roundToInt

class SpotList(
    private val context: Context,
    private val layout: Int,
    private val stations: List<SpotListCardDetails>
) : RecyclerView.Adapter<SpotList.ViewHolder>() {

    // not recommended in model view view model pattern
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val spot = stations[position]

        if (spot.isFree) {
            setFreeSpotVisibility(holder)
        } else {
            setVisibility(holder)
        }

        holder.recyclerView.adapter = Carousel(
            context,
            R.layout.round_carousel,
            spot.images
        )

        holder.textViewName.text = spot.name

        spot.price?.let {
            holder.textViewPrice.text = it.toString()
        }

        spot.distance?.let {
            holder.textViewDistance.text = formatDistance(it)
        }

        val numberOfUser = spot.rating?.second ?: 0
        val totalRating = spot.rating?.first ?: 0f
        var rating = 0f
        if (numberOfUser != 0) {
            rating = totalRating / numberOfUser
        }
        holder.textViewRating.text = String.format("%.1f", rating)
        holder.textViewNumberOfUser.text = numberOfUser.toString()
        holder.textViewRating.backgroundTintList = getTint(rating)

        holder.flexboxLayout.removeAllViews()
        for (i in spot.amenities ?: emptyList()) {
            holder.flexboxLayout.addView(Amenities(context, i).textView)
        }

        holder.materialCard.setOnClickListener {
            startNextActivity(
                context = context,
                stationUID = spot.documentId,
                isFree = spot.isFree
            )
        }
    }

    private fun startNextActivity(context: Context, stationUID: String, isFree: Boolean = false) {
        val intent = Intent(context, SpotDetail::class.java).apply {
            putExtra("stationUID", stationUID)
            putExtra("isFree", isFree)
        }
        context.startActivity(intent)
    }


    private fun getTint(ratting: Float): ColorStateList {
        return if (ratting <= 2.5) {
            ColorStateList.valueOf(Color.parseColor("#e5391a"))
        } else if (ratting < 4) {
            ColorStateList.valueOf(Color.parseColor("#cb8300"))
        } else {
            ColorStateList.valueOf(Color.parseColor("#026a28"))
        }
    }

    private fun setFreeSpotVisibility(holder: ViewHolder) {
        holder.textViewRating.visibility = View.GONE
        holder.textViewPrice.visibility = View.GONE
        holder.flexboxLayout.visibility = View.GONE
        holder.textViewNumberOfUser.visibility = View.GONE
        holder.textViewFrom.visibility = View.GONE
        holder.imageViewFree.visibility = View.VISIBLE
        holder.linearLayoutRating.visibility = View.GONE
    }

    private fun setVisibility(holder: ViewHolder) {
        holder.textViewRating.visibility = View.VISIBLE
        holder.textViewPrice.visibility = View.VISIBLE
        holder.flexboxLayout.visibility = View.VISIBLE
        holder.textViewNumberOfUser.visibility = View.VISIBLE
        holder.textViewFrom.visibility = View.VISIBLE
        holder.imageViewFree.visibility = View.GONE
        holder.linearLayoutRating.visibility = View.VISIBLE
    }

    private fun formatDistance(distanceKm: Double, precision: Int = 1): String {
        return when {
            distanceKm < 1 -> "${(distanceKm * 1000).roundToInt()}m"
            else -> "%.${precision}f km".format(distanceKm)
        }
    }

    override fun getItemCount(): Int {
        return stations.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val materialCard: MaterialCardView = itemView.findViewById(R.id.materialCardView)
        val textViewName: TextView = itemView.findViewById(R.id.textViewName)
        val textViewRating: TextView = itemView.findViewById(R.id.textViewRating)
        val textViewNumberOfUser: TextView = itemView.findViewById(R.id.textViewNumberOfUser)
        val textViewFrom: TextView = itemView.findViewById(R.id.textView2)
        val textViewDistance: TextView = itemView.findViewById(R.id.textViewDistance)
        val textViewPrice: TextView = itemView.findViewById(R.id.textViewPrice)
        val linearLayoutRating: LinearLayout = itemView.findViewById(R.id.linearLayout2)
        val imageViewFree: ImageView = itemView.findViewById(R.id.freeImageView)
        val flexboxLayout: FlexboxLayout = itemView.findViewById(R.id.flexboxLayout)
        val recyclerView: RecyclerView =
            itemView.findViewById<RecyclerView>(R.id.recyclerView).apply {
                layoutManager = CarouselLayoutManager()
            }
    }
}