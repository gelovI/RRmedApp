package com.example.bloodpressureapp.ui.components

import android.app.DatePickerDialog
import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.bloodpressureapp.R
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun PDFDateRangeDialog(
    context: Context,
    onCancel: () -> Unit,
    onConfirm: (Date, Date) -> Unit
) {
    var start by remember { mutableStateOf<Date?>(null) }
    var end by remember { mutableStateOf<Date?>(null) }

    AlertDialog(
        onDismissRequest = onCancel,
        title = { stringResource(R.string.date_range) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(onClick = {
                    showDatePicker(context) { selected ->
                        start = selected
                    }
                }) {
                    Text(stringResource(R.string.start_date))
                }

                Button(onClick = {
                    showDatePicker(context) { selected ->
                        end = selected
                    }
                }) {
                    Text(stringResource(R.string.end_date))
                }

                if (start != null && end != null) {
                    Text("${stringResource(R.string.from)} ${formatDate(start!!)}\n🗓 ${stringResource(R.string.to)} ${formatDate(end!!)}")
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (start != null && end != null) {
                        onConfirm(start!!, end!!)
                    }
                }
            ) {
                Text(stringResource(R.string.PDF_gen))
            }
        },
        dismissButton = {
            TextButton(onClick = onCancel) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}

fun showDatePicker(context: Context, onDateSelected: (Date) -> Unit) {
    val calendar = Calendar.getInstance()
    DatePickerDialog(
        context,
        { _, year, month, day ->
            calendar.set(year, month, day, 0, 0)
            onDateSelected(calendar.time)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    ).show()
}

fun formatDate(date: Date): String {
    val sdf = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    return sdf.format(date)
}
