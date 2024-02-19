package com.application.parkpilot.bottomSheet

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.application.parkpilot.R
import com.application.parkpilot.activity.SpotDetailActivity
import com.application.parkpilot.adapter.CarouselRecyclerView
import com.application.parkpilot.viewModel.SpotPreviewViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.carousel.CarouselLayoutManager

class SpotPreview : BottomSheetDialogFragment(R.layout.spot_preview) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView: RecyclerView = view.findViewById(R.id.recycleView)
        val textViewName: TextView = view.findViewById(R.id.textViewName)
        val textViewRating: TextView = view.findViewById(R.id.textViewRating)
        val textViewDistance: TextView = view.findViewById(R.id.textViewDistance)
        val textViewPrice: TextView = view.findViewById(R.id.textViewPrice)
        val buttonDetail: Button = view.findViewById(R.id.buttonDetail)

        val stationUID = tag!!

        val viewModel = ViewModelProvider(this)[SpotPreviewViewModel::class.java]

        recyclerView.layoutManager = CarouselLayoutManager()
        viewModel.loadCarousel()
        viewModel.loadInfo(stationUID)


        buttonDetail.setOnClickListener {
            val intent = Intent(context, SpotDetailActivity::class.java)
            startActivity(intent)
        }

        viewModel.carouselImages.observe(this) { images ->
            recyclerView.adapter =
                CarouselRecyclerView(requireContext(), R.layout.round_carousel, images)
        }

        viewModel.stationBasicInfo.observe(this){
            textViewName.text = it.name
            textViewRating.text = it.rating.toString()
            textViewPrice.text = it.price.toString()
            textViewDistance.text = "N/A"
        }
    }
}