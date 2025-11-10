package com.example.bloodpressureapp.util.backup

import kotlinx.serialization.Serializable

@Serializable
data class BackupData(
    val users: List<BackupUser> = emptyList(),
    val measurements: List<BackupMeasurement> = emptyList(),
    val therapies: List<BackupTherapy> = emptyList(),
    val reminders: List<BackupReminder> = emptyList()
)

@Serializable
data class BackupUser(
    val id: Int,
    val name: String
)

@Serializable
data class BackupMeasurement(
    val id: Int,
    val userId: Int,
    val systolic: Int,
    val diastolic: Int,
    val pulse: Int,
    val arrhythmia: Boolean,
    val timestamp: Long
)

@Serializable
data class BackupTherapy(
    val id: Int,
    val userId: Int,
    val name: String,
    val dosage: String
)

@Serializable
data class BackupReminder(
    val id: Int? = null,
    val userId: Int,
    val hour: Int? = null,
    val minute: Int? = null,
    val message: String? = null,
    val repeatDaily: Boolean? = null,
    val days: String? = null
)
