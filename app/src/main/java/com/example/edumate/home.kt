package com.example.edumate

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView

class home : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home)
        
        // Initialize CardViews
        val cardUpcomingClasses = findViewById<CardView>(R.id.cardUpcomingClasses)
        val cardUpcomingEvent = findViewById<CardView>(R.id.cardUpcomingEvent)
        val cardAssignmentDeadlines = findViewById<CardView>(R.id.cardAssignmentDeadlines)
        
        // Set click listeners
        cardUpcomingClasses.setOnClickListener {
            val intent = Intent(this, HomeClassActivity::class.java)
            startActivity(intent)
        }
        
        cardUpcomingEvent.setOnClickListener {
            val intent = Intent(this, HomeEventActivity::class.java)
            startActivity(intent)
        }
        
        cardAssignmentDeadlines.setOnClickListener {
            val intent = Intent(this, HomeAssignmentActivity::class.java)
            startActivity(intent)
        }
    }
}