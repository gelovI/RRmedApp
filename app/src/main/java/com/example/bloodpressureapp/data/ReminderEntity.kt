package com.example.bloodpressureapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reminders")
data class Reminder(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: Int,
    val hour: Int,
    val minute: Int,
    val message: String,
    val repeatDaily: Boolean,
    val days: String = "",
    val createdAt: Long = System.currentTimeMillis()
)