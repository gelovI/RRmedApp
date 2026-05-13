package com.example.bloodpressureapp.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.example.bloodpressureapp.data.Reminder
import com.example.bloodpressureapp.receiver.ReminderReceiver
import java.util.Calendar

class ReminderScheduler(private val context: Context) {

    private val alarmManager =
        context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun schedule(reminder: Reminder) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
            !alarmManager.canScheduleExactAlarms()
        ) {
            return
        }

        if (reminder.repeatDaily) {
            scheduleDaily(reminder)
        } else {
            scheduleWeekly(reminder)
        }
    }

    private fun scheduleDaily(reminder: Reminder) {
        val pendingIntent = pendingIntentFor(reminder.id, reminder.message)
        val triggerAt = nextTriggerMillis(reminder.hour, reminder.minute)

        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            triggerAt,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
    }

    private fun scheduleWeekly(reminder: Reminder) {
        val selectedDays = reminder.days
            .split(",")
            .mapNotNull { it.toIntOrNull() }

        selectedDays.forEach { dayOfWeek ->
            val requestCode = reminder.id * 10 + dayOfWeek
            val pendingIntent = pendingIntentFor(requestCode, reminder.message)
            val triggerAt = nextWeeklyTriggerMillis(dayOfWeek, reminder.hour, reminder.minute)

            alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                triggerAt,
                AlarmManager.INTERVAL_DAY * 7,
                pendingIntent
            )
        }
    }

    fun cancel(reminder: Reminder) {
        alarmManager.cancel(pendingIntentFor(reminder.id, reminder.message))

        reminder.days
            .split(",")
            .mapNotNull { it.toIntOrNull() }
            .forEach { dayOfWeek ->
                val requestCode = reminder.id * 10 + dayOfWeek
                alarmManager.cancel(pendingIntentFor(requestCode, reminder.message))
            }
    }

    private fun pendingIntentFor(requestCode: Int, message: String): PendingIntent {
        val intent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra("message", message)
        }

        return PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun nextTriggerMillis(hour: Int, minute: Int): Long {
        return Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)

            if (timeInMillis <= System.currentTimeMillis()) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }.timeInMillis
    }

    private fun nextWeeklyTriggerMillis(dayOfWeek: Int, hour: Int, minute: Int): Long {
        return Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.DAY_OF_WEEK, dayOfWeek)
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)

            if (timeInMillis <= System.currentTimeMillis()) {
                add(Calendar.WEEK_OF_YEAR, 1)
            }
        }.timeInMillis
    }
}
