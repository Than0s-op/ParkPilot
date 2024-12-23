package com.application.parkpilot.fragment

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.application.parkpilot.R
import com.application.parkpilot.adapter.recycler.History
import com.application.parkpilot.adapter.recycler.SpotList
import com.application.parkpilot.databinding.SpotListBinding
import com.application.parkpilot.fragment.bottomSheet.SortFragment
import com.application.parkpilot.viewModel.SpotListViewModel

class SpotList : Fragment(R.layout.spot_list),
    SortFragment.OnRadioButtonSelectedListener, SortFragment.OnFilterSelectedListener {
    companion object {
        // Default
        private var sortingType: Int = 0
        private var filterSelection = listOf(true, true)
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
            val dialog = SortFragment.newInstance(sortingType, filterSelection)
            dialog.onRadioButtonSelectedListener = this
            dialog.onFilterSelectedListener = this
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

        binding.searchView.setOnQueryTextListener(object : OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                val filteredList = viewModel.liveDataStationList.value?.filter {
                    it.name.contains(newText ?: "", true)
                } ?: emptyList()

                binding.recyclerView.adapter = SpotList(
                    requireContext(), R.layout.spot_list_item, filteredList
                )
                return true
            }
        })
    }

    override fun onRadioButtonSelected(selectedRadioButtonId: Int) {
        sortingType = selectedRadioButtonId
    }

    private fun triggerSorting() {
        viewModel.applyFilter(filterSelection)
        when (sortingType) {
            1 -> viewModel.sortByDistance()
            2 -> viewModel.sortByRating()
        }
    }

    private fun showShimmer() {
        binding.scrollView.visibility = View.VISIBLE
        binding.recyclerView.visibility = View.GONE
        binding.noHistoryTextView.visibility = View.GONE
    }

    private fun hideShimmer() {
        binding.scrollView.visibility = View.GONE
        if (viewModel.liveDataStationList.value.isNullOrEmpty()) {
            binding.recyclerView.visibility = View.GONE
            binding.noHistoryTextView.visibility = View.VISIBLE
        } else {
            binding.recyclerView.visibility = View.VISIBLE
            binding.noHistoryTextView.visibility = View.GONE
            binding.buttonSort.visibility = View.VISIBLE
        }
    }

    override fun onFilterSelected(list: List<Boolean>) {
        filterSelection = list
        triggerSorting()
    }
}