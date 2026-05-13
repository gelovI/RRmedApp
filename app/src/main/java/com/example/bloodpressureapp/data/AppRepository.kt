package com.example.bloodpressureapp.data

import kotlinx.coroutines.flow.Flow

class AppRepository(private val dao: AppDao) {

    val users: Flow<List<User>> = dao.getAllUsers()

    fun measurementsForUser(userId: Int): Flow<List<Measurement>> =
        dao.getMeasurementsForUser(userId)

    fun therapiesForUser(userId: Int): Flow<List<Therapy>> =
        dao.getTherapiesForUser(userId)

    fun remindersForUser(userId: Int): Flow<List<Reminder>> =
        dao.getRemindersForUser(userId)

    suspend fun insertUser(user: User): Long = dao.insertUser(user)
    suspend fun deleteUser(user: User) = dao.deleteUser(user)

    suspend fun insertMeasurement(measurement: Measurement) =
        dao.insertMeasurement(measurement)

    suspend fun updateMeasurement(measurement: Measurement) =
        dao.updateMeasurement(measurement)

    suspend fun deleteMeasurement(measurement: Measurement) =
        dao.deleteMeasurement(measurement)

    suspend fun insertTherapy(therapy: Therapy) =
        dao.insertTherapy(therapy)

    suspend fun updateTherapy(therapy: Therapy) =
        dao.updateTherapy(therapy)

    suspend fun deleteTherapy(therapy: Therapy) =
        dao.deleteTherapy(therapy)

    suspend fun insertReminder(reminder: Reminder): Long =
        dao.insertReminder(reminder)

    suspend fun updateReminder(reminder: Reminder) =
        dao.updateReminder(reminder)

    suspend fun deleteReminder(reminder: Reminder) =
        dao.deleteReminder(reminder)

    suspend fun getAllUsersOnce(): List<User> = dao.getAllUsersOnce()
    suspend fun getAllMeasurements(): List<Measurement> = dao.getAllMeasurements()
    suspend fun getAllTherapies(): List<Therapy> = dao.getAllTherapies()
    suspend fun getAllReminders(): List<Reminder> = dao.getAllReminders()
}
