package com.rashid.edumate

import android.app.Application
import com.google.firebase.FirebaseApp

class EduMateApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        // Initialize Firebase
        FirebaseApp.initializeApp(this)
    }
}
