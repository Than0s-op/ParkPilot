package com.application.parkpilot.viewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.RecyclerView
import com.application.parkpilot.R
import com.application.parkpilot.Ticket
import com.application.parkpilot.User
import com.application.parkpilot.adapter.recycler.History
import com.application.parkpilot.module.firebase.database.Booking
import kotlinx.coroutines.launch

class BookingHistoryViewModel : ViewModel() {
    var bookingList = emptyList<Ticket>()
    fun loadRecycler(context: Context, recyclerView: RecyclerView, onComplete: () -> Unit) {
        viewModelScope.launch {
            val booking = Booking()
            bookingList = booking.getTicket(User.UID)
            recyclerView.adapter = History(context, R.layout.history_card, ArrayList(bookingList))
            onComplete()
        }
    }
}