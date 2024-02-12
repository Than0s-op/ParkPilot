package com.application.parkpilot.activity

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.application.parkpilot.QRCodeCollection
import com.application.parkpilot.R
import com.application.parkpilot.adapter.CarouselRecyclerView
import com.application.parkpilot.module.QRGenerator
import com.application.parkpilot.module.RazorPay
import com.application.parkpilot.view.DialogQRCode
import com.google.android.material.carousel.CarouselLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.razorpay.PaymentResultListener
import com.application.parkpilot.module.firebase.database.QRCode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class SpotDetailActivity : AppCompatActivity(R.layout.spot_detail), PaymentResultListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val recycleView: RecyclerView = findViewById(R.id.recycleView)
        val buttonBookNow: Button = findViewById(R.id.buttonBookNow)

        val qr = QRCode()
        CoroutineScope(Dispatchers.IO).launch {
            qr.QRCodeGet(Firebase.auth.currentUser!!.uid)
        }

        val arr: ArrayList<Any> = ArrayList()

        arr.add("https://images.pexels.com/photos/1545743/pexels-photo-1545743.jpeg?auto=compress&cs=tinysrgb&w=600")
        arr.add("https://images.pexels.com/photos/116675/pexels-photo-116675.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=1")
        arr.add("https://images.pexels.com/photos/707046/pexels-photo-707046.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=1")
        arr.add("https://images.pexels.com/photos/164634/pexels-photo-164634.jpeg?auto=compress&cs=tinysrgb&w=600")

        recycleView.layoutManager = CarouselLayoutManager()

        recycleView.adapter = CarouselRecyclerView(this, R.layout.square_carousel, arr)

        buttonBookNow.setOnClickListener {
            // this is temporary
            val razorpay = RazorPay(this)
            razorpay.makePayment("INR", 1000, "123")
        }
    }

    override fun onPaymentSuccess(razorpayPaymentID: String) {
        Toast.makeText(
            this,
            "Payment successfully completed $razorpayPaymentID",
            Toast.LENGTH_SHORT
        ).show()

        val key = Firebase.auth.currentUser!!.uid
        val drawableQRCode = QRGenerator(this).generate(key)
        val fireStoreQRCode = QRCode()

        GlobalScope.launch {
            fireStoreQRCode.QRCodeSet(QRCodeCollection(key, 10), Firebase.auth.currentUser!!.uid)
        }

        MaterialAlertDialogBuilder(this)
            .setView(DialogQRCode(this, drawableQRCode, "This is a QR Code"))
            .show()
    }

    override fun onPaymentError(code: Int, response: String) {
        Toast.makeText(this, "Payment failed $response", Toast.LENGTH_SHORT).show()
    }
}