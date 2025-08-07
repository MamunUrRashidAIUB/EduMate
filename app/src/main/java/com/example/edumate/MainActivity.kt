package com.example.edumate

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen // 1. Import this
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlinx.coroutines.delay // For a coroutine-based delay if absolutely needed for data loading
import kotlinx.coroutines.launch
import androidx.lifecycle.lifecycleScope // For launching coroutines tied to activity lifecycle

class MainActivity : AppCompatActivity() {
    // A flag to simulate data loading or some initial setup
    private var isLoading = true

    override fun onCreate(savedInstanceState: Bundle?) {
        // 2. Install the splash screen. MUST be called before super.onCreate()
        val splashScreen = installSplashScreen()

        super.onCreate(savedInstanceState)

        // 3. Optional: Keep the splash screen visible while app initializes/loads data
        //    This is the recommended way to control splash screen duration.
        splashScreen.setKeepOnScreenCondition {
            // Return true to keep the splash screen on, false to dismiss it.
            // Replace 'isLoading' with your actual condition (e.g., data from ViewModel is ready).
            isLoading
        }

        // Simulate some background work (e.g., loading data)
        // For a real app, this would be loading from a database, network, etc.
        lifecycleScope.launch { // Use coroutines for background tasks
            delay(2000) // Simulate a 2-second loading delay (adjust as needed)
            isLoading = false // Update the condition to dismiss the splash screen
            // At this point, setKeepOnScreenCondition will return false, and the splash screen will hide.
        }

        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}
