package com.rashid.edumate

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.rashid.edumate.utils.AuthManager

class SignupActivity : AppCompatActivity() {
    
    private lateinit var authManager: AuthManager
    private lateinit var nameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var confirmPasswordEditText: EditText
    private lateinit var signupButton: Button
    private lateinit var loginTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signup)
        
        // Initialize AuthManager
        authManager = AuthManager(this)
        
        // Initialize UI components
        initializeViews()
        setupClickListeners()
    }
    
    private fun initializeViews() {
        nameEditText = findViewById(R.id.usernameEditText)
        emailEditText = findViewById(R.id.editTextText3)
        passwordEditText = findViewById(R.id.editTextText4)
        confirmPasswordEditText = findViewById(R.id.editTextText5)
        signupButton = findViewById(R.id.signupButton)
        loginTextView = findViewById(R.id.textView11)
    }
    
    private fun setupClickListeners() {
        loginTextView.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
        
        signupButton.setOnClickListener {
            performSignUp()
        }
    }
    
    private fun performSignUp() {
        val name = nameEditText.text.toString().trim()
        val email = emailEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()
        val confirmPassword = confirmPasswordEditText.text.toString().trim()
        
        // Validation
        if (name.isEmpty()) {
            nameEditText.error = "Name is required"
            nameEditText.requestFocus()
            return
        }
        
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
        
        if (password.length < 6) {
            passwordEditText.error = "Password must be at least 6 characters"
            passwordEditText.requestFocus()
            return
        }
        
        if (confirmPassword.isEmpty()) {
            confirmPasswordEditText.error = "Please confirm your password"
            confirmPasswordEditText.requestFocus()
            return
        }
        
        if (password != confirmPassword) {
            confirmPasswordEditText.error = "Passwords do not match"
            confirmPasswordEditText.requestFocus()
            return
        }
        
        // Disable button to prevent multiple clicks
        signupButton.isEnabled = false
        signupButton.text = "Creating Account..."
        
        // Add timeout protection
        var signupCompleted = false
        
        // Timeout handler (15 seconds)
        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            if (!signupCompleted) {
                signupCompleted = true
                runOnUiThread {
                    signupButton.isEnabled = true
                    signupButton.text = "Sign Up"
                    Toast.makeText(this@SignupActivity, "Signup timeout. Please check your internet connection and try again.", Toast.LENGTH_LONG).show()
                }
            }
        }, 15000)
        
        // Perform Firebase signup (using simple method)
        authManager.signUpSimple(name, email, password) { success, error ->
            if (!signupCompleted) {
                signupCompleted = true
                runOnUiThread {
                    signupButton.isEnabled = true
                    signupButton.text = "Sign Up"
                    
                    if (success) {
                        Toast.makeText(this, "Account created successfully!", Toast.LENGTH_SHORT).show()
                        // Navigate to home screen
                        val intent = Intent(this, home::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this, "Sign up failed: ${error ?: "Unknown error"}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }
}
