package com.application.parkpilot.viewModel

import android.app.Activity
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.RecyclerView
import com.application.parkpilot.App
import com.application.parkpilot.QRCodeCollection
import com.application.parkpilot.R
import com.application.parkpilot.User
import com.application.parkpilot.module.QRGenerator
import com.application.parkpilot.module.RazorPay
import com.application.parkpilot.module.firebase.database.QRCode
import com.application.parkpilot.view.DialogQRCode
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch

class SpotDetailViewModel : ViewModel() {
    fun loadCarousel(context: Context, recyclerView: RecyclerView) {
        val images: ArrayList<Any> = ArrayList()

        // [temp START]
        images.apply {
            add("https://images.pexels.com/photos/1545743/pexels-photo-1545743.jpeg?auto=compress&cs=tinysrgb&w=600")
            add("https://images.pexels.com/photos/116675/pexels-photo-116675.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=1")
            add("https://images.pexels.com/photos/707046/pexels-photo-707046.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=1")
            add("https://images.pexels.com/photos/164634/pexels-photo-164634.jpeg?auto=compress&cs=tinysrgb&w=600")
        }
        // [temp END]
    }

    fun bookNow(activity: Activity) {
        val razorpay = RazorPay(activity)
        // [temp START]
        razorpay.makePayment(razorpay.INDIA, 1000, "123")
        // [temp END]
    }

    fun selectTime(){

    }

    fun onPaymentSuccess(context: Context, paymentID: String) {
        // key generation using UID of user and paymentID
        val key = User.UID!! + paymentID

        // QR code generation using key
        val drawableQRCode = QRGenerator(context).generate(key)

        // it is [com.application.parkpilot.module.firebase.database.QRCode]
        val fireStoreQRCode = QRCode()

        viewModelScope.launch {
            fireStoreQRCode.QRCodeSet(QRCodeCollection(key, 10), Firebase.auth.currentUser!!.uid)
        }

//        MaterialAlertDialogBuilder(context)
//            .setView(DialogQRCode(context, drawableQRCode, "This is a QR Code"))
//            .show()
    }
}