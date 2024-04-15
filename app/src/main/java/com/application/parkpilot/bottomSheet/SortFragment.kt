package com.application.parkpilot.bottomSheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import com.application.parkpilot.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class SortFragment : BottomSheetDialogFragment() {
    interface OnRadioButtonSelectedListener {
        fun onRadioButtonSelected(selectedRadioButtonId: Int)
    }

    var onRadioButtonSelectedListener: OnRadioButtonSelectedListener? = null
    private var selectedRadioButtonId: Int = 0

    companion object {
        const val TAG = "SortFragment"

        fun newInstance(selectedRadioButtonId: Int): SortFragment {
            val fragment = SortFragment()
            fragment.selectedRadioButtonId = selectedRadioButtonId
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_sort, container, false)

        val defaultFilter: RadioButton = view.findViewById(R.id.defaultFilter)
        val distanceFilter: RadioButton = view.findViewById(R.id.distanceFilter)
        val ratingsFilter: RadioButton = view.findViewById(R.id.ratingsFilter)

        val radioButtonMap = mapOf(
            defaultFilter to 0,
            distanceFilter to 1,
            ratingsFilter to 2,
        )

        radioButtonMap.forEach { (radioButton, id) ->
            radioButton.isChecked = id == selectedRadioButtonId
        }

        radioButtonMap.keys.forEach { radioButton ->
            radioButton.setOnClickListener {
                selectedRadioButtonId = radioButtonMap[radioButton] ?: 0
                notifyActivity(selectedRadioButtonId)
                dismiss()
            }
        }

        return view
    }

    private fun notifyActivity(selectedRadioButtonId: Int) {
        onRadioButtonSelectedListener?.onRadioButtonSelected(
            selectedRadioButtonId
        )
    }
}