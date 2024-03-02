package com.application.parkpilot.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.application.parkpilot.R

class HowAreYou: AppCompatActivity(R.layout.how_are_you) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val buttonOwner:Button = findViewById(R.id.buttonOwner)
        val buttonFinder: Button = findViewById(R.id.buttonFinder)

        buttonOwner.setOnClickListener {
            startActivity(Intent(this,ParkRegisterActivity::class.java))
        }

        buttonFinder.setOnClickListener{
            startActivity(Intent(this,UserRegisterActivity::class.java))
        }
    }
}