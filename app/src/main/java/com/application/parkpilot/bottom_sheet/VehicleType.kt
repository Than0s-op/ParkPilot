package com.application.parkpilot.bottom_sheet

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import com.application.parkpilot.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class VehicleType : BottomSheetDialogFragment(R.layout.vehicle_type) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val imageViewBike: ImageView = view.findViewById(R.id.imageViewBike)
        val imageViewCar: ImageView = view.findViewById(R.id.imageViewCar)
        val imageViewHeavyVehicle:ImageView = view.findViewById(R.id.imageViewHeavyVehicle)

        imageViewBike.setOnClickListener{
            // type 1 for bike
            VehicleDetails(1).show(parentFragmentManager,null)
        }
        imageViewCar.setOnClickListener{
            // type 2 for car
            VehicleDetails(2).show(parentFragmentManager,null)
        }
        imageViewHeavyVehicle.setOnClickListener{
            // type 3 for heavy vehicle
            VehicleDetails(3).show(parentFragmentManager,null)
        }
    }
}