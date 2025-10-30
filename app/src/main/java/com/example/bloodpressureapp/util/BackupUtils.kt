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

suspend fun exportDataForUsers(
    context: Context,
    viewModel: AppViewModel,
    userIds: Set<Int>
): String = withContext(Dispatchers.IO) {
    // Daten holen
    val allUsers = viewModel.getAllUsersOnce()
    val allMeasurements = viewModel.getAllMeasurements()
    val allTherapies = viewModel.getAllTherapies()
    val allReminders = viewModel.getAllReminders()

    // Filtern
    val users = allUsers.filter { it.id in userIds }
    val measurements = allMeasurements.filter { it.userId in userIds }
    val therapies = allTherapies.filter { it.userId in userIds }
    val reminders = allReminders.filter { it.userId in userIds }

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
    importBackupSubset(context, viewModel, backup, sourceUserIds = backup.users.map { it.id }.toSet())
}

// --------- NEU: Import NUR ausgewählter Nutzer aus dem Backup ----------
suspend fun importDataForSelectedUsers(
    context: Context,
    jsonContent: String,
    viewModel: AppViewModel,
    sourceUserIds: Set<Int> // Nutzer-IDs aus dem Backup (nicht die DB!)
) = withContext(Dispatchers.IO) {
    val backup = Json.decodeFromString<BackupData>(jsonContent)
    importBackupSubset(context, viewModel, backup, sourceUserIds)
}

// --------- INTERN: Subset-Import mit ID-Neuzuordnung ----------
private suspend fun importBackupSubset(
    context: Context,
    viewModel: AppViewModel,
    backup: BackupData,
    sourceUserIds: Set<Int>
) = withContext(Dispatchers.IO) {
    val userIdMap = mutableMapOf<Int, Int>() // oldId -> newId

    // Nur gewählte Nutzer anlegen
    backup.users.filter { it.id in sourceUserIds }.forEach { user ->
        val newId = viewModel.saveUserAndReturnId(user.name)
        userIdMap[user.id] = newId
    }

    // Zugehörige Daten filtern und importieren
    backup.measurements.filter { it.userId in sourceUserIds }.forEach { m ->
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

    backup.therapies.filter { it.userId in sourceUserIds }.forEach { t ->
        val newUserId = userIdMap[t.userId] ?: return@forEach
        viewModel.saveTherapy(newUserId, t.name, t.dosage)
    }

    backup.reminders.filter { it.userId in sourceUserIds }.forEach { r ->
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

@kotlinx.serialization.Serializable
data class BackupUserPreview(
    val id: Int,     // ID aus dem Backup-File
    val name: String,
    val measurementCount: Int,
    val therapyCount: Int,
    val reminderCount: Int
)

fun peekUsersInBackup(jsonContent: String): List<BackupUserPreview> {
    val backup = Json.decodeFromString<BackupData>(jsonContent)
    return backup.users.map { u ->
        BackupUserPreview(
            id = u.id,
            name = u.name,
            measurementCount = backup.measurements.count { it.userId == u.id },
            therapyCount = backup.therapies.count { it.userId == u.id },
            reminderCount = backup.reminders.count { it.userId == u.id }
        )
    }.sortedBy { it.name.lowercase() }
}