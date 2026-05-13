package com.example.bloodpressureapp.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bloodpressureapp.data.AppRepository
import com.example.bloodpressureapp.data.Measurement
import com.example.bloodpressureapp.data.Reminder
import com.example.bloodpressureapp.data.Therapy
import com.example.bloodpressureapp.data.User
import com.example.bloodpressureapp.util.ReminderScheduler
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class AppViewModel(
    private val repository: AppRepository,
    private val reminderScheduler: ReminderScheduler
) : ViewModel() {

    val users: StateFlow<List<User>> = repository.users.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        emptyList()
    )

    private val _selectedUser = MutableStateFlow<User?>(null)
    val selectedUser: StateFlow<User?> = _selectedUser.asStateFlow()

    private val selectedUserId = selectedUser
        .map { it?.id }
        .distinctUntilChanged()

    val measurements: StateFlow<List<Measurement>> =
        selectedUserId.flatMapLatest { userId ->
            if (userId == null) {
                flowOf(emptyList())
            } else {
                repository.measurementsForUser(userId)
            }
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            emptyList()
        )

    val therapies: StateFlow<List<Therapy>> =
        selectedUserId.flatMapLatest { userId ->
            if (userId == null) {
                flowOf(emptyList())
            } else {
                repository.therapiesForUser(userId)
            }
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            emptyList()
        )

    val reminders: StateFlow<List<Reminder>> =
        selectedUserId.flatMapLatest { userId ->
            if (userId == null) {
                flowOf(emptyList())
            } else {
                repository.remindersForUser(userId)
            }
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            emptyList()
        )

    fun selectUser(user: User) {
        _selectedUser.value = user
    }

    fun saveUser(name: String) {
        viewModelScope.launch {
            val id = repository.insertUser(User(name = name)).toInt()
            _selectedUser.value = User(id = id, name = name)
        }
    }

    suspend fun saveUserAndReturnId(name: String): Int {
        return repository.insertUser(User(name = name)).toInt()
    }

    fun deleteUser(user: User) {
        viewModelScope.launch {
            repository.deleteUser(user)
            if (_selectedUser.value?.id == user.id) {
                _selectedUser.value = null
            }
        }
    }

    fun saveMeasurement(
        systolic: Int,
        diastolic: Int,
        pulse: Int,
        arrhythmia: Boolean,
        userId: Int,
        timestamp: Long = System.currentTimeMillis()
    ) {
        viewModelScope.launch {
            repository.insertMeasurement(
                Measurement(
                    userId = userId,
                    systolic = systolic,
                    diastolic = diastolic,
                    pulse = pulse,
                    arrhythmia = arrhythmia,
                    timestamp = timestamp
                )
            )
        }
    }

    fun updateMeasurement(measurement: Measurement) {
        viewModelScope.launch {
            repository.updateMeasurement(measurement)
        }
    }

    fun deleteMeasurement(measurement: Measurement) {
        viewModelScope.launch {
            repository.deleteMeasurement(measurement)
        }
    }

    fun deleteMeasurementById(id: Int) {
        val item = measurements.value.firstOrNull { it.id == id } ?: return
        deleteMeasurement(item)
    }

    fun saveTherapy(userId: Int, name: String, dosage: String) {
        viewModelScope.launch {
            repository.insertTherapy(
                Therapy(
                    userId = userId,
                    name = name,
                    dosage = dosage
                )
            )
        }
    }

    fun updateTherapy(therapy: Therapy) {
        viewModelScope.launch {
            repository.updateTherapy(therapy)
        }
    }

    fun deleteTherapy(therapy: Therapy) {
        viewModelScope.launch {
            repository.deleteTherapy(therapy)
        }
    }

    fun addReminder(
        context: Context,
        userId: Int,
        hour: Int,
        minute: Int,
        message: String,
        repeatDaily: Boolean,
        days: String
    ) {
        viewModelScope.launch {
            val reminder = Reminder(
                userId = userId,
                hour = hour,
                minute = minute,
                message = message,
                repeatDaily = repeatDaily,
                days = days
            )

            val id = repository.insertReminder(reminder)
            val savedReminder = reminder.copy(id = id.toInt())

            reminderScheduler.schedule(savedReminder)
        }
    }

    fun updateReminder(reminder: Reminder) {
        viewModelScope.launch {
            repository.updateReminder(reminder)
            reminderScheduler.schedule(reminder)
        }
    }

    fun deleteReminder(reminder: Reminder, context: Context) {
        viewModelScope.launch {
            reminderScheduler.cancel(reminder)
            repository.deleteReminder(reminder)
        }
    }

    fun scheduleReminderAlarm(context: Context, reminder: Reminder) {
        reminderScheduler.schedule(reminder)
    }

    suspend fun getAllUsersOnce(): List<User> = repository.getAllUsersOnce()
    suspend fun getAllMeasurements(): List<Measurement> = repository.getAllMeasurements()
    suspend fun getAllTherapies(): List<Therapy> = repository.getAllTherapies()
    suspend fun getAllReminders(): List<Reminder> = repository.getAllReminders()

    fun loadTherapies(userId: Int) = Unit
    fun loadReminders(userId: Int) = Unit
}
