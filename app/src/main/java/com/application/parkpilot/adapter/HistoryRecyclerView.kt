package com.application.parkpilot.adapter

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.application.parkpilot.R
import com.application.parkpilot.Ticket
import com.application.parkpilot.module.QRGenerator
import com.application.parkpilot.view.DialogQRCode
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class HistoryRecyclerView(
    private val context: Context,
    private val layout: Int,
    private val tickets: ArrayList<Ticket>
) : RecyclerView.Adapter<HistoryRecyclerView.ViewHolder>() {
    val qrGenerator = QRGenerator(context)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(layout, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return tickets.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textViewFromDate.text = tickets[position].fromTimestamp.toDate().toString()
//        holder.textViewFromTime.text = tickets[position].from.toString()
        holder.textViewToDate.text = tickets[position].toTimestamp.toDate().toString()
//        holder.textViewToTime.text = tickets[position]

        // set on click listener to card
        holder.itemView.setOnClickListener {
            // QR code generation using FireStore 'key'
            val drawableQRCode = qrGenerator.generate(tickets[position].qrcode)

            MaterialAlertDialogBuilder(context)
                .setView(
                    DialogQRCode(
                        context as Activity,
                        drawableQRCode,
                        "This is a QR Code"
                    ).layout
                )
                .show()
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewFromDate: TextView = itemView.findViewById(R.id.textViewFromDate)
        val textViewFromTime: TextView = itemView.findViewById(R.id.textViewFromTime)
        val textViewToDate: TextView = itemView.findViewById(R.id.textViewToDate)
        val textViewToTime: TextView = itemView.findViewById(R.id.textViewToTime)
    }
}