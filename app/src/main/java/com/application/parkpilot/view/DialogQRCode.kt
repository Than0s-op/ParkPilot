package com.application.parkpilot.view

import android.app.Activity
import android.graphics.drawable.Drawable
import android.widget.ImageView
import android.widget.TextView
import com.application.parkpilot.R


class DialogQRCode(activity: Activity, QRCode: Drawable, description: String) {
    val layout = activity.layoutInflater.inflate(R.layout.dialog_qrcode, null, false)

    init {
        val imageViewQRCode: ImageView = layout.findViewById(R.id.imageViewQRCode)
//        val textViewQRMessage: TextView = layout.findViewById(R.id.textViewQRMessage)
        imageViewQRCode.setImageDrawable(QRCode)
//        textViewQRMessage.text = description
    }
}