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
        return auth.currentUser != null
    }
    
    // Get current user
    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }
    
    // Get user name from shared preferences
    fun getUserName(): String {
        return sharedPrefs.getString("user_name", "") ?: ""
    }
    
    // Get user email from shared preferences
    fun getUserEmail(): String {
        return sharedPrefs.getString("user_email", "") ?: ""
    }
    
    // Save user name to shared preferences
    private fun saveUserName(name: String) {
        sharedPrefs.edit().putString("user_name", name).apply()
    }
    
    // Save user email to shared preferences
    private fun saveUserEmail(email: String) {
        sharedPrefs.edit().putString("user_email", email).apply()
    }
    
    // Simple sign up method (recommended)
    fun signUpSimple(name: String, email: String, password: String, callback: (Boolean, String?) -> Unit) {
        try {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        if (user != null) {
                            // Save user info locally first
                            saveUserName(name)
                            saveUserEmail(email)
                            
                            // Create user data in Firebase in background (non-blocking)
                            val userModel = User(
                                userId = user.uid,
                                name = name,
                                email = email,
                                role = "student"
                            )
                            database.child("users").child(user.uid).setValue(userModel)
                            
                            // Return success immediately
                            callback(true, null)
                        } else {
                            callback(false, "User creation failed")
                        }
                    } else {
                        callback(false, task.exception?.message)
                    }
                }
                .addOnFailureListener { exception ->
                    callback(false, "Sign-up failed: ${exception.message}")
                }
        } catch (e: Exception) {
            callback(false, "Sign-up error: ${e.message}")
        }
    }
    
    // Sign up with email and password (original method)
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
                                    saveUserEmail(email)
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
    
    // Simple sign in method (fallback)
    fun signInSimple(email: String, password: String, callback: (Boolean, String?) -> Unit) {
        try {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        if (user != null) {
                            // Save basic info and proceed
                            val name = user.email?.substringBefore("@") ?: "User"
                            saveUserName(name)
                            saveUserEmail(user.email ?: "")
                            callback(true, null)
                        } else {
                            callback(false, "Authentication failed")
                        }
                    } else {
                        callback(false, task.exception?.message)
                    }
                }
        } catch (e: Exception) {
            callback(false, e.message)
        }
    }
    
    // Sign in with email and password
    fun signIn(email: String, password: String, callback: (Boolean, String?) -> Unit) {
        try {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    try {
                        if (task.isSuccessful) {
                            val user = auth.currentUser
                            if (user != null) {
                                // Fetch user data from Firebase to get the name
                                database.child("users").child(user.uid)
                                    .addListenerForSingleValueEvent(object : ValueEventListener {
                                        override fun onDataChange(snapshot: DataSnapshot) {
                                            try {
                                                val userModel = snapshot.getValue(User::class.java)
                                                if (userModel != null) {
                                                    saveUserName(userModel.name)
                                                    saveUserEmail(userModel.email)
                                                    callback(true, null)
                                                } else {
                                                    // If no user data in database, use email as fallback
                                                    val fallbackName = user.email?.substringBefore("@") ?: "User"
                                                    val fallbackEmail = user.email ?: ""
                                                    
                                                    saveUserName(fallbackName)
                                                    saveUserEmail(fallbackEmail)
                                                    
                                                    // Try to create user data in background, but don't block login
                                                    val newUser = User(
                                                        userId = user.uid,
                                                        name = fallbackName,
                                                        email = fallbackEmail
                                                    )
                                                    database.child("users").child(user.uid).setValue(newUser)
                                                    
                                                    callback(true, null)
                                                }
                                            } catch (e: Exception) {
                                                callback(false, "Error processing user data: ${e.message}")
                                            }
                                        }
                                        
                                        override fun onCancelled(error: DatabaseError) {
                                            callback(false, "Database error: ${error.message}")
                                        }
                                    })
                            } else {
                                callback(false, "Authentication failed - no user")
                            }
                        } else {
                            callback(false, task.exception?.message ?: "Unknown authentication error")
                        }
                    } catch (e: Exception) {
                        callback(false, "Authentication exception: ${e.message}")
                    }
                }
                .addOnFailureListener { exception ->
                    callback(false, "Sign-in failed: ${exception.message}")
                }
        } catch (e: Exception) {
            callback(false, "Sign-in error: ${e.message}")
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
        if (currentUser != null) {
            // User is authenticated, check if we have user data locally
            if (getUserName().isEmpty() || getUserEmail().isEmpty()) {
                // Fetch user data from Firebase
                database.child("users").child(currentUser.uid)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val userModel = snapshot.getValue(User::class.java)
                            if (userModel != null) {
                                saveUserName(userModel.name)
                                saveUserEmail(userModel.email)
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
                // User data already available locally
                callback(true)
            }
        } else {
            // User is not authenticated
            callback(false)
        }
    }
}