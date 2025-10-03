package com.rashid.edumate

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton

class HomeAssignmentActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.homeassignment)
        
        // Initialize floating action button
        val fabAddAssignment = findViewById<FloatingActionButton>(R.id.fabAddAssignment)
        
        // Set click listener for FAB
        fabAddAssignment.setOnClickListener {
            val intent = Intent(this, AddAssignmentActivity::class.java)
            startActivity(intent)
        }
    }
}
