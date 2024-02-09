package com.application.parkpilot.view

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.Gravity
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView


class DialogQRCode(context: Context, QRCode: Drawable, description: String) :
    LinearLayout(context) {
    init {
        orientation = VERTICAL
        layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        gravity = Gravity.CENTER_HORIZONTAL

        val imageView = ImageView(context).apply {
            layoutParams = ViewGroup.LayoutParams(700, 700)
            setImageDrawable(QRCode)
        }

        val textView = TextView(context).apply {
            gravity = Gravity.CENTER_HORIZONTAL
            setPadding(10,10,10,10)
            text = description
        }

        addView(imageView)
        addView(textView)
    }
}