package com.application.parkpilot.bottomSheet

import android.os.Bundle
import android.view.View
import android.widget.Button
import com.application.parkpilot.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class WhoAreYou: BottomSheetDialogFragment(R.layout.who_are_you) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val buttonStationOwner: Button = view.findViewById(R.id.buttonStationOwner)
        val buttonNormalUser:Button = view.findViewById(R.id.buttonNormalUser)

    }
}