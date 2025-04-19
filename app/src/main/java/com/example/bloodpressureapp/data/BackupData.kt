package com.example.bloodpressureapp.data

import kotlinx.serialization.Serializable

@Serializable
data class BackupData(
    val users: List<User>,
    val measurements: List<Measurement>,
    val therapies: List<Therapy>,
    val reminders: List<Reminder>
)