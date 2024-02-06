package com.application.parkpilot.module

import android.app.Activity
import android.content.Context
import android.widget.Toast
import com.application.parkpilot.R
import com.razorpay.Checkout
import com.razorpay.PayloadHelper
import com.razorpay.PaymentResultListener

class RazorPay(private val context: Activity): PaymentResultListener {
    private val checkout = Checkout()
    init {
        Checkout.preload(context);
        Checkout.sdkCheckIntegration(context)

        // it will set razor api key
        checkout.setKeyID(context.getString(R.string.razor_pay_test_key))

        // it will set image to gateway
//        checkout.setImage()
    }

    fun makePayment(currency:String, amount:Int, orderId:String){
        // for detail reading https://razorpay.com/docs/payments/payment-gateway/android-integration/standard/build-integration/

        // currency = "INR"... like this
        val payloadHelper = PayloadHelper(currency,amount,orderId)
        payloadHelper.name = "Than0s"
        checkout.open(context,payloadHelper.getJson())
    }

    override fun onPaymentError(errorCode: Int, response: String?) {
        /**
         * Add your logic here for a failed payment response
         */
        println("razor failed: $response")
        Toast.makeText(context, "Payment Failed due to error : $response", Toast.LENGTH_SHORT).show();
    }

    override fun onPaymentSuccess(razorpayPaymentId: String?) {
        /**
         * Add your logic here for a successful payment response
         */
        println("razor success: $razorpayPaymentId")
        Toast.makeText(context, "Payment is successful : $razorpayPaymentId", Toast.LENGTH_SHORT).show();
    }
}