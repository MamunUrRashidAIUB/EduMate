package com.rashid.edumate

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.rashid.edumate.utils.AuthManager

class home : AppCompatActivity() {
    
    private lateinit var authManager: AuthManager
    private lateinit var greetingTextView: TextView
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home)
        
        // Initialize AuthManager
        authManager = AuthManager(this)
        
        // Check authentication state
        checkAuthenticationState()
        
        // Initialize UI components
        initializeViews()
        setupClickListeners()
        
        // Update greeting with user name
        updateGreeting()
    }
    
    private fun checkAuthenticationState() {
        // If user is not logged in, redirect to login
        if (!authManager.isUserLoggedIn()) {
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
            return
        }
    }
    
    private fun initializeViews() {
        greetingTextView = findViewById(R.id.greetingText)
    }
    
    private fun setupClickListeners() {
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
    
    private fun updateGreeting() {
        val userName = authManager.getUserName()
        if (userName.isNotEmpty()) {
            greetingTextView.text = "Hi, $userName"
        } else {
            // Fallback if name is not available
            greetingTextView.text = "Hi, User"
        }
    }
    
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.home_menu, menu)
        return true
    }
    
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                logout()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    
    private fun logout() {
        authManager.signOut()
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
