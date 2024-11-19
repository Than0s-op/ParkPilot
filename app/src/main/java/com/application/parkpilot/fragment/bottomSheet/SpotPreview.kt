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
import com.application.parkpilot.view.Amenities
import com.application.parkpilot.viewModel.SpotPreviewViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.carousel.CarouselLayoutManager

class SpotPreview(private val isFreeSpot: Boolean) :
    BottomSheetDialogFragment(R.layout.spot_list_item) {
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

        loadView()
        viewModel.stationUID = tag!!

        binding.recyclerView.layoutManager = CarouselLayoutManager()

        if (!isFreeSpot) {
            showShimmer()
            viewModel.loadDetailScreen {
                hideShimmer()
            }
        } else {
            showShimmer()
            viewModel.getFreeSpotDetails()
        }

        binding.materialCardView.setOnClickListener {
            val intent = Intent(context, SpotDetail::class.java).apply {
                putExtra("stationUID", stationUID)
                putExtra("isFree", isFreeSpot)
            }
            startActivity(intent)
        }

        viewModel.carouselImages.observe(this) { images ->
            binding.recyclerView.adapter =
                Carousel(requireContext(), R.layout.round_carousel, images)
        }

        viewModel.stationAdvanceInfo.observe(this) {
            binding.flexboxLayout.removeAllViews()
            for (i in it.amenities) {
                binding.flexboxLayout.addView(Amenities(requireContext(), i).textView)
            }
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
        viewModel.freeSpotInfo.observe(this) {
            binding.textViewName.text = it.landMark
            binding.recyclerView.adapter =
                Carousel(requireContext(), R.layout.round_carousel, it.images)
            viewModel.getDistance(requireContext())
            hideShimmer()
        }
        viewModel.liveDataDistance.observe(this) {
            binding.textViewDistance.text = it
        }
    }

    private fun showShimmer() {
        binding.linearLayout.visibility = View.GONE
        binding.shimmerLayout.shimmerLayout.visibility = View.VISIBLE
    }

    private fun hideShimmer() {
        binding.shimmerLayout.shimmerLayout.visibility = View.GONE
        binding.linearLayout.visibility = View.VISIBLE
    }

    private fun loadView() {
        if (isFreeSpot) {
            binding.linearLayout2.visibility = View.GONE
            binding.priceLayout.visibility = View.GONE
            binding.freeImageView.visibility = View.VISIBLE
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