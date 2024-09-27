package com.application.parkpilot.fragment.bottomSheet

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.application.parkpilot.R
import com.application.parkpilot.activity.SpotDetail
import com.application.parkpilot.adapter.recycler.Carousel
import com.application.parkpilot.databinding.SpotListItemBinding
import com.application.parkpilot.viewModel.SpotPreviewViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.carousel.CarouselLayoutManager

class SpotPreview : BottomSheetDialogFragment(R.layout.spot_list_item) {
    private lateinit var binding: SpotListItemBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = SpotListItemBinding.bind(view)

        val stationUID = tag!!

        val viewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return SpotPreviewViewModel(requireContext()) as T
            }
        })[SpotPreviewViewModel::class.java]

        binding.recyclerView.layoutManager = CarouselLayoutManager()
        viewModel.loadCarousel(stationUID)
        viewModel.loadBasicInfo(stationUID)
        viewModel.loadRating(stationUID)

        binding.materialCardView.setOnClickListener {
            val intent = Intent(context, SpotDetail::class.java).apply {
                putExtra("stationUID", stationUID)
            }
            startActivity(intent)
        }

        viewModel.carouselImages.observe(this) { images ->
            binding.recyclerView.adapter =
                Carousel(requireContext(), R.layout.round_carousel, images)
        }

        viewModel.stationBasicInfo.observe(this) {
            binding.textViewName.text = it.name
            binding.textViewPrice.text = it.price.toString()
        }
        viewModel.stationRating.observe(this) {
            binding.textViewRating.text = String.format("%.1f", it.first / it.second)
            binding.textViewRating.backgroundTintList = getTint(it.first / it.second)
            binding.textViewNumberOfUser.text = it.second.toString()
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