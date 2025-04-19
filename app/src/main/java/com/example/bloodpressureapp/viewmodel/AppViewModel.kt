package com.example.bloodpressureapp.viewmodel

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bloodpressureapp.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import com.example.bloodpressureapp.data.Reminder
import com.example.bloodpressureapp.receiver.ReminderReceiver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Calendar

class AppViewModel(private val dao: AppDao) : ViewModel() {

    val users = dao.getAllUsers().stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
    private val _measurements = MutableStateFlow<List<Measurement>>(emptyList())
    val measurements: StateFlow<List<Measurement>> = _measurements

    private val _selectedUser = MutableStateFlow<User?>(null)
    val selectedUser: StateFlow<User?> = _selectedUser

    private val _reminders = MutableStateFlow<List<Reminder>>(emptyList())
    val reminders: StateFlow<List<Reminder>> = _reminders.asStateFlow()

    fun selectUser(user: User) {
        _selectedUser.value = user
        loadMeasurements(user.id)
    }

    fun saveUser(name: String) {
        viewModelScope.launch {
            val newUser = User(name = name)
            dao.insertUser(newUser)

            // Benutzerliste neu laden und letzten setzen
            dao.getAllUsers().collect { list ->
                if (list.isNotEmpty()) {
                    val lastUser = list.last()
                    _selectedUser.value = lastUser
                    loadMeasurements(lastUser.id)
                    return@collect
                }
            }
        }
    }

    suspend fun saveUserAndReturnId(name: String): Int {
        return dao.insertUser(User(name = name)).toInt()
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
            dao.insertMeasurement(
                Measurement(
                    userId = userId,
                    systolic = systolic,
                    diastolic = diastolic,
                    pulse = pulse,
                    arrhythmia = arrhythmia,
                    timestamp = timestamp
                )
            )
            loadMeasurements(userId)
        }
    }

    private fun loadMeasurements(userId: Int) {
        viewModelScope.launch {
            dao.getMeasurementsForUser(userId).collect {
                _measurements.value = it
            }
        }
    }

    fun updateMeasurement(measurement: Measurement) {
        viewModelScope.launch {
            dao.updateMeasurement(measurement)
            loadMeasurements(measurement.userId)
        }
    }

    fun deleteMeasurement(measurement: Measurement) {
        viewModelScope.launch {
            dao.deleteMeasurement(measurement)
            loadMeasurements(measurement.userId)
        }
    }


    fun deleteUser(user: User) {
        viewModelScope.launch {
            dao.deleteUser(user)
            _selectedUser.value = null
            _measurements.value = emptyList()
        }
    }

    private val _therapies = MutableStateFlow<List<Therapy>>(emptyList())
    val therapies: StateFlow<List<Therapy>> = _therapies

    fun loadTherapies(userId: Int) {
        viewModelScope.launch {
            dao.getTherapiesForUser(userId).collect {
                _therapies.value = it
            }
        }
    }

    fun saveTherapy(userId: Int, name: String, dosage: String) {
        viewModelScope.launch {
            dao.insertTherapy(Therapy(userId = userId, name = name, dosage = dosage))
            loadTherapies(userId)
        }
    }

    fun updateTherapy(therapy: Therapy) {
        viewModelScope.launch {
            dao.updateTherapy(therapy)
            loadTherapies(therapy.userId)
        }
    }

    fun deleteTherapy(therapy: Therapy) {
        viewModelScope.launch {
            dao.deleteTherapy(therapy)
            loadTherapies(therapy.userId)
        }
    }

    suspend fun getAllUsersOnce(): List<User> = dao.getAllUsersOnce()
    suspend fun getAllMeasurements(): List<Measurement> = dao.getAllMeasurements()
    suspend fun getAllTherapies(): List<Therapy> = dao.getAllTherapies()
    suspend fun getAllReminders(): List<Reminder> = dao.getAllReminders()


    fun loadReminders(userId: Int) {
        viewModelScope.launch {
            dao.getRemindersForUser(userId).collect {
                _reminders.value = it
            }
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

            // Reminder speichern und ID zurückbekommen
            val id = dao.insertReminder(reminder)

            // Reminder mit korrekter ID kopieren
            val savedReminder = reminder.copy(id = id.toInt())

            // Liste neu laden
            loadReminders(userId)

            // Alarm für Reminder setzen
            scheduleReminderAlarm(context, savedReminder)
        }
    }

    fun updateReminder(reminder: Reminder) {
        viewModelScope.launch {
            dao.updateReminder(reminder)
            loadReminders(reminder.userId)
        }
    }

    private fun cancelReminderAlarm(context: Context, reminder: Reminder) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            reminder.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }

    fun deleteReminder(reminder: Reminder, context: Context) {
        viewModelScope.launch {
            cancelReminderAlarm(context, reminder) // Alarm abbrechen
            dao.deleteReminder(reminder)
            loadReminders(reminder.userId)
        }
    }

    fun scheduleReminderAlarm(context: Context, reminder: Reminder) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                return
            }
        }
        val intent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra("message", reminder.message)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            reminder.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, reminder.hour)
            set(Calendar.MINUTE, reminder.minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        if (reminder.repeatDaily) {
            alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                AlarmManager.INTERVAL_DAY,
                pendingIntent
            )
        } else {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
        }
    }
}
