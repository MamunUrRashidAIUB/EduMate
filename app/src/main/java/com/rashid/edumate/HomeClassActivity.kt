package com.rashid.edumate

import android.content.Intent
import android.os.Bundle
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity

class HomeClassActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.homeclass)
        
        // Initialize add button
        val btnAddClass = findViewById<FrameLayout>(R.id.btnAddClass)
        
        // Set click listener for add button
        btnAddClass.setOnClickListener {
            val intent = Intent(this, AddClassActivity::class.java)
            startActivity(intent)
        }
    }
}
