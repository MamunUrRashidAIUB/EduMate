package com.rashid.edumate.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.rashid.edumate.models.Assignment

class AssignmentRepository {
    
    private val database: DatabaseReference = FirebaseDatabase.getInstance().reference
    private val assignmentsRef: DatabaseReference = database.child("assignments")
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    
    // Add a new assignment
    fun addAssignment(assignment: Assignment, callback: (Boolean, String?) -> Unit) {
        try {
            val currentUser = auth.currentUser
            if (currentUser != null) {
                val assignmentId = assignmentsRef.push().key
                if (assignmentId != null) {
                    val updatedAssignment = assignment.copy(
                        assignmentId = assignmentId,
                        teacherId = currentUser.uid
                    )
                    
                    assignmentsRef.child(assignmentId).setValue(updatedAssignment)
                        .addOnSuccessListener {
                            callback(true, null)
                        }
                        .addOnFailureListener { exception ->
                            callback(false, exception.message)
                        }
                } else {
                    callback(false, "Failed to generate assignment ID")
                }
            } else {
                callback(false, "User not authenticated")
            }
        } catch (e: Exception) {
            callback(false, e.message)
        }
    }
    
    // Get all assignments for current user
    fun getUserAssignments(callback: (List<Assignment>) -> Unit) {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            assignmentsRef.orderByChild("teacherId").equalTo(currentUser.uid)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val assignments = mutableListOf<Assignment>()
                        for (assignmentSnapshot in snapshot.children) {
                            val assignment = assignmentSnapshot.getValue(Assignment::class.java)
                            assignment?.let { assignments.add(it) }
                        }
                        callback(assignments)
                    }
                    
                    override fun onCancelled(error: DatabaseError) {
                        callback(emptyList())
                    }
                })
        } else {
            callback(emptyList())
        }
    }
    
    // Get upcoming assignments (assignments with future due dates)
    fun getUpcomingAssignments(callback: (List<Assignment>) -> Unit) {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val currentTime = System.currentTimeMillis()
            
            assignmentsRef.orderByChild("teacherId").equalTo(currentUser.uid)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val upcomingAssignments = mutableListOf<Assignment>()
                        for (assignmentSnapshot in snapshot.children) {
                            val assignment = assignmentSnapshot.getValue(Assignment::class.java)
                            assignment?.let { 
                                // Include assignments due in the future or recently created
                                if (it.dueDate >= currentTime || it.dueDate == 0L) {
                                    upcomingAssignments.add(it)
                                }
                            }
                        }
                        // Sort by due date (earliest first)
                        upcomingAssignments.sortBy { if (it.dueDate == 0L) Long.MAX_VALUE else it.dueDate }
                        callback(upcomingAssignments)
                    }
                    
                    override fun onCancelled(error: DatabaseError) {
                        callback(emptyList())
                    }
                })
        } else {
            callback(emptyList())
        }
    }
    
    // Update an assignment
    fun updateAssignment(assignment: Assignment, callback: (Boolean, String?) -> Unit) {
        try {
            assignmentsRef.child(assignment.assignmentId).setValue(assignment)
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
    
    // Delete an assignment
    fun deleteAssignment(assignmentId: String, callback: (Boolean, String?) -> Unit) {
        try {
            assignmentsRef.child(assignmentId).removeValue()
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
    
    // Get single assignment by ID
    fun getAssignment(assignmentId: String, callback: (Assignment?) -> Unit) {
        assignmentsRef.child(assignmentId).get()
            .addOnSuccessListener { snapshot ->
                val assignment = snapshot.getValue(Assignment::class.java)
                callback(assignment)
            }
            .addOnFailureListener {
                callback(null)
            }
    }
}