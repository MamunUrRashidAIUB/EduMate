package com.rashid.edumate.utils

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError

class FirebaseHelper {
    
    companion object {
        private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
        
        // Database references for different data types
        val usersRef: DatabaseReference = database.getReference("users")
        val classesRef: DatabaseReference = database.getReference("classes")
        val assignmentsRef: DatabaseReference = database.getReference("assignments")
        val eventsRef: DatabaseReference = database.getReference("events")
        
        // Write data to Firebase
        fun writeUser(userId: String, userData: Map<String, Any>) {
            usersRef.child(userId).setValue(userData)
        }
        
        fun writeClass(classId: String, classData: Map<String, Any>) {
            classesRef.child(classId).setValue(classData)
        }
        
        fun writeAssignment(assignmentId: String, assignmentData: Map<String, Any>) {
            assignmentsRef.child(assignmentId).setValue(assignmentData)
        }
        
        // Read data from Firebase
        fun readUserData(userId: String, callback: (DataSnapshot) -> Unit) {
            usersRef.child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    callback(snapshot)
                }
                
                override fun onCancelled(error: DatabaseError) {
                    // Handle error
                }
            })
        }
        
        // Listen for real-time updates
        fun listenToClasses(callback: (DataSnapshot) -> Unit) {
            classesRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    callback(snapshot)
                }
                
                override fun onCancelled(error: DatabaseError) {
                    // Handle error
                }
            })
        }
    }
}