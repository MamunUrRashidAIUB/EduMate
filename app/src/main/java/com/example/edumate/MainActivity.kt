package com.example.edumate

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

class MainActivity : AppCompatActivity() {
    // A flag to simulate data loading or some initial setup
    private var isLoading = true

    override fun onCreate(savedInstanceState: Bundle?) {
        // 2. Install the splash screen. MUST be called before super.onCreate()
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        splashScreen.setKeepOnScreenCondition {
            isLoading
        }
        lifecycleScope.launch {
            delay(2000) // Simulate a 2-second loading delay
            isLoading = false // Update the condition to dismiss the splash screen
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
}
