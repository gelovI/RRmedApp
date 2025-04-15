package com.example.bloodpressureapp.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.bloodpressureapp.R
import com.example.bloodpressureapp.data.Measurement

@Composable
fun EditMeasurementDialog(
    initial: Measurement,
    onDismiss: () -> Unit,
    onSave: (Measurement) -> Unit
) {
    var systolic by remember { mutableStateOf(initial.systolic.toString()) }
    var diastolic by remember { mutableStateOf(initial.diastolic.toString()) }
    var pulse by remember { mutableStateOf(initial.pulse.toString()) }
    var arrhythmia by remember { mutableStateOf(initial.arrhythmia) }

    val isValidInput = systolic.toIntOrNull() in 40..250 &&
            diastolic.toIntOrNull() in 30..150 &&
            pulse.toIntOrNull() in 30..200

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.edit_measurement)) },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = systolic,
                    onValueChange = { systolic = it },
                    label = { Text(stringResource(R.string.systolic)) },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    isError = systolic.isNotBlank() && systolic.toIntOrNull() !in 40..250
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = diastolic,
                    onValueChange = { diastolic = it },
                    label = { Text(stringResource(R.string.diastolic)) },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    isError = diastolic.isNotBlank() && diastolic.toIntOrNull() !in 30..150
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = pulse,
                    onValueChange = { pulse = it },
                    label = { Text(stringResource(R.string.pulse)) },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    isError = pulse.isNotBlank() && pulse.toIntOrNull() !in 30..200
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = arrhythmia, onCheckedChange = { arrhythmia = it })
                    Text(stringResource(R.string.arrhythmia))
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onSave(
                        initial.copy(
                            systolic = systolic.toInt(),
                            diastolic = diastolic.toInt(),
                            pulse = pulse.toInt(),
                            arrhythmia = arrhythmia
                        )
                    )
                    onDismiss()
                },
                enabled = isValidInput
            ) {
                Text(stringResource(R.string.save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}