package com.application.parkpilot.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.application.parkpilot.R


// NOTE: this activity will start, when user would login first time
class WhoAreYou : AppCompatActivity(R.layout.who_are_you) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // owner button creating
        val buttonOwner: Button = findViewById(R.id.buttonOwner)

        // finder button creating
        val buttonFinder: Button = findViewById(R.id.buttonFinder)

        // when owner button clicked
        buttonOwner.setOnClickListener {
            // start owner registration activity
            startActivity(Intent(this, ParkRegisterActivity::class.java))
        }

        // when finder button clicked
        buttonFinder.setOnClickListener {
            // start finder registration activity
            startActivity(Intent(this, UserRegisterActivity::class.java))
        }
    }
}