package com.rashid.edumate

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.rashid.edumate.models.ClassModel
import com.rashid.edumate.repository.ClassRepository
import com.rashid.edumate.utils.AuthManager

class AddClassActivity : AppCompatActivity() {
    
    private lateinit var classRepository: ClassRepository
    private lateinit var authManager: AuthManager
    
    // UI Elements
    private lateinit var classNameInput: EditText
    private lateinit var backButton: LinearLayout
    private lateinit var saveButton: Button
    private lateinit var selectedDays: MutableSet<String>
    
    // Day buttons
    private lateinit var btnMon: Button
    private lateinit var btnTue: Button
    private lateinit var btnWed: Button
    private lateinit var btnThu: Button
    private lateinit var btnFri: Button
    private lateinit var btnSat: Button
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.addclass)
        
        // Initialize components
        classRepository = ClassRepository()
        authManager = AuthManager(this)
        selectedDays = mutableSetOf()
        
        // Initialize UI
        initializeViews()
        setupClickListeners()
    }
    
    private fun initializeViews() {
        classNameInput = findViewById(R.id.classNameInput)
        backButton = findViewById(R.id.backButton)
        saveButton = findViewById(R.id.saveClassButton)
        
        // Day buttons
        btnMon = findViewById(R.id.btnMon)
        btnTue = findViewById(R.id.btnTue)
        btnWed = findViewById(R.id.btnWed)
        btnThu = findViewById(R.id.btnThu)
        btnFri = findViewById(R.id.btnFri)
        btnSat = findViewById(R.id.btnSat)
    }
    
    private fun setupClickListeners() {
        // Back button
        backButton.setOnClickListener {
            finish()
        }
        
        // Day selection buttons
        setupDayButton(btnMon, "Mon")
        setupDayButton(btnTue, "Tue")
        setupDayButton(btnWed, "Wed")
        setupDayButton(btnThu, "Thu")
        setupDayButton(btnFri, "Fri")
        setupDayButton(btnSat, "Sat")
        
        // Save button
        saveButton.setOnClickListener {
            saveClass()
        }
    }
    
    private fun setupDayButton(button: Button, day: String) {
        button.setOnClickListener {
            if (selectedDays.contains(day)) {
                selectedDays.remove(day)
                button.isSelected = false
                button.setBackgroundColor(getColor(android.R.color.darker_gray))
            } else {
                selectedDays.add(day)
                button.isSelected = true
                button.setBackgroundColor(getColor(R.color.purple_200))
            }
        }
    }
    
    private fun saveClass() {
        val className = classNameInput.text.toString().trim()
        
        // Check authentication first
        if (!authManager.isUserLoggedIn()) {
            Toast.makeText(this, "Please log in first", Toast.LENGTH_LONG).show()
            return
        }
        
        // Validation
        if (className.isEmpty()) {
            classNameInput.error = "Class name is required"
            classNameInput.requestFocus()
            return
        }
        
        if (selectedDays.isEmpty()) {
            Toast.makeText(this, "Please select at least one day", Toast.LENGTH_SHORT).show()
            return
        }
        
        // Debug info
        Toast.makeText(this, "Saving class for user: ${authManager.getUserName()}", Toast.LENGTH_SHORT).show()
        
        // Create class model
        val schedule = selectedDays.joinToString(", ")
        val newClass = ClassModel(
            className = className,
            subject = className, // Using className as subject for now
            teacherName = authManager.getUserName(),
            description = "Added via EduMate",
            schedule = schedule,
            time = "TBD", // Time to be determined
            room = "TBD" // Room to be determined
        )
        
        // Debug: Show what we're trying to save
        Toast.makeText(this, "Attempting to save class: $className", Toast.LENGTH_SHORT).show()
        
        // Save to Firebase
        classRepository.addClass(newClass) { success, error ->
            runOnUiThread {
                if (success) {
                    Toast.makeText(this, "✅ Class '$className' saved successfully!", Toast.LENGTH_LONG).show()
                    setResult(RESULT_OK)
                    finish()
                } else {
                    Toast.makeText(this, "❌ SAVE FAILED: ${error ?: "Unknown error"}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
