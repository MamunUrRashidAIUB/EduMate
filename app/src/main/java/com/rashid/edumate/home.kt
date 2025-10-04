package com.rashid.edumate

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.rashid.edumate.utils.AuthManager

class home : AppCompatActivity() {
    
    private lateinit var authManager: AuthManager
    private lateinit var greetingTextView: TextView
    private lateinit var profileIcon: ImageView
    
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
        profileIcon = findViewById(R.id.profileIcon)
    }
    
    private fun setupClickListeners() {
        // Initialize CardViews
        val cardUpcomingClasses = findViewById<CardView>(R.id.cardUpcomingClasses)
        val cardUpcomingEvent = findViewById<CardView>(R.id.cardUpcomingEvent)
        val cardAssignmentDeadlines = findViewById<CardView>(R.id.cardAssignmentDeadlines)
        
        // Set click listeners for cards
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
        
        // Profile icon click listener
        profileIcon.setOnClickListener {
            showProfileDialog()
        }
    }
    
    private fun updateGreeting() {
        val userName = authManager.getUserName()
        if (userName.isNotEmpty()) {
            greetingTextView.text = "Hi, $userName"
        } else {
            // If name is not available, try to fetch it again
            val currentUser = authManager.getCurrentUser()
            if (currentUser != null) {
                authManager.checkAuthState { isLoggedIn ->
                    if (isLoggedIn) {
                        runOnUiThread {
                            val updatedName = authManager.getUserName()
                            greetingTextView.text = if (updatedName.isNotEmpty()) {
                                "Hi, $updatedName"
                            } else {
                                "Hi, User"
                            }
                        }
                    }
                }
            } else {
                greetingTextView.text = "Hi, User"
            }
        }
    }
    
    // Removed menu-based logout since we now use profile dialog
    
    private fun showProfileDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_profile)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        
        // Get dialog views
        val profileNameText = dialog.findViewById<TextView>(R.id.profileNameText)
        val profileEmailText = dialog.findViewById<TextView>(R.id.profileEmailText)
        val logoutButton = dialog.findViewById<Button>(R.id.logoutButton)
        
        // Set user data
        profileNameText.text = authManager.getUserName()
        profileEmailText.text = authManager.getUserEmail()
        
        // Logout button click listener
        logoutButton.setOnClickListener {
            dialog.dismiss()
            logout()
        }
        
        dialog.show()
    }
    
    private fun logout() {
        authManager.signOut()
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
