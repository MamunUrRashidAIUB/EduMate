package com.rashid.edumate.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.rashid.edumate.models.ClassModel
import com.rashid.edumate.utils.AuthManager

class ClassRepository {
    
    private val database: DatabaseReference = FirebaseDatabase.getInstance().reference
    private val classesRef: DatabaseReference = database.child("classes")
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    
    // Add a new class
    fun addClass(classModel: ClassModel, callback: (Boolean, String?) -> Unit) {
        try {
            val currentUser = auth.currentUser
            android.util.Log.d("ClassRepository", "Current user: ${currentUser?.uid}")
            
            if (currentUser != null) {
                val classId = classesRef.push().key
                android.util.Log.d("ClassRepository", "Generated class ID: $classId")
                
                if (classId != null) {
                    val updatedClass = classModel.copy(
                        classId = classId,
                        teacherId = currentUser.uid
                    )
                    
                    android.util.Log.d("ClassRepository", "Saving class: $updatedClass")
                    android.util.Log.d("ClassRepository", "Database path: ${classesRef.child(classId)}")
                    
                    classesRef.child(classId).setValue(updatedClass)
                        .addOnSuccessListener {
                            android.util.Log.d("ClassRepository", "✅ Successfully saved class to Firebase!")
                            callback(true, null)
                        }
                        .addOnFailureListener { exception ->
                            val errorMsg = "Firebase error: ${exception.message} (Code: ${exception.javaClass.simpleName})"
                            android.util.Log.e("ClassRepository", "❌ Failed to save class: $errorMsg")
                            callback(false, errorMsg)
                        }
                } else {
                    callback(false, "Failed to generate class ID")
                }
            } else {
                callback(false, "User not authenticated")
            }
        } catch (e: Exception) {
            callback(false, e.message)
        }
    }
    
    // Get all classes for current user
    fun getUserClasses(callback: (List<ClassModel>) -> Unit) {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            classesRef.orderByChild("teacherId").equalTo(currentUser.uid)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val classes = mutableListOf<ClassModel>()
                        for (classSnapshot in snapshot.children) {
                            val classModel = classSnapshot.getValue(ClassModel::class.java)
                            classModel?.let { classes.add(it) }
                        }
                        callback(classes)
                    }
                    
                    override fun onCancelled(error: DatabaseError) {
                        callback(emptyList())
                    }
                })
        } else {
            callback(emptyList())
        }
    }
    
    // Get upcoming classes (classes scheduled for today or future)
    fun getUpcomingClasses(callback: (List<ClassModel>) -> Unit) {
        val currentUser = auth.currentUser
        android.util.Log.d("ClassRepository", "getUpcomingClasses - Current user: ${currentUser?.uid}")
        
        if (currentUser != null) {
            val currentTime = System.currentTimeMillis()
            
            android.util.Log.d("ClassRepository", "Querying classes for user: ${currentUser.uid}")
            
            // Let's try getting ALL classes first to see the data structure
            classesRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val upcomingClasses = mutableListOf<ClassModel>()
                        
                        // Debug: Log the raw data
                        android.util.Log.d("ClassRepository", "Raw snapshot: ${snapshot.value}")
                        android.util.Log.d("ClassRepository", "Children count: ${snapshot.childrenCount}")
                        
                        // Debug: Log each child
                        for (child in snapshot.children) {
                            android.util.Log.d("ClassRepository", "Child key: ${child.key}")
                            android.util.Log.d("ClassRepository", "Child value: ${child.value}")
                        }
                        
                        for (classSnapshot in snapshot.children) {
                            val classModel = classSnapshot.getValue(ClassModel::class.java)
                            android.util.Log.d("ClassRepository", "Parsed class: $classModel")
                            classModel?.let { 
                                // Check if this class belongs to current user
                                android.util.Log.d("ClassRepository", "Class teacherId: '${it.teacherId}', Current user: '${currentUser.uid}'")
                                if (it.teacherId == currentUser.uid) {
                                    upcomingClasses.add(it)
                                    android.util.Log.d("ClassRepository", "✅ Added class: ${it.className}")
                                } else {
                                    android.util.Log.d("ClassRepository", "❌ Skipped class (different teacher): ${it.className}")
                                }
                            }
                        }
                        // Sort by creation time (most recent first)
                        upcomingClasses.sortByDescending { it.createdAt }
                        
                        android.util.Log.d("ClassRepository", "Final classes list size: ${upcomingClasses.size}")
                        callback(upcomingClasses)
                    }
                    
                    override fun onCancelled(error: DatabaseError) {
                        android.util.Log.e("ClassRepository", "❌ Database error: ${error.message}")
                        callback(emptyList())
                    }
                })
        } else {
            callback(emptyList())
        }
    }
    
    // Update a class
    fun updateClass(classModel: ClassModel, callback: (Boolean, String?) -> Unit) {
        try {
            classesRef.child(classModel.classId).setValue(classModel)
                .addOnSuccessListener {
                    callback(true, null)
                }
                .addOnFailureListener { exception ->
                    callback(false, exception.message)
                }
        } catch (e: Exception) {
            callback(false, e.message)
        }
    }
    
    // Delete a class
    fun deleteClass(classId: String, callback: (Boolean, String?) -> Unit) {
        try {
            classesRef.child(classId).removeValue()
                .addOnSuccessListener {
                    callback(true, null)
                }
                .addOnFailureListener { exception ->
                    callback(false, exception.message)
                }
        } catch (e: Exception) {
            callback(false, e.message)
        }
    }
    
    // Get single class by ID
    fun getClass(classId: String, callback: (ClassModel?) -> Unit) {
        classesRef.child(classId).get()
            .addOnSuccessListener { snapshot ->
                val classModel = snapshot.getValue(ClassModel::class.java)
                callback(classModel)
            }
            .addOnFailureListener {
                callback(null)
            }
    }
}