package com.application.parkpilot.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.application.parkpilot.R
import com.application.parkpilot.adapter.SpotListRecyclerView
import com.application.parkpilot.bottomSheet.SortFragment
import com.application.parkpilot.viewModel.SpotListViewModel
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton

class SpotList : Fragment(R.layout.spot_list),
    SortFragment.OnRadioButtonSelectedListener {
    companion object {
        // Default
        private var sortingType: Int = 0
    }

    private lateinit var viewModel: SpotListViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerView)
        val swipeRefreshLayout: SwipeRefreshLayout =
            view.findViewById(R.id.swipeRefreshLayout)
        val buttonSort: ExtendedFloatingActionButton = view.findViewById(R.id.buttonSort)


        viewModel = ViewModelProvider(this)[SpotListViewModel::class.java]
        viewModel.loadStation()
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        viewModel.permission.gpsPermissionRequest(requireContext())

        buttonSort.setOnClickListener {
            val dialog = SortFragment.newInstance(sortingType)
            dialog.onRadioButtonSelectedListener = this
            dialog.show(childFragmentManager, SortFragment.TAG)
        }

        recyclerView.setOnScrollChangeListener { _, _, scrollY, _, oldScrollY ->
            // the delay of the extension of the FAB is set for 12 items
            if (scrollY > oldScrollY + 12 && buttonSort.isShown) {
                buttonSort.hide()
            }

            // the delay of the extension of the FAB is set for 12 items
            if (scrollY < oldScrollY - 12 && !buttonSort.isShown) {
                buttonSort.show()
            }
        }

        swipeRefreshLayout.setOnRefreshListener {
            viewModel.loadStation()
            viewModel.permission.gpsPermissionRequest(requireContext())
            swipeRefreshLayout.isRefreshing = false
        }

        viewModel.liveDataStationLocation.observe(requireActivity()) {
            it?.let {
                recyclerView.adapter = SpotListRecyclerView(
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