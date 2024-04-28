package com.application.parkpilot.viewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.RecyclerView
import com.application.parkpilot.R
import com.application.parkpilot.User
import com.application.parkpilot.adapter.recycler.History
import com.application.parkpilot.module.firebase.database.Booking
import kotlinx.coroutines.launch

class BookingHistoryViewModel : ViewModel() {
    fun loadRecycler(context: Context, recyclerView: RecyclerView) {
        val booking = Booking()
        viewModelScope.launch {
            val list = booking.getTicket(User.UID)
            recyclerView.adapter = History(context, R.layout.history_card, list)
        }
    }
}