package com.example.bloodpressureapp.data

import androidx.room.*
import kotlinx.serialization.Serializable

@Serializable
@Entity(
    tableName = "measurements",
    foreignKeys = [ForeignKey(
        entity = User::class,
        parentColumns = ["id"],
        childColumns = ["userId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["userId"])]
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
