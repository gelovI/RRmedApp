package com.example.bloodpressureapp.util

import android.content.Context
import com.example.bloodpressureapp.viewmodel.AppViewModel
import com.example.bloodpressureapp.util.backup.BackupData
import com.example.bloodpressureapp.util.backup.BackupUser
import com.example.bloodpressureapp.util.backup.BackupMeasurement
import com.example.bloodpressureapp.util.backup.BackupTherapy
import com.example.bloodpressureapp.util.backup.BackupReminder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

sealed class ImportResult {
    data class Success(val importedUsers: Int) : ImportResult()
    data class Error(val message: String) : ImportResult()
}

// -------------- JSON Konfiguration --------------
private val jsonFmt = Json {
    ignoreUnknownKeys = true
    isLenient = true
    coerceInputValues = true
}

// -------------- EXPORT (ALLE) --------------
suspend fun exportData(
    context: Context,
    viewModel: AppViewModel
): String = withContext(Dispatchers.IO) {
    val users = viewModel.getAllUsersOnce()
    val measurements = viewModel.getAllMeasurements()
    val therapies = viewModel.getAllTherapies()
    val reminders = viewModel.getAllReminders()

    // Entities -> DTOs mappen
    val backup = BackupData(
        users = users.map { u ->
            BackupUser(id = u.id, name = u.name)
        },
        measurements = measurements.map { m ->
            BackupMeasurement(
                id = m.id,
                userId = m.userId,
                systolic = m.systolic,
                diastolic = m.diastolic,
                pulse = m.pulse,
                arrhythmia = m.arrhythmia,
                timestamp = m.timestamp
            )
        },
        therapies = therapies.map { t ->
            BackupTherapy(
                id = t.id,
                userId = t.userId,
                name = t.name,
                dosage = t.dosage
            )
        },
        reminders = reminders.map { r ->
            BackupReminder(
                id = r.id,
                userId = r.userId,
                hour = r.hour,
                minute = r.minute,
                message = r.message,
                repeatDaily = r.repeatDaily,
                days = r.days
            )
        }
    )

    jsonFmt.encodeToString(backup)
}

// -------------- EXPORT (ausgewählte Nutzer) --------------
suspend fun exportDataForUsers(
    context: Context,
    viewModel: AppViewModel,
    userIds: Set<Int>
): String = withContext(Dispatchers.IO) {
    val allUsers = viewModel.getAllUsersOnce()
    val allMeasurements = viewModel.getAllMeasurements()
    val allTherapies = viewModel.getAllTherapies()
    val allReminders = viewModel.getAllReminders()

    val users = allUsers.filter { it.id in userIds }.map { u ->
        BackupUser(id = u.id, name = u.name)
    }
    val measurements = allMeasurements.filter { it.userId in userIds }.map { m ->
        BackupMeasurement(
            id = m.id,
            userId = m.userId,
            systolic = m.systolic,
            diastolic = m.diastolic,
            pulse = m.pulse,
            arrhythmia = m.arrhythmia,
            timestamp = m.timestamp
        )
    }
    val therapies = allTherapies.filter { it.userId in userIds }.map { t ->
        BackupTherapy(
            id = t.id,
            userId = t.userId,
            name = t.name,
            dosage = t.dosage
        )
    }
    val reminders = allReminders.filter { it.userId in userIds }.map { r ->
        BackupReminder(
            id = r.id,
            userId = r.userId,
            hour = r.hour,
            minute = r.minute,
            message = r.message,
            repeatDaily = r.repeatDaily,
            days = r.days
        )
    }

    val backup = BackupData(
        users = users,
        measurements = measurements,
        therapies = therapies,
        reminders = reminders
    )

    jsonFmt.encodeToString(backup)
}

// -------------- IMPORT (ALLE) --------------
suspend fun importData(
    context: Context,
    jsonContent: String,
    viewModel: AppViewModel
) = withContext(Dispatchers.IO) {
    val clean = jsonContent.trimStart('\uFEFF')
    val backup = jsonFmt.decodeFromString<BackupData>(clean)
    importBackupSubset(context, viewModel, backup, sourceUserIds = backup.users.map { it.id }.toSet())
}

// -------------- IMPORT (ausgewählte Nutzer) --------------
suspend fun importDataForSelectedUsers(
    context: Context,
    jsonContent: String,
    viewModel: AppViewModel,
    sourceUserIds: Set<Int> // IDs aus dem Backup-File
) = withContext(Dispatchers.IO) {
    val clean = jsonContent.trimStart('\uFEFF')
    val backup = jsonFmt.decodeFromString<BackupData>(clean)
    importBackupSubset(context, viewModel, backup, sourceUserIds)
}

suspend fun importDataForSelectedUsersSafely(
    context: Context,
    jsonContent: String,
    viewModel: AppViewModel,
    sourceUserIds: Set<Int>,
    maxBytes: Int = 2_000_000
): ImportResult = withContext(Dispatchers.IO) {
    if (jsonContent.toByteArray().size > maxBytes) {
        return@withContext ImportResult.Error("Backup-Datei ist zu groß.")
    }

    val clean = jsonContent.trimStart('\uFEFF')

    val backup = try {
        jsonFmt.decodeFromString<BackupData>(clean)
    } catch (e: Exception) {
        return@withContext ImportResult.Error("Backup-Datei ist ungültig oder beschädigt.")
    }

    if (backup.users.isEmpty()) {
        return@withContext ImportResult.Error("Backup enthält keine Nutzer.")
    }

    val selectedUsers = backup.users.filter { it.id in sourceUserIds }
    if (selectedUsers.isEmpty()) {
        return@withContext ImportResult.Error("Keine passenden Nutzer im Backup gefunden.")
    }

    return@withContext try {
        importBackupSubset(context, viewModel, backup, sourceUserIds)
        ImportResult.Success(selectedUsers.size)
    } catch (e: Exception) {
        ImportResult.Error("Import fehlgeschlagen.")
    }
}

// -------------- INTERN: Subset-Import mit ID-Neuzuordnung --------------
private suspend fun importBackupSubset(
    context: Context,
    viewModel: AppViewModel,
    backup: BackupData,
    sourceUserIds: Set<Int>
) = withContext(Dispatchers.IO) {
    val userIdMap = mutableMapOf<Int, Int>() // oldId -> newId

    // Nutzer neu anlegen
    backup.users.filter { it.id in sourceUserIds }.forEach { user ->
        val newId = viewModel.saveUserAndReturnId(user.name)
        userIdMap[user.id] = newId
    }

    // Messungen
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

    // Therapien
    backup.therapies.filter { it.userId in sourceUserIds }.forEach { t ->
        val newUserId = userIdMap[t.userId] ?: return@forEach
        viewModel.saveTherapy(newUserId, t.name, t.dosage)
    }

    // Erinnerungen
    backup.reminders.filter { it.userId in sourceUserIds }.forEach { r ->
        val newUserId = userIdMap[r.userId] ?: return@forEach
        viewModel.addReminder(
            context = context,
            userId = newUserId,
            hour = r.hour ?: 9,
            minute = r.minute ?: 0,
            message = r.message ?: "",
            repeatDaily = r.repeatDaily ?: false,
            days = r.days ?: ""
        )
    }
}

// -------------- Vorschau der Nutzer im Backup --------------
@kotlinx.serialization.Serializable
data class BackupUserPreview(
    val id: Int,     // ID aus dem Backup
    val name: String,
    val measurementCount: Int,
    val therapyCount: Int,
    val reminderCount: Int
)

fun peekUsersInBackup(jsonContent: String): List<BackupUserPreview> {
    val clean = jsonContent.trimStart('\uFEFF')
    val backup = jsonFmt.decodeFromString<BackupData>(clean)
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