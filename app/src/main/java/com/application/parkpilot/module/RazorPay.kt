package com.application.parkpilot.module

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import com.application.parkpilot.R
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.razorpay.Checkout
import com.razorpay.PayloadHelper
import com.razorpay.PaymentResultListener

class RazorPay:Activity(), PaymentResultListener {
    private lateinit var checkout:Checkout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Checkout.preload(this);
        Checkout.sdkCheckIntegration(this)

        // checkout initializing
        checkout = Checkout()

        // it will set razor api key
        checkout.setKeyID(this.getString(R.string.razor_pay_test_key))

        // it will set image to gateway
        // checkout.setImage()
    }

    fun makePayment(currency:String, amount:Int, orderId:String){
        // for detail reading https://razorpay.com/docs/payments/payment-gateway/android-integration/standard/build-integration/

        // currency = "INR"... like this
        val payloadHelper = PayloadHelper(currency,amount,orderId)
        payloadHelper.name = "Than0s"
        checkout.open(this,payloadHelper.getJson())
    }

    override fun onPaymentError(errorCode: Int, response: String?) {
        /**
         * Add your logic here for a failed payment response
         */
        println("razor failed: $response")
        Toast.makeText(this, "Payment Failed due to error : $response", Toast.LENGTH_SHORT).show()
        QRGenerator(this).generate(Firebase.auth.currentUser!!.uid)
    }

    override fun onPaymentSuccess(razorpayPaymentId: String?) {
        /**
         * Add your logic here for a successful payment response
         */
        println("razor success: $razorpayPaymentId")
        Toast.makeText(this, "Payment is successful : $razorpayPaymentId", Toast.LENGTH_SHORT).show()
        QRGenerator(this).generate(Firebase.auth.currentUser!!.uid)
    }
}