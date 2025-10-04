package com.rashid.edumate.utils

import android.content.Context
import android.content.SharedPreferences
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.rashid.edumate.models.User

class AuthManager(private val context: Context) {
    
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database: DatabaseReference = FirebaseDatabase.getInstance().reference
    private val sharedPrefs: SharedPreferences = context.getSharedPreferences("EduMatePrefs", Context.MODE_PRIVATE)
    
    // Check if user is currently logged in
    fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null && getUserName().isNotEmpty()
    }
    
    // Get current user
    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }
    
    // Get user name from shared preferences
    fun getUserName(): String {
        return sharedPrefs.getString("user_name", "") ?: ""
    }
    
    // Save user name to shared preferences
    private fun saveUserName(name: String) {
        sharedPrefs.edit().putString("user_name", name).apply()
    }
    
    // Sign up with email and password
    fun signUp(name: String, email: String, password: String, callback: (Boolean, String?) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    user?.let {
                        // Save user data to Firebase Realtime Database
                        val userModel = User(
                            userId = it.uid,
                            name = name,
                            email = email,
                            role = "student"
                        )
                        
                        database.child("users").child(it.uid).setValue(userModel)
                            .addOnCompleteListener { dbTask ->
                                if (dbTask.isSuccessful) {
                                    saveUserName(name)
                                    callback(true, null)
                                } else {
                                    callback(false, dbTask.exception?.message)
                                }
                            }
                    } ?: callback(false, "User creation failed")
                } else {
                    callback(false, task.exception?.message)
                }
            }
    }
    
    // Sign in with email and password
    fun signIn(email: String, password: String, callback: (Boolean, String?) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    user?.let {
                        // Fetch user data from Firebase to get the name
                        database.child("users").child(it.uid)
                            .addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    val userModel = snapshot.getValue(User::class.java)
                                    if (userModel != null) {
                                        saveUserName(userModel.name)
                                        callback(true, null)
                                    } else {
                                        callback(false, "User data not found")
                                    }
                                }
                                
                                override fun onCancelled(error: DatabaseError) {
                                    callback(false, error.message)
                                }
                            })
                    } ?: callback(false, "Authentication failed")
                } else {
                    callback(false, task.exception?.message)
                }
            }
    }
    
    // Sign out
    fun signOut() {
        auth.signOut()
        sharedPrefs.edit().clear().apply()
    }
    
    // Check authentication state and fetch user data
    fun checkAuthState(callback: (Boolean) -> Unit) {
        val currentUser = auth.currentUser
        if (currentUser != null && getUserName().isEmpty()) {
            // User is authenticated but we don't have name stored locally
            database.child("users").child(currentUser.uid)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val userModel = snapshot.getValue(User::class.java)
                        if (userModel != null) {
                            saveUserName(userModel.name)
                            callback(true)
                        } else {
                            callback(false)
                        }
                    }
                    
                    override fun onCancelled(error: DatabaseError) {
                        callback(false)
                    }
                })
        } else {
            callback(currentUser != null)
        }
    }
}