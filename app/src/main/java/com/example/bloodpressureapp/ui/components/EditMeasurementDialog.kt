package com.example.bloodpressureapp.ui.components

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.bloodpressureapp.R
import com.example.bloodpressureapp.data.Measurement
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun EditMeasurementDialog(
    initial: Measurement,
    onDismiss: () -> Unit,
    onSave: (Measurement) -> Unit
) {
    val context = LocalContext.current

    // Zahlenfelder
    var systolic by remember { mutableStateOf(initial.systolic.toString()) }
    var diastolic by remember { mutableStateOf(initial.diastolic.toString()) }
    var pulse by remember { mutableStateOf(initial.pulse.toString()) }
    var arrhythmia by remember { mutableStateOf(initial.arrhythmia) }

    // Datum/Zeit-State aus initial.timestamp
    val calInit = remember(initial.timestamp) {
        Calendar.getInstance().apply { timeInMillis = initial.timestamp }
    }
    var year   by remember { mutableIntStateOf(calInit.get(Calendar.YEAR)) }
    var month  by remember { mutableIntStateOf(calInit.get(Calendar.MONTH)) }          // 0..11
    var day    by remember { mutableIntStateOf(calInit.get(Calendar.DAY_OF_MONTH)) }
    var hour   by remember { mutableIntStateOf(calInit.get(Calendar.HOUR_OF_DAY)) }
    var minute by remember { mutableIntStateOf(calInit.get(Calendar.MINUTE)) }

    // Anzeigeformate
    val dateFmt = remember { SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()) }
    val timeFmt = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }

    fun currentDateString(): String {
        val c = Calendar.getInstance().apply { set(year, month, day) }
        return dateFmt.format(c.time)
    }
    fun currentTimeString(): String {
        val c = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
        }
        return timeFmt.format(c.time)
    }

    // Validierung
    val isValidInput = systolic.toIntOrNull() in 40..250 &&
            diastolic.toIntOrNull() in 30..150 &&
            pulse.toIntOrNull() in 30..200

    val buttonWidth = 220.dp
    val fieldWidth  = 320.dp

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.edit_measurement)) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // --- Datum: eigene Zeile, zentriert ---
                OutlinedButton(
                    onClick = {
                        DatePickerDialog(
                            context,
                            { _, y, m, d -> year = y; month = m; day = d },
                            year, month, day
                        ).show()
                    },
                    modifier = Modifier.widthIn(max = buttonWidth)
                ) {
                    Text(text = currentDateString())
                }

                Spacer(Modifier.height(10.dp))

                // --- Uhrzeit: eigene Zeile, zentriert ---
                OutlinedButton(
                    onClick = {
                        TimePickerDialog(
                            context,
                            { _, h, min -> hour = h; minute = min },
                            hour, minute, true
                        ).show()
                    },
                    modifier = Modifier.widthIn(max = buttonWidth)
                ) {
                    Text(text = currentTimeString())
                }

                Spacer(Modifier.height(16.dp))

                // --- Felder: untereinander & zentriert ---
                OutlinedTextField(
                    value = systolic,
                    onValueChange = { systolic = it },
                    label = { Text(stringResource(R.string.systolic)) },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    isError = systolic.isNotBlank() && systolic.toIntOrNull() !in 40..250,
                    singleLine = true,
                    modifier = Modifier.widthIn(max = fieldWidth)
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = diastolic,
                    onValueChange = { diastolic = it },
                    label = { Text(stringResource(R.string.diastolic)) },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    isError = diastolic.isNotBlank() && diastolic.toIntOrNull() !in 30..150,
                    singleLine = true,
                    modifier = Modifier.widthIn(max = fieldWidth)
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = pulse,
                    onValueChange = { pulse = it },
                    label = { Text(stringResource(R.string.pulse)) },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    isError = pulse.isNotBlank() && pulse.toIntOrNull() !in 30..200,
                    singleLine = true,
                    modifier = Modifier.widthIn(max = fieldWidth)
                )

                Spacer(Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.widthIn(max = fieldWidth)
                ) {
                    Checkbox(checked = arrhythmia, onCheckedChange = { arrhythmia = it })
                    Spacer(Modifier.width(6.dp))
                    Text(stringResource(R.string.arrhythmia))
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val calSave = Calendar.getInstance().apply {
                        set(Calendar.YEAR, year)
                        set(Calendar.MONTH, month)
                        set(Calendar.DAY_OF_MONTH, day)
                        set(Calendar.HOUR_OF_DAY, hour)
                        set(Calendar.MINUTE, minute)
                        set(Calendar.SECOND, 0)
                        set(Calendar.MILLISECOND, 0)
                    }
                    onSave(
                        initial.copy(
                            systolic = systolic.toInt(),
                            diastolic = diastolic.toInt(),
                            pulse = pulse.toInt(),
                            arrhythmia = arrhythmia,
                            timestamp = calSave.timeInMillis
                        )
                    )
                    onDismiss()
                },
                enabled = isValidInput
            ) { Text(stringResource(R.string.save)) }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel)) } }
    )
}