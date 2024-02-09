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

class RazorPay(private val activity:Activity){
    private var checkout:Checkout
    init{
        Checkout.preload(activity)
        Checkout.sdkCheckIntegration(activity)

        // checkout initializing
        checkout = Checkout()

        // it will set razor api key
        checkout.setKeyID(activity.getString(R.string.razor_pay_test_key))

        // it will set image to gateway
        checkout.setImage(R.drawable.heavy_vehicle)
    }

    fun makePayment(currency:String, amount:Int, orderId:String){
        // for detail reading https://razorpay.com/docs/payments/payment-gateway/android-integration/standard/build-integration/

        // currency = "INR"... like this
        val payloadHelper = PayloadHelper(currency,amount,orderId)
        payloadHelper.name = "Than0s"
        checkout.open(activity,payloadHelper.getJson())
    }
}