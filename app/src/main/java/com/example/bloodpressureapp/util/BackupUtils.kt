package com.example.bloodpressureapp.util

import android.content.Context
import com.example.bloodpressureapp.viewmodel.AppViewModel
import com.example.bloodpressureapp.data.BackupData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

suspend fun exportData(context: Context, viewModel: AppViewModel): String = withContext(Dispatchers.IO) {
    val users = viewModel.getAllUsersOnce()
    val measurements = viewModel.getAllMeasurements()
    val therapies = viewModel.getAllTherapies()
    val reminders = viewModel.getAllReminders()

    val backup = BackupData(
        users = users,
        measurements = measurements,
        therapies = therapies,
        reminders = reminders
    )

    Json.encodeToString(backup)
}

suspend fun importData(context: Context, jsonContent: String, viewModel: AppViewModel) = withContext(Dispatchers.IO) {
    val backup = Json.decodeFromString<BackupData>(jsonContent)

    val userIdMap = mutableMapOf<Int, Int>()

    backup.users.forEach { user ->
        val newId = viewModel.saveUserAndReturnId(user.name)
        userIdMap[user.id] = newId
    }

    backup.measurements.forEach { m ->
        val newUserId = userIdMap[m.userId] ?: return@forEach
        viewModel.saveMeasurement(
            systolic = m.systolic,
            diastolic = m.diastolic,
            pulse = m.pulse,
            arrhythmia = m.arrhythmia,
            userId = newUserId,
            timestamp = m.timestamp
        )
    }

    backup.therapies.forEach { t ->
        val newUserId = userIdMap[t.userId] ?: return@forEach
        viewModel.saveTherapy(newUserId, t.name, t.dosage)
    }

    backup.reminders.forEach { r ->
        val newUserId = userIdMap[r.userId] ?: return@forEach
        viewModel.addReminder(
            context = context,
            userId = newUserId,
            hour = r.hour,
            minute = r.minute,
            message = r.message,
            repeatDaily = r.repeatDaily,
            days = r.days
        )
    }
}