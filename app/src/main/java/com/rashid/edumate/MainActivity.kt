package com.rashid.edumate

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.lifecycle.lifecycleScope
import com.rashid.edumate.utils.AuthManager

class MainActivity : AppCompatActivity() {
    // A flag to simulate data loading or some initial setup
    private var isLoading = true
    private lateinit var authManager: AuthManager

    override fun onCreate(savedInstanceState: Bundle?) {
        // Install the splash screen. MUST be called before super.onCreate()
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        
        // Initialize AuthManager
        authManager = AuthManager(this)
        
        splashScreen.setKeepOnScreenCondition {
            isLoading
        }
        
        lifecycleScope.launch {
            // Check authentication state during splash screen
            checkAuthenticationAndNavigate()
        }
        
        enableEdgeToEdge()
        setContentView(R.layout.onboarding)

        // Add click listener for the "Get Started" button
        val getStartedButton: Button = findViewById(R.id.button)
        getStartedButton.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
    
    private suspend fun checkAuthenticationAndNavigate() {
        // Simulate loading delay
        delay(2000)
        
        // Check if user is already logged in
        authManager.checkAuthState { isLoggedIn ->
            runOnUiThread {
                isLoading = false
                
                if (isLoggedIn) {
                    // User is already logged in, navigate to home
                    val intent = Intent(this, home::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
                // If not logged in, stay on onboarding screen
            }
        }
    }
}
