package com.example.bloodpressureapp.ui.theme

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bloodpressureapp.R
import com.example.bloodpressureapp.data.Reminder
import com.example.bloodpressureapp.ui.components.ReminderCardContent
import com.example.bloodpressureapp.ui.components.SwipeableCard
import com.example.bloodpressureapp.viewmodel.AppViewModel
import com.example.bloodpressureapp.ui.components.TimePickerDialogSpinner
import java.util.Locale

@Composable
fun ReminderScreen(viewModel: AppViewModel, userId: Int) {
    val reminders by viewModel.reminders.collectAsState()

    var hour by remember { mutableIntStateOf(8) }
    var minute by remember { mutableIntStateOf(0) }
    var message by remember { mutableStateOf("") }
    var repeatDaily by remember { mutableStateOf(true) }
    var showTimePicker by remember { mutableStateOf(false) }
    val context = LocalContext.current
    var editMode by remember { mutableStateOf<Reminder?>(null) }
    val showDeleteDialog = remember { mutableStateOf<Reminder?>(null) }

    val daysOfWeek = listOf(
        stringResource(R.string.monday_short),
        stringResource(R.string.tuesday_short),
        stringResource(R.string.wednesday_short),
        stringResource(R.string.thursday_short),
        stringResource(R.string.friday_short),
        stringResource(R.string.saturday_short),
        stringResource(R.string.sunday_short)
    )
    val selectedDays = remember {
        mutableStateMapOf<String, Boolean>().apply {
            daysOfWeek.forEach { this[it] = false }
        }
    }

    val revealedStates = remember { mutableStateMapOf<Int, Boolean>() }

    LaunchedEffect(userId) {
        viewModel.loadReminders(userId)
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {
        Text(stringResource(R.string.add_reminder), style = MaterialTheme.typography.h6)
        Spacer(modifier = Modifier.height(8.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(String.format(Locale.getDefault(), "%02d:%02d", hour, minute), fontSize = 14.sp)
            Spacer(modifier = Modifier.width(8.dp))
            TextButton(onClick = { showTimePicker = true }) {
                Text(stringResource(R.string.select_time), fontSize = 12.sp)
            }
        }

        OutlinedTextField(
            value = message,
            onValueChange = { message = it },
            label = { Text(stringResource(R.string.reminder_message)) },
            modifier = Modifier.fillMaxWidth()
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = repeatDaily, onCheckedChange = { repeatDaily = it })
            Text(stringResource(R.string.repeat_daily), fontSize = 12.sp)
        }

        if (!repeatDaily) {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                daysOfWeek.forEach { day ->
                    val isSelected = selectedDays[day] == true
                    Button(
                        onClick = { selectedDays[day] = !isSelected },
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = if (isSelected) MaterialTheme.colors.primary else Color.LightGray
                        ),
                        modifier = Modifier.size(36.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(day, fontSize = 12.sp, color = Color.White)
                    }
                }
            }
        }

        Button(
            onClick = {
                val daysString = selectedDays.filterValues { it }.keys.joinToString(",")
                if (message.isNotBlank()) {
                    if (editMode != null) {
                        val updated = editMode!!.copy(
                            hour = hour,
                            minute = minute,
                            message = message,
                            repeatDaily = repeatDaily,
                            days = daysString
                        )
                        viewModel.updateReminder(updated)
                        viewModel.scheduleReminderAlarm(context, updated)
                        editMode = null
                    } else {
                        viewModel.addReminder(
                            context,
                            userId,
                            hour,
                            minute,
                            message,
                            repeatDaily,
                            daysString
                        )
                    }

                    // Reset
                    message = ""
                    selectedDays.keys.forEach { selectedDays[it] = false }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (editMode != null) stringResource(R.string.updateReminder) else stringResource(R.string.save_reminder))
        }

        Spacer(modifier = Modifier.height(16.dp))
        Divider()
        Spacer(modifier = Modifier.height(8.dp))
        Text(stringResource(R.string.your_reminders), style = MaterialTheme.typography.subtitle1)

        LazyColumn {
            items(reminders, key = { it.id }) { reminder ->
                val isRevealed = revealedStates[reminder.id] ?: false

                SwipeableCard(
                    isRevealed = isRevealed,
                    onReveal = { revealedStates[reminder.id] = true },
                    onReset = { revealedStates[reminder.id] = false },
                    onEdit = {
                        hour = reminder.hour
                        minute = reminder.minute
                        message = reminder.message
                        repeatDaily = reminder.repeatDaily
                        selectedDays.clear()
                        reminder.days.split(",").forEach { day -> selectedDays[day] = true }
                        editMode = reminder
                    },
                    onDelete = {
                        showDeleteDialog.value = reminder
                        revealedStates.remove(reminder.id) // Zustand zurücksetzen
                    }
                ) {
                    ReminderCardContent(
                        time = String.format(Locale.getDefault(), "%02d:%02d", reminder.hour, reminder.minute),
                        message = reminder.message,
                        repeatInfo = if (reminder.repeatDaily)
                            stringResource(R.string.repeats_daily)
                        else
                            "📅 Aktiv an: ${reminder.days}"
                    )
                }
            }
        }
    }

    if (showTimePicker) {
        TimePickerDialogSpinner(
            initialHour = hour,
            initialMinute = minute,
            onTimeSelected = { h, m ->
                hour = h
                minute = m
                showTimePicker = false
            },
            onDismiss = { showTimePicker = false }
        )
    }

    showDeleteDialog.value?.let { reminderToDelete ->
        AlertDialog(
            onDismissRequest = { showDeleteDialog.value = null },
            title = { Text("Erinnerung löschen") },
            text = { Text("Möchtest du diese Erinnerung wirklich löschen?", fontSize = 11.sp) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteReminder(reminderToDelete, context)
                    showDeleteDialog.value = null
                }) {
                    Text("Löschen", color = MaterialTheme.colors.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog.value = null }) {
                    Text("Abbrechen")
                }
            }
        )
    }
}