package com.application.parkpilot.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.application.parkpilot.QRCodeCollection
import com.application.parkpilot.R
import com.application.parkpilot.module.QRGenerator
import com.application.parkpilot.view.DialogQRCode
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class HistoryRecyclerView(
    private val context: Context,
    private val layout: Int,
    private val qrDetail: ArrayList<QRCodeCollection>
) : RecyclerView.Adapter<HistoryRecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(layout, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return qrDetail.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textViewDate.text = qrDetail[position].from.toDate().toString()
        holder.textViewTime.text = qrDetail[position].from.toString()
        holder.textViewHours.text = qrDetail[position].to.toString()

        // if QR code is not expired
        if (!qrDetail[position].valid) {

            // set invisible property to "Expired" text View
            holder.textViewExpired.visibility = View.INVISIBLE

            // set on click listener to card
            holder.itemView.setOnClickListener {
                // QR code generation using FireStore 'key'
                val drawableQRCode = QRGenerator(context).generate(qrDetail[position].key)

//                MaterialAlertDialogBuilder(context)
//                    .setView(DialogQRCode(context, drawableQRCode, "This is a QR Code"))
//                    .show()
            }
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewDate: TextView = itemView.findViewById(R.id.textViewDate)
        val textViewTime: TextView = itemView.findViewById(R.id.textViewTime)
        val textViewHours: TextView = itemView.findViewById(R.id.textViewHours)
        val textViewExpired: TextView = itemView.findViewById(R.id.textViewExpired)
    }
}