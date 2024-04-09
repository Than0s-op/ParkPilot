package com.application.parkpilot.view

import android.content.Context
import android.content.res.ColorStateList
import android.util.TypedValue
import android.widget.LinearLayout
import android.widget.TextView
import com.application.parkpilot.R


class Amenities(context: Context, text: String) {
    val textView = TextView(context)

    init {
        textView.text = text
        val params: LinearLayout.LayoutParams =
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        params.setMargins(5, 5, 5, 5)
        textView.setLayoutParams(params)

        val padding = (context.resources.displayMetrics.density * 7).toInt()
        textView.setPadding(padding, padding, padding, padding)
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11.0f)
        textView.setBackgroundResource(R.drawable.round_corner_background)
        textView.backgroundTintList =
            ColorStateList.valueOf(context.resources.getColor(R.color.md_theme_light_primaryContainer))
    }
}