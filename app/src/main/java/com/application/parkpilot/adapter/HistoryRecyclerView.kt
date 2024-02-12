package com.application.parkpilot.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.application.parkpilot.QRCodeCollection
import com.application.parkpilot.R

class HistoryRecyclerView(
    private val context: Context,
    private val layout: Int,
    private val QRDetail: ArrayList<QRCodeCollection>
) : RecyclerView.Adapter<HistoryRecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(context).inflate(layout, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return QRDetail.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textViewDate.text = QRDetail[position].generate.toDate().toString()
        holder.textViewTime.text = QRDetail[position].generate.toString()
        holder.textViewHours.text = QRDetail[position].upTo.toString()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewDate: TextView = itemView.findViewById(R.id.textViewDate)
        val textViewTime:TextView = itemView.findViewById(R.id.textViewTime)
        val textViewHours:TextView = itemView.findViewById(R.id.textViewHours)
    }
}