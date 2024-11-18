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

        binding = SpotListBinding.bind(view)
        viewModel = ViewModelProvider(this)[SpotListViewModel::class.java]
        viewModel.loadStations(requireContext())
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())

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
            showShimmer()
            viewModel.loadStations(requireContext())
            binding.swipeRefreshLayout.isRefreshing = false
        }

        viewModel.liveDataStationList.observe(requireActivity()) {
            binding.recyclerView.adapter = SpotList(
                requireContext(), R.layout.spot_list_item, it
            )
            hideShimmer()
        }
    }

    override fun onRadioButtonSelected(selectedRadioButtonId: Int) {
        sortingType = selectedRadioButtonId
        triggerSorting()
    }

    private fun triggerSorting() {
        when (sortingType) {
            1 -> viewModel.sortByDistance()
            2 -> viewModel.sortByRating()
        }
    }

    private fun showShimmer() {
        binding.shimmerLayout.visibility = View.VISIBLE
        binding.recyclerView.visibility = View.GONE
    }

    private fun hideShimmer() {
        binding.shimmerLayout.visibility = View.GONE
        binding.recyclerView.visibility = View.VISIBLE
    }
}