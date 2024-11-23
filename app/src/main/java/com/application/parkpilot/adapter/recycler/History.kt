package com.application.parkpilot.adapter.recycler

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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class History(
    private val context: Context,
    private val layout: Int,
    private val tickets: ArrayList<Ticket>
) : RecyclerView.Adapter<History.ViewHolder>() {
    val qrGenerator = QRGenerator(context)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(layout, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return tickets.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textViewFromDate.text = getDate(tickets[position].fromTimestamp.toDate().time)
        holder.textViewFromTime.text = getTime(tickets[position].fromTimestamp.toDate().time)
        holder.textViewToDate.text = getDate(tickets[position].toTimestamp.toDate().time)
        holder.textViewToTime.text = getTime(tickets[position].toTimestamp.toDate().time)
        holder.textViewStationName.text = tickets[position].stationID

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

    private fun convertDateToSpecificFormat(date: Date, format: String): String {
        // Create a SimpleDateFormat with the desired format
        val sdf = SimpleDateFormat(format, Locale.getDefault())

        // Format the given Date object
        return sdf.format(date)
    }

    private fun getDate(timestamp: Long): String {
//        val format = "dd-MM-yyyy HH:mm:ss"  // Desired format
        val format = "dd-MM-yyyy"  // Desired format
        return convertDateToSpecificFormat(Date(timestamp), format)
    }

    private fun getTime(timestamp: Long): String {
        val format = "HH:mm:ss"  // Desired format
        return convertDateToSpecificFormat(Date(timestamp), format)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewFromDate: TextView = itemView.findViewById(R.id.textViewFromDate)
        val textViewFromTime: TextView = itemView.findViewById(R.id.textViewFromTime)
        val textViewToDate: TextView = itemView.findViewById(R.id.textViewToDate)
        val textViewToTime: TextView = itemView.findViewById(R.id.textViewToTime)
        val textViewStationName: TextView = itemView.findViewById(R.id.textViewStationName)
    }
}