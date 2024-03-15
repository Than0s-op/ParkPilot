package com.application.parkpilot.viewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.application.parkpilot.R
import com.application.parkpilot.adapter.HistoryRecyclerView
import com.application.parkpilot.module.firebase.database.QRCode
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch

class BookingHistoryViewModel : ViewModel() {
    fun loadRecycler(context: Context, recyclerView: RecyclerView) {
        val fireStoreQRCode = QRCode()
        viewModelScope.launch {
            val list = fireStoreQRCode.QRCodeGet(Firebase.auth.currentUser!!.uid)
            recyclerView.layoutManager = LinearLayoutManager(context)
            recyclerView.adapter = HistoryRecyclerView(context, R.layout.history_card, list!!)
        }
    }
}