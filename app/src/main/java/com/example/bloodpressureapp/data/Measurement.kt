package com.example.bloodpressureapp.data

import androidx.room.*

@Entity(
    tableName = "measurements",
    foreignKeys = [ForeignKey(
        entity = User::class,
        parentColumns = ["id"],
        childColumns = ["userId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class Measurement(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: Int,
    val systolic: Int,
    val diastolic: Int,
    val pulse: Int,
    val arrhythmia: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)
