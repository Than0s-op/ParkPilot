package com.application.parkpilot.bottom_sheet

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.application.parkpilot.R
import com.application.parkpilot.activity.SpotDetailActivity
import com.application.parkpilot.adapter.Carousel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.carousel.CarouselLayoutManager

class VehicleType : BottomSheetDialogFragment(R.layout.vehicle_type) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recycleView: RecyclerView = view.findViewById(R.id.recycleView)
        val textViewName: TextView = view.findViewById(R.id.textViewName)
        val textViewRating:TextView = view.findViewById(R.id.textViewRating)
        val textViewDistance:TextView = view.findViewById(R.id.textViewDistance)
        val textViewPrice:TextView = view.findViewById(R.id.textViewPrice)
        val buttonDetail: Button = view.findViewById(R.id.buttonDetail)

        val arr: ArrayList<Any> = ArrayList()

        arr.add("https://images.pexels.com/photos/1545743/pexels-photo-1545743.jpeg?auto=compress&cs=tinysrgb&w=600")
        arr.add("https://images.pexels.com/photos/116675/pexels-photo-116675.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=1")
        arr.add("https://images.pexels.com/photos/707046/pexels-photo-707046.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=1")
        arr.add("https://images.pexels.com/photos/164634/pexels-photo-164634.jpeg?auto=compress&cs=tinysrgb&w=600")

        recycleView.layoutManager = CarouselLayoutManager()
        recycleView.adapter = Carousel(requireContext(),R.layout.carousel,arr)

        buttonDetail.setOnClickListener {
            val intent = Intent(context, SpotDetailActivity::class.java)
            startActivity(intent)
        }
    }
}