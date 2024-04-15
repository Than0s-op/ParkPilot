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
import com.application.parkpilot.viewModel.SpotListViewModel

class SpotList : Fragment(R.layout.spot_list) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerView)
        val swipeRefreshLayout: SwipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout)

        val viewModel = ViewModelProvider(this)[SpotListViewModel::class.java]
        viewModel.loadStation()
        viewModel.permission.gpsPermissionRequest(requireContext())
        
        swipeRefreshLayout.setOnRefreshListener {
            viewModel.loadStation()
            viewModel.permission.gpsPermissionRequest(requireContext())
            swipeRefreshLayout.isRefreshing = false
        }
        viewModel.liveDataStationLocation.observe(requireActivity()) {
            it?.let {
                recyclerView.layoutManager = LinearLayoutManager(requireContext())
                recyclerView.adapter =
                    SpotListRecyclerView(requireContext(), R.layout.spot_list_item, it)
            }
        }
    }
}