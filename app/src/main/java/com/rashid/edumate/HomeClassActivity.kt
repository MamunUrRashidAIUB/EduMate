package com.rashid.edumate

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rashid.edumate.adapter.ClassAdapter
import com.rashid.edumate.models.ClassModel
import com.rashid.edumate.repository.ClassRepository
import com.rashid.edumate.utils.AuthManager

class HomeClassActivity : AppCompatActivity() {
    
    private lateinit var classAdapter: ClassAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyStateText: TextView
    private lateinit var classRepository: ClassRepository
    private lateinit var authManager: AuthManager
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.homeclass)
        
        // Initialize components
        initializeComponents()
        setupRecyclerView()
        setupClickListeners()
        loadClasses()
    }
    
    private fun initializeComponents() {
        classRepository = ClassRepository()
        authManager = AuthManager(this)
        
        // Find views (we'll update the layout to include RecyclerView)
        recyclerView = findViewById(R.id.classesRecyclerView)
        emptyStateText = findViewById(R.id.emptyStateText)
    }
    
    private fun setupRecyclerView() {
        classAdapter = ClassAdapter(
            onClassClick = { classModel ->
                // Handle class item click - could open class details
                showClassDetails(classModel)
            },
            onOptionsClick = { classModel ->
                // Handle options menu click - edit/delete class
                showClassOptions(classModel)
            }
        )
        
        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@HomeClassActivity)
            adapter = classAdapter
        }
    }
    
    private fun setupClickListeners() {
        // Initialize add button
        val btnAddClass = findViewById<FrameLayout>(R.id.btnAddClass)
        
        // Set click listener for add button
        btnAddClass.setOnClickListener {
            val intent = Intent(this, AddClassActivity::class.java)
            startActivityForResult(intent, ADD_CLASS_REQUEST_CODE)
        }
    }
    
    private fun loadClasses() {
        // Debug: Check if user is authenticated
        if (!AuthManager(this).isUserLoggedIn()) {
            Toast.makeText(this, "User not logged in!", Toast.LENGTH_LONG).show()
            showEmptyState()
            return
        }
        
        Toast.makeText(this, "Loading classes...", Toast.LENGTH_SHORT).show()
        
        classRepository.getUpcomingClasses { classes ->
            runOnUiThread {
                Toast.makeText(this, "Found ${classes.size} classes", Toast.LENGTH_LONG).show()
                
                // Debug: Show class names
                if (classes.isNotEmpty()) {
                    val classNames = classes.map { it.className }.joinToString(", ")
                    Toast.makeText(this, "Classes: $classNames", Toast.LENGTH_LONG).show()
                }
                
                if (classes.isEmpty()) {
                    showEmptyState()
                } else {
                    hideEmptyState()
                    classAdapter.updateClasses(classes)
                }
            }
        }
    }
    
    private fun showEmptyState() {
        recyclerView.visibility = View.GONE
        emptyStateText.visibility = View.VISIBLE
        emptyStateText.text = "No classes yet.\nTap + to add your first class!\n\n👆 Tap here to refresh & debug"
        
        // Add click to refresh with debug info
        emptyStateText.setOnClickListener {
            val user = AuthManager(this).getCurrentUser()
            Toast.makeText(this, "🔄 Refreshing for user: ${user?.uid}", Toast.LENGTH_LONG).show()
            loadClasses()
        }
    }
    
    private fun hideEmptyState() {
        recyclerView.visibility = View.VISIBLE
        emptyStateText.visibility = View.GONE
    }
    
    private fun showClassDetails(classModel: ClassModel) {
        // For now, show a simple dialog with class details
        AlertDialog.Builder(this)
            .setTitle(classModel.className)
            .setMessage("""
                Subject: ${classModel.subject}
                Schedule: ${classModel.schedule}
                Time: ${classModel.time}
                Room: ${classModel.room}
                Description: ${classModel.description}
            """.trimIndent())
            .setPositiveButton("OK", null)
            .show()
    }
    
    private fun showClassOptions(classModel: ClassModel) {
        AlertDialog.Builder(this)
            .setTitle("Class Options")
            .setItems(arrayOf("Edit Class", "Delete Class")) { _, which ->
                when (which) {
                    0 -> editClass(classModel)
                    1 -> deleteClass(classModel)
                }
            }
            .show()
    }
    
    private fun editClass(classModel: ClassModel) {
        // TODO: Implement edit functionality
        Toast.makeText(this, "Edit functionality coming soon!", Toast.LENGTH_SHORT).show()
    }
    
    private fun deleteClass(classModel: ClassModel) {
        AlertDialog.Builder(this)
            .setTitle("Delete Class")
            .setMessage("Are you sure you want to delete '${classModel.className}'?")
            .setPositiveButton("Delete") { _, _ ->
                classRepository.deleteClass(classModel.classId) { success, error ->
                    runOnUiThread {
                        if (success) {
                            Toast.makeText(this, "Class deleted successfully", Toast.LENGTH_SHORT).show()
                            classAdapter.removeClass(classModel)
                        } else {
                            Toast.makeText(this, "Failed to delete class: ${error ?: "Unknown error"}", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ADD_CLASS_REQUEST_CODE && resultCode == RESULT_OK) {
            // Refresh the class list when returning from add class
            loadClasses()
        }
    }
    
    companion object {
        private const val ADD_CLASS_REQUEST_CODE = 1001
    }
}
