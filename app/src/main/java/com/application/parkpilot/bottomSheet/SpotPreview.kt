package com.application.parkpilot.bottomSheet

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.application.parkpilot.R
import com.application.parkpilot.activity.SpotDetail
import com.application.parkpilot.adapter.CarouselRecyclerView
import com.application.parkpilot.viewModel.SpotPreviewViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.card.MaterialCardView
import com.google.android.material.carousel.CarouselLayoutManager

class SpotPreview : BottomSheetDialogFragment(R.layout.spot_list_item) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val materialCardView: MaterialCardView = view.findViewById(R.id.materialCardView)
        val recyclerView: RecyclerView = view.findViewById(R.id.recycleView)
        val textViewName: TextView = view.findViewById(R.id.textViewName)
        val textViewRating: TextView = view.findViewById(R.id.textViewRating)
        val textViewNumberOfUser: TextView = view.findViewById(R.id.textViewNumberOfUser)
        val textViewDistance: TextView = view.findViewById(R.id.textViewDistance)
        val textViewPrice: TextView = view.findViewById(R.id.textViewPrice)

        val stationUID = tag!!

        val viewModel = ViewModelProvider(this)[SpotPreviewViewModel::class.java]

        recyclerView.layoutManager = CarouselLayoutManager()
        viewModel.loadCarousel(stationUID)
        viewModel.loadBasicInfo(stationUID)
        viewModel.loadRating(stationUID)

        materialCardView.setOnClickListener {
            val intent = Intent(context, SpotDetail::class.java).apply {
                putExtra("stationUID", stationUID)
            }
            startActivity(intent)
        }

        viewModel.carouselImages.observe(this) { images ->
            recyclerView.adapter =
                CarouselRecyclerView(requireContext(), R.layout.round_carousel, images)
        }

        viewModel.stationBasicInfo.observe(this) {
            textViewName.text = it.name
            textViewPrice.text = it.price.toString()
        }
        viewModel.stationRating.observe(this) {
            textViewRating.text = String.format("%.1f", it.first / it.second)
            textViewRating.backgroundTintList = getTint(it.first / it.second)
            textViewNumberOfUser.text = it.second.toString()
        }
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
}