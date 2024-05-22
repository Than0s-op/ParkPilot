package com.application.parkpilot.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.application.parkpilot.R
import com.application.parkpilot.adapter.recycler.SpotList
import com.application.parkpilot.databinding.SpotListBinding
import com.application.parkpilot.fragment.bottomSheet.SortFragment
import com.application.parkpilot.viewModel.SpotListViewModel

class SpotList : Fragment(R.layout.spot_list),
    SortFragment.OnRadioButtonSelectedListener {
    companion object {
        // Default
        private var sortingType: Int = 0
    }

    private lateinit var viewModel: SpotListViewModel
    private lateinit var binding: SpotListBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[SpotListViewModel::class.java]
        viewModel.loadStation()
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        viewModel.permission.gpsPermissionRequest(requireContext())

        binding.buttonSort.setOnClickListener {
            val dialog = SortFragment.newInstance(sortingType)
            dialog.onRadioButtonSelectedListener = this
            dialog.show(childFragmentManager, SortFragment.TAG)
        }

        binding.recyclerView.setOnScrollChangeListener { _, _, scrollY, _, oldScrollY ->
            // the delay of the extension of the FAB is set for 12 items
            if (scrollY > oldScrollY + 12 && binding.buttonSort.isShown) {
                binding.buttonSort.hide()
            }

            // the delay of the extension of the FAB is set for 12 items
            if (scrollY < oldScrollY - 12 && !binding.buttonSort.isShown) {
                binding.buttonSort.show()
            }
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.loadStation()
            viewModel.permission.gpsPermissionRequest(requireContext())
            binding.swipeRefreshLayout.isRefreshing = false
        }

        viewModel.liveDataStationLocation.observe(requireActivity()) {
            it?.let {
                binding.recyclerView.adapter = SpotList(
                    requireContext(), R.layout.spot_list_item, it
                )
            }
        }
    }

    override fun onRadioButtonSelected(selectedRadioButtonId: Int) {
        sortingType = selectedRadioButtonId
        triggerSorting()
    }

    private fun triggerSorting() {
        viewModel.liveDataStationLocation.value?.let { stations ->
            when (sortingType) {
                1 -> viewModel.sortByDistance(requireContext(), stations)
                2 -> viewModel.sortByRating(stations)
            }
        }
    }
}