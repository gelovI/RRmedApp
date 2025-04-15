package com.example.bloodpressureapp.ui.theme

import com.example.bloodpressureapp.util.generateMeasurementPdf
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bloodpressureapp.viewmodel.AppViewModel
import java.text.SimpleDateFormat
import java.util.*
import com.example.bloodpressureapp.ui.components.PDFDateRangeDialog
import com.example.bloodpressureapp.data.Measurement
import com.example.bloodpressureapp.ui.components.EditMeasurementDialog
import androidx.compose.ui.res.stringResource
import com.example.bloodpressureapp.R
import com.example.bloodpressureapp.ui.components.SwipeableCard

@Composable
fun OverviewScreen(viewModel: AppViewModel) {
    val measurements by viewModel.measurements.collectAsState()
    val context = LocalContext.current
    var showEditDialog by remember { mutableStateOf<Measurement?>(null) }
    var showDeleteDialog by remember { mutableStateOf<Measurement?>(null) }

    val user = viewModel.selectedUser.collectAsState().value

    val grouped = measurements.groupBy {
        SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date(it.timestamp))
    }

    var showDateDialog by remember { mutableStateOf(false) }
    var startDate by remember { mutableStateOf<Date?>(null) }
    var endDate by remember { mutableStateOf<Date?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(stringResource(R.string.overview_title), style = MaterialTheme.typography.h6)
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { showDateDialog = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.overview_export_pdf), fontSize = 12.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            contentPadding = PaddingValues(bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            grouped.forEach { (date, items) ->
                item {
                    Text(
                        text = "📅 $date",
                        fontSize = 14.sp,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
                items(items) { measurement ->
                    val time = SimpleDateFormat(
                        "HH:mm",
                        Locale.getDefault()
                    ).format(Date(measurement.timestamp))
                    SwipeableCard(
                        onEdit = { showEditDialog = measurement },
                        onDelete = { showDeleteDialog = measurement }
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("🕒 $time", fontSize = 12.sp)
                            Text(
                                "${stringResource(R.string.overview_systolic)}: ${measurement.systolic}",
                                fontSize = 12.sp
                            )
                            Text(
                                "${stringResource(R.string.overview_diastolic)}: ${measurement.diastolic}",
                                fontSize = 12.sp
                            )
                            Text(
                                "${stringResource(R.string.overview_pulse)}: ${measurement.pulse}",
                                fontSize = 12.sp
                            )
                            Text(
                                "${stringResource(R.string.overview_arrhythmia)}: ${
                                    if (measurement.arrhythmia) stringResource(
                                        R.string.overview_yes
                                    ) else stringResource(R.string.overview_no)
                                }", fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }
    }

    if (showDateDialog) {
        PDFDateRangeDialog(
            context = context,
            onCancel = { showDateDialog = false },
            onConfirm = { start, end ->
                startDate = start
                endDate = end
                showDateDialog = false

                val pdfFile = generateMeasurementPdf(measurements, start, end, user!!)
                Toast.makeText(
                    context,
                    pdfFile?.let { "${context.getString(R.string.overview_pdf_saved)}: ${it.name}" }
                        ?: context.getString(R.string.overview_no_data),
                    Toast.LENGTH_LONG
                ).show()
            }
        )
    }

    showEditDialog?.let { measurement ->
        EditMeasurementDialog(
            initial = measurement,
            onDismiss = { showEditDialog = null },
            onSave = { updated ->
                viewModel.updateMeasurement(updated)
                showEditDialog = null
            }
        )
    }

    showDeleteDialog?.let { measurement ->
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = { Text(stringResource(R.string.overview_delete_title)) },
            text = { Text(stringResource(R.string.overview_delete_confirm)) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteMeasurement(measurement)
                    showDeleteDialog = null
                }) {
                    Text(
                        stringResource(R.string.overview_delete),
                        color = MaterialTheme.colors.error
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = null }) {
                    Text(stringResource(R.string.overview_cancel))
                }
            }
        )
    }
}
