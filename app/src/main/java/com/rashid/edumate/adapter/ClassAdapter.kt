package com.rashid.edumate.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.rashid.edumate.R
import com.rashid.edumate.models.ClassModel
import java.text.SimpleDateFormat
import java.util.*

class ClassAdapter(
    private var classes: MutableList<ClassModel> = mutableListOf(),
    private val onClassClick: (ClassModel) -> Unit = {},
    private val onOptionsClick: (ClassModel) -> Unit = {}
) : RecyclerView.Adapter<ClassAdapter.ClassViewHolder>() {

    inner class ClassViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val classNameText: TextView = itemView.findViewById(R.id.classNameText)
        private val subjectText: TextView = itemView.findViewById(R.id.subjectText)
        private val scheduleText: TextView = itemView.findViewById(R.id.scheduleText)
        private val timeText: TextView = itemView.findViewById(R.id.timeText)
        private val roomText: TextView = itemView.findViewById(R.id.roomText)
        private val colorIndicator: View = itemView.findViewById(R.id.classColorIndicator)
        private val optionsMenu: ImageView = itemView.findViewById(R.id.optionsMenu)

        fun bind(classModel: ClassModel) {
            classNameText.text = classModel.className
            subjectText.text = classModel.subject
            
            // Display schedule
            scheduleText.text = if (classModel.schedule.isNotEmpty()) {
                classModel.schedule
            } else {
                "No schedule set"
            }
            
            // Display time
            timeText.text = if (classModel.time.isNotEmpty()) {
                classModel.time
            } else {
                "Time TBD"
            }
            
            // Display room
            roomText.text = if (classModel.room.isNotEmpty()) {
                classModel.room
            } else {
                "Room: TBD"
            }
            
            // Set color indicator
            try {
                val color = android.graphics.Color.parseColor(classModel.color)
                colorIndicator.setBackgroundColor(color)
            } catch (e: Exception) {
                // Fallback to default color
                colorIndicator.setBackgroundColor(android.graphics.Color.parseColor("#E2F163"))
            }
            
            // Click listeners
            itemView.setOnClickListener {
                onClassClick(classModel)
            }
            
            optionsMenu.setOnClickListener {
                onOptionsClick(classModel)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClassViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_class, parent, false)
        return ClassViewHolder(view)
    }

    override fun onBindViewHolder(holder: ClassViewHolder, position: Int) {
        holder.bind(classes[position])
    }

    override fun getItemCount(): Int = classes.size

    fun updateClasses(newClasses: List<ClassModel>) {
        classes.clear()
        classes.addAll(newClasses)
        notifyDataSetChanged()
    }
    
    fun addClass(classModel: ClassModel) {
        classes.add(0, classModel) // Add to beginning
        notifyItemInserted(0)
    }
    
    fun removeClass(classModel: ClassModel) {
        val index = classes.indexOfFirst { it.classId == classModel.classId }
        if (index != -1) {
            classes.removeAt(index)
            notifyItemRemoved(index)
        }
    }
    
    fun updateClass(classModel: ClassModel) {
        val index = classes.indexOfFirst { it.classId == classModel.classId }
        if (index != -1) {
            classes[index] = classModel
            notifyItemChanged(index)
        }
    }
}