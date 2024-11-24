package com.application.parkpilot.fragment.bottomSheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.RadioButton
import com.application.parkpilot.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class SortFragment : BottomSheetDialogFragment() {
    interface OnRadioButtonSelectedListener {
        fun onRadioButtonSelected(selectedRadioButtonId: Int)
    }

    interface OnFilterSelectedListener {
        fun onFilterSelected(filterSelection: List<Boolean>)
    }

    var onRadioButtonSelectedListener: OnRadioButtonSelectedListener? = null
    var onFilterSelectedListener: OnFilterSelectedListener? = null
    private var selectedRadioButtonId: Int = 0
    private var filterSelection = listOf(true, true)

    companion object {
        const val TAG = "SortFragment"

        fun newInstance(selectedRadioButtonId: Int, filterSelection: List<Boolean>): SortFragment {
            val fragment = SortFragment()
            fragment.selectedRadioButtonId = selectedRadioButtonId
            fragment.filterSelection = filterSelection
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
        val freeCheckBox: CheckBox = view.findViewById(R.id.freeCheckBox)
        val paidCheckBox: CheckBox = view.findViewById(R.id.paidCheckBox)
        val applyButton: Button = view.findViewById(R.id.applyButton)

        val radioButtonMap = mapOf(
            defaultFilter to 0,
            distanceFilter to 1,
            ratingsFilter to 2,
        )

        radioButtonMap.forEach { (radioButton, id) ->
            radioButton.isChecked = id == selectedRadioButtonId
        }

        freeCheckBox.isChecked = filterSelection[0]
        paidCheckBox.isChecked = filterSelection[1]

        radioButtonMap.keys.forEach { radioButton ->
            radioButton.setOnClickListener {
                selectedRadioButtonId = radioButtonMap[radioButton] ?: 0
            }
        }

        applyButton.setOnClickListener {
            notifyActivity(selectedRadioButtonId, freeCheckBox, paidCheckBox)
            dismiss()
        }

        return view
    }

    private fun notifyActivity(
        selectedRadioButtonId: Int,
        freeCheckBox: CheckBox,
        paidCheckBox: CheckBox
    ) {
        onRadioButtonSelectedListener?.onRadioButtonSelected(
            selectedRadioButtonId
        )
        onFilterSelectedListener?.onFilterSelected(
            listOf(freeCheckBox.isChecked, paidCheckBox.isChecked)
        )
    }
}