package com.example.bloodpressureapp.ui.components

import android.widget.TimePicker
import androidx.compose.foundation.layout.*
import androidx.compose.material.AlertDialog
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidView
import com.example.bloodpressureapp.R

@Composable
fun TimePickerDialogSpinner(
    initialHour: Int,
    initialMinute: Int,
    onTimeSelected: (hour: Int, minute: Int) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var selectedHour by remember { mutableIntStateOf(initialHour) }
    var selectedMinute by remember { mutableIntStateOf(initialMinute) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(R.string.select_time), style = MaterialTheme.typography.h6) },
        text = {
            AndroidView(
                factory = {
                    TimePicker(context).apply {
                        setIs24HourView(true)
                        hour = selectedHour
                        minute = selectedMinute
                        setOnTimeChangedListener { _, h, m ->
                            selectedHour = h
                            selectedMinute = m
                        }
                    }
                },
                modifier = Modifier.wrapContentSize()
            )
        },
        confirmButton = {
            TextButton(onClick = {
                onTimeSelected(selectedHour, selectedMinute)
                onDismiss()
            }) {
                Text(stringResource(R.string.confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}
