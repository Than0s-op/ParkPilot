package com.application.parkpilot.bottom_sheet

import android.os.Bundle
import android.view.View
import com.application.parkpilot.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

// type 1=bike , 2=car , 3=heavy_vehicle
class VehicleDetails(val vehicleType:Int):BottomSheetDialogFragment(R.layout.vehicle_details) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}