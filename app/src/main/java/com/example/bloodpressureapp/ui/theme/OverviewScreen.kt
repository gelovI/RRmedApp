package com.example.bloodpressureapp.ui.theme

import com.example.bloodpressureapp.util.generateMeasurementPdf
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PictureAsPdf
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
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
import com.example.bloodpressureapp.ui.components.DateGroupBox
import com.example.bloodpressureapp.ui.components.MeasurementCardContent
import com.example.bloodpressureapp.ui.components.QuickAnalysisDialog
import com.example.bloodpressureapp.ui.components.SwipeableCard

@Composable
fun OverviewScreen(viewModel: AppViewModel) {
    val measurements by viewModel.measurements.collectAsState()
    val context = LocalContext.current
    var showEditDialog by remember { mutableStateOf<Measurement?>(null) }
    var showDeleteDialog by remember { mutableStateOf<Measurement?>(null) }

    val user = viewModel.selectedUser.collectAsState().value

    val revealedStates = remember { mutableStateMapOf<Int, Boolean>() }

    val grouped = remember(measurements) {
        measurements.groupBy {
            SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date(it.timestamp))
        }
    }

    var showDateDialog by remember { mutableStateOf(false) }
    var startDate by remember { mutableStateOf<Date?>(null) }
    var endDate by remember { mutableStateOf<Date?>(null) }

    val configuration = LocalConfiguration.current
    val screenWidthDp = configuration.screenWidthDp
    val narrow = screenWidthDp < 420
    val fontSize = if (screenWidthDp >= 720) 20.sp else 15.sp

    // ---- Mehrfachauswahl-Status ----
    val selectedIds = remember { mutableStateMapOf<Int, Boolean>() }
    var selectAll by remember { mutableStateOf(false) }
    var showConfirmBulkDelete by remember { mutableStateOf(false) }

    // Liste verändert? Auswahl bereinigen + ggf. auffüllen
    LaunchedEffect(measurements) {
        // nur noch IDs behalten, die es noch gibt
        val valid = measurements.map { it.id }.toSet()
        selectedIds.keys.retainAll(valid)

        // wenn "Select All" aktiv ist, (re)alle markieren
        if (selectAll) {
            selectedIds.clear()
            measurements.forEach { selectedIds[it.id] = true }
        }
    }

    // Header-Handler: explizites (De)Selektieren steuern
    val onHeaderSelectAll: (Boolean) -> Unit = { checked ->
        selectAll = checked
        if (checked) {
            selectedIds.clear()
            measurements.forEach { selectedIds[it.id] = true }
        } else {
            // bewusst ALLES leeren (Header abgewählt = keine Auswahl)
            selectedIds.clear()
        }
    }

    // Einzel-Toggle: Header-Checkbox automatisch aktualisieren
    fun toggleSelect(id: Int, checked: Boolean) {
        if (checked) selectedIds[id] = true else selectedIds.remove(id)
        selectAll = measurements.isNotEmpty() && selectedIds.size == measurements.size
    }

    val selectionMode by remember { derivedStateOf { selectAll || selectedIds.isNotEmpty() } }


    fun isSelected(id: Int) = selectedIds.containsKey(id)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(
                text = stringResource(R.string.overview_title),
                fontSize = fontSize,
                modifier = Modifier.align(Alignment.Center)
            )

            IconButton(
                onClick = { showDateDialog = true },
                modifier = Modifier.align(Alignment.CenterEnd)
            ) {
                Icon(
                    imageVector = Icons.Outlined.PictureAsPdf,
                    contentDescription = stringResource(R.string.overview_export_pdf)
                )
            }
        }

        Spacer(Modifier.height(8.dp))
        Divider()

        if (narrow) {
            Column(Modifier.fillMaxWidth().padding(vertical = 10.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = selectAll, onCheckedChange = onHeaderSelectAll)
                    Spacer(Modifier.width(8.dp))
                    Text(stringResource(R.string.select_all))
                    if (selectedIds.isNotEmpty()) {
                        Spacer(Modifier.width(12.dp)); Text("• ${selectedIds.size}")
                    }
                }
                Spacer(Modifier.height(8.dp))

                if (selectionMode) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                        Button(
                            onClick = { showConfirmBulkDelete = true },
                            enabled = selectedIds.isNotEmpty(),
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = if (selectedIds.isNotEmpty())
                                    MaterialTheme.colors.error
                                else
                                    MaterialTheme.colors.onSurface.copy(alpha = 0.08f),
                                contentColor = MaterialTheme.colors.onPrimary
                            )
                        ) { Text(stringResource(R.string.delete_selected)) }
                    }
                }
            }
        } else {
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = selectAll, onCheckedChange = onHeaderSelectAll)
                    Spacer(Modifier.width(8.dp))
                    Text(stringResource(R.string.select_all))
                    if (selectedIds.isNotEmpty()) {
                        Spacer(Modifier.width(12.dp)); Text("• ${selectedIds.size}")
                    }
                }

                if (selectionMode) {
                    AnimatedVisibility(visible = selectionMode) {
                        Button(
                            onClick = { showConfirmBulkDelete = true },
                            enabled = selectedIds.isNotEmpty(),
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = if (selectedIds.isNotEmpty())
                                    MaterialTheme.colors.error
                                else
                                    MaterialTheme.colors.onSurface.copy(alpha = 0.08f),
                                contentColor = MaterialTheme.colors.onPrimary
                            )
                        ) { Text(stringResource(R.string.delete_selected)) }
                    }
                }
            }
        }
        Divider()
        Spacer(Modifier.height(8.dp))

        LazyColumn(
            contentPadding = PaddingValues(bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            grouped.forEach { (date, itemsForDate) ->
                // 📅 Datum als eigenes Element
                item {
                    DateGroupBox(date = date)
                }

                // Alle Messungen des Datums
                items(itemsForDate, key = { it.id }) { measurement ->
                    val time = SimpleDateFormat(
                        "HH:mm",
                        Locale.getDefault()
                    ).format(Date(measurement.timestamp))
                    var showQuickAnalysis by remember { mutableStateOf(false) }
                    val isRevealed = revealedStates[measurement.id] ?: false

                    val content: @Composable () -> Unit = {
                        MeasurementCardContent(
                            time = time,
                            systolic = measurement.systolic,
                            diastolic = measurement.diastolic,
                            pulse = measurement.pulse,
                            arrhythmia = measurement.arrhythmia,
                            onInfoClick = { showQuickAnalysis = true },
                            showCheckbox = selectionMode,
                            checked = isSelected(measurement.id),
                            onCheckedChange = {checked -> toggleSelect(measurement.id, checked)}
                        )
                    }

                    if (selectionMode) {
                        // Im Selektionsmodus kein Swipe, nur Card anzeigen
                        Card { content() }
                    } else {
                        SwipeableCard(
                            isRevealed = isRevealed,
                            onReveal = { revealedStates[measurement.id] = true },
                            onReset = { revealedStates[measurement.id] = false },
                            onEdit = { showEditDialog = measurement },
                            onDelete = {
                                showDeleteDialog = measurement
                                revealedStates.remove(measurement.id)
                            }
                        ) { content() }
                    }

                    if (showQuickAnalysis) {
                        QuickAnalysisDialog(
                            context = context,
                            systolic = measurement.systolic,
                            diastolic = measurement.diastolic,
                            pulse = measurement.pulse,
                            onDismiss = { showQuickAnalysis = false }
                        )
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

                val u = user
                if (u == null) {
                    Toast.makeText(context, R.string.overview_no_user, Toast.LENGTH_LONG).show()
                    return@PDFDateRangeDialog
                }

                val pdfFile = generateMeasurementPdf(context, measurements, start, end, u)
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

    // Sammel-Löschdialog
    if (showConfirmBulkDelete) {
        val count = selectedIds.size
        AlertDialog(
            onDismissRequest = { showConfirmBulkDelete = false },
            title = { Text(stringResource(R.string.delete_selected_confirm_title)) },
            text  = { Text(stringResource(R.string.delete_selected_confirm_msg, count)) },
            confirmButton = {
                TextButton(onClick = {
                    val ids = selectedIds.keys.toList()
                    // Falls du keine Batch-Methode hast, einzeln löschen:
                    ids.forEach { id -> viewModel.deleteMeasurementById(id) }
                    // Oder, wenn vorhanden:
                    // viewModel.deleteMeasurements(ids)

                    showConfirmBulkDelete = false
                    selectedIds.clear()
                    selectAll = false
                }) {
                    Text(stringResource(R.string.delete), color = MaterialTheme.colors.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmBulkDelete = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}
