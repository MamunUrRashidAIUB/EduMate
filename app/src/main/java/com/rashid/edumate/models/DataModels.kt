package com.rashid.edumate.models

// Data models for Firebase
data class User(
    val userId: String = "",
    val name: String = "",
    val email: String = "",
    val role: String = "student", // student, teacher, admin
    val profileImage: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

data class ClassModel(
    val classId: String = "",
    val className: String = "",
    val subject: String = "",
    val teacherId: String = "",
    val teacherName: String = "",
    val students: Map<String, Boolean> = mapOf(),
    val description: String = "",
    val schedule: String = "", // Days of week (e.g., "Mon,Wed,Fri")
    val time: String = "", // Class time (e.g., "10:00 AM - 11:30 AM")
    val room: String = "", // Classroom or location
    val color: String = "#B19CD9", // Color for UI display
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

data class Assignment(
    val assignmentId: String = "",
    val title: String = "",
    val description: String = "",
    val classId: String = "",
    val teacherId: String = "",
    val dueDate: Long = 0,
    val maxPoints: Int = 100,
    val submissions: Map<String, AssignmentSubmission> = mapOf(),
    val createdAt: Long = System.currentTimeMillis()
)

data class AssignmentSubmission(
    val studentId: String = "",
    val studentName: String = "",
    val content: String = "",
    val attachments: List<String> = listOf(),
    val submittedAt: Long = System.currentTimeMillis(),
    val grade: Int? = null,
    val feedback: String = ""
)

data class Event(
    val eventId: String = "",
    val title: String = "",
    val description: String = "",
    val date: Long = 0,
    val location: String = "",
    val organizer: String = "",
    val attendees: Map<String, Boolean> = mapOf(),
    val createdAt: Long = System.currentTimeMillis()
)