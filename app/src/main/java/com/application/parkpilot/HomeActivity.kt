package com.application.parkpilot

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.search.SearchBar
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import org.osmdroid.config.Configuration

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home)
        Configuration.getInstance().userAgentValue = "ParkPilot"

//        val logoutButton: Button = findViewById(R.id.logoutButton)
//        logoutButton.setOnClickListener {
//            Firebase.auth.signOut()
//            val intent = Intent(this,AuthenticationActivity::class.java)
//            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//            startActivity(intent)
//        }
        val OSMMap = OSM(findViewById(R.id.OSMMapView), this)
        val bar: SearchBar = findViewById(R.id.search_bar)
        bar.setOnMenuItemClickListener{
            when (it.itemId) {
                R.id.logoutButton -> {
                    // Code to be executed when the button is clicked
                    Firebase.auth.signOut()
                    Toast.makeText(this,"Logout Successfully", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this,AuthenticationActivity::class.java)
                    // to clear activity stack
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    return@setOnMenuItemClickListener true
                }

                else -> {
                    return@setOnMenuItemClickListener false
                }
            }
        }
    }

    // Inflating the menu items from the menu_items.xml file
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.searchbar_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    // Handling the click events of the menu items
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Switching on the item id of the menu item
        when (item.itemId) {
            R.id.logoutButton -> {
                // Code to be executed when the button is clicked
                Firebase.auth.signOut()
                Toast.makeText(this,"Logout Successfully", Toast.LENGTH_SHORT).show()
                val intent = Intent(this,AuthenticationActivity::class.java)
                // to clear activity stack
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}