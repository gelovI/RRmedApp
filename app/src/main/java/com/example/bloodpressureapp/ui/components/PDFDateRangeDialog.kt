package com.example.bloodpressureapp.ui.components

import android.app.DatePickerDialog
import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

    val dateFormat = remember { SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()) }

    val isRangeSelected = start != null && end != null
    val isRangeValid = isRangeSelected && !end!!.before(start)

    AlertDialog(
        onDismissRequest = onCancel,
        title = {
            Text(
                text = stringResource(R.string.date_range),
                fontSize = 15.sp,
                style = MaterialTheme.typography.h6
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {

                Text(
                    text = stringResource(R.string.pdf_range_hint),
                    fontSize = 12.sp,
                    style = MaterialTheme.typography.body2
                )

                // Start-Datum
                OutlinedButton(
                    onClick = {
                        showDatePicker(
                            context = context,
                            initial = start
                        ) { selected -> start = selected }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = null,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(
                        text = start?.let { "${stringResource(R.string.start_date)}: ${dateFormat.format(it)}" }
                            ?: stringResource(R.string.select_start_date),
                        fontSize = 12.sp
                    )
                }

                // End-Datum
                OutlinedButton(
                    onClick = {
                        showDatePicker(
                            context = context,
                            initial = end ?: start
                        ) { selected -> end = selected }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = null,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(
                        text = end?.let { "${stringResource(R.string.end_date)}: ${dateFormat.format(it)}" }
                            ?: stringResource(R.string.select_end_date),
                        fontSize = 12.sp
                    )
                }

                // Zusammenfassung / Fehler
                if (isRangeSelected) {
                    if (isRangeValid) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(2.dp),
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
                            Text(
                                text = stringResource(
                                    R.string.pdf_range_summary,
                                    dateFormat.format(start!!),
                                    dateFormat.format(end!!)
                                ),
                                style = MaterialTheme.typography.body2
                            )
                        }
                    } else {
                        Text(
                            text = stringResource(R.string.invalid_date_range),
                            style = MaterialTheme.typography.body2,
                            color = MaterialTheme.colors.error
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                enabled = isRangeValid,
                onClick = {
                    if (isRangeValid) {
                        onConfirm(start!!, end!!)
                    }
                }
            ) {
                Text(stringResource(R.string.PDF_gen), fontSize = 12.sp)
            }
        },
        dismissButton = {
            TextButton(onClick = onCancel) {
                Text(stringResource(R.string.cancel), fontSize = 12.sp)
            }
        }
    )
}

fun showDatePicker(
    context: Context,
    initial: Date? = null,
    onDateSelected: (Date) -> Unit
) {
    val calendar = Calendar.getInstance().apply {
        time = initial ?: Date()
    }

    DatePickerDialog(
        context,
        { _, year, month, day ->
            calendar.set(year, month, day, 0, 0, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            onDateSelected(calendar.time)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    ).show()
}