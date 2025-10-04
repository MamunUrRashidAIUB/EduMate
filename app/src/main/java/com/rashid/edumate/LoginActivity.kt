package com.rashid.edumate

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.rashid.edumate.utils.AuthManager

class LoginActivity : AppCompatActivity() {
    
    private lateinit var authManager: AuthManager
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var signupTextView: TextView
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)
        
        // Initialize AuthManager
        authManager = AuthManager(this)
        
        // Check if user is already logged in
        if (authManager.isUserLoggedIn()) {
            navigateToHome()
            return
        }
        
        // Initialize UI components
        initializeViews()
        setupClickListeners()
    }
    
    private fun initializeViews() {
        emailEditText = findViewById(R.id.usernameEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        loginButton = findViewById(R.id.loginButton)
        signupTextView = findViewById(R.id.signUpText)
    }
    
    private fun setupClickListeners() {
        signupTextView.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }
        
        loginButton.setOnClickListener {
            performLogin()
        }
    }
    
    private fun performLogin() {
        val email = emailEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()
        
        // Validation
        if (email.isEmpty()) {
            emailEditText.error = "Email is required"
            emailEditText.requestFocus()
            return
        }
        
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.error = "Please enter a valid email"
            emailEditText.requestFocus()
            return
        }
        
        if (password.isEmpty()) {
            passwordEditText.error = "Password is required"
            passwordEditText.requestFocus()
            return
        }
        
        // Disable button to prevent multiple clicks
        loginButton.isEnabled = false
        loginButton.text = "Signing In..."
        
        // Perform Firebase login
        authManager.signIn(email, password) { success, error ->
            runOnUiThread {
                loginButton.isEnabled = true
                loginButton.text = "Login"
                
                if (success) {
                    Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show()
                    navigateToHome()
                } else {
                    Toast.makeText(this, "Login failed: ${error ?: "Unknown error"}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
    
    private fun navigateToHome() {
        val intent = Intent(this, home::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
