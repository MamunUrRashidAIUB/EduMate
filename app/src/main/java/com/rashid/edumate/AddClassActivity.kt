package com.rashid.edumate

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.rashid.edumate.models.ClassModel
import com.rashid.edumate.utils.FirebaseHelper

class AddClassActivity : AppCompatActivity() {
    
    private lateinit var auth: FirebaseAuth
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.addclass)
        
        auth = FirebaseAuth.getInstance()
        
        // Example: Add a new class to Firebase
        // You would typically get this data from UI elements
        // addNewClass("Mathematics", "Advanced Calculus", "Room 101")
    }
    
    private fun addNewClass(className: String, subject: String, description: String) {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val classId = FirebaseHelper.classesRef.push().key ?: return
            
            val newClass = ClassModel(
                classId = classId,
                className = className,
                subject = subject,
                teacherId = currentUser.uid,
                teacherName = currentUser.displayName ?: "Teacher",
                description = description
            )
            
            FirebaseHelper.writeClass(classId, newClass.toMap())
            
            Toast.makeText(this, "Class added successfully!", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}

// Extension function to convert data class to Map
private fun ClassModel.toMap(): Map<String, Any> {
    return mapOf(
        "classId" to classId,
        "className" to className,
        "subject" to subject,
        "teacherId" to teacherId,
        "teacherName" to teacherName,
        "students" to students,
        "description" to description,
        "createdAt" to createdAt
    )
}
