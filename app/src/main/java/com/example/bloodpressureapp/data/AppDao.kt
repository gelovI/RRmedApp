package com.example.bloodpressureapp.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {
    @Insert suspend fun insertUser(user: User): Long
    @Insert suspend fun insertMeasurement(measurement: Measurement)

    @Query("SELECT * FROM measurements WHERE userId = :userId ORDER BY timestamp DESC")
    fun getMeasurementsForUser(userId: Int): Flow<List<Measurement>>

    @Query("SELECT * FROM users")
    fun getAllUsers(): Flow<List<User>>

    @Delete
    suspend fun deleteUser(user: User)

    @Update
    suspend fun updateMeasurement(measurement: Measurement)

    @Delete
    suspend fun deleteMeasurement(measurement: Measurement)

    @Insert
    suspend fun insertTherapy(therapy: Therapy)

    @Update
    suspend fun updateTherapy(therapy: Therapy)

    @Delete
    suspend fun deleteTherapy(therapy: Therapy)

    @Query("SELECT * FROM users")
    suspend fun getAllUsersOnce(): List<User>

    @Query("SELECT * FROM measurements")
    suspend fun getAllMeasurements(): List<Measurement>

    @Query("SELECT * FROM therapy")
    suspend fun getAllTherapies(): List<Therapy>

    @Query("SELECT * FROM reminders")
    suspend fun getAllReminders(): List<Reminder>


    @Query("SELECT * FROM reminders WHERE userId = :userId")
    fun getRemindersForUser(userId: Int): Flow<List<Reminder>>

    @Query("SELECT * FROM therapy WHERE userId = :userId")
    fun getTherapiesForUser(userId: Int): Flow<List<Therapy>>

    @Insert
    suspend fun insertReminder(reminder: Reminder): Long

    @Update
    suspend fun updateReminder(reminder: Reminder)

    @Delete
    suspend fun deleteReminder(reminder: Reminder)
}
