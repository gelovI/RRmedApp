package com.example.bloodpressureapp.ui.theme

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.bloodpressureapp.data.Therapy
import com.example.bloodpressureapp.viewmodel.AppViewModel
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import com.example.bloodpressureapp.R
import com.example.bloodpressureapp.ui.components.SwipeableCard
import com.example.bloodpressureapp.ui.components.TherapyFormSection

@Composable
fun TherapyScreen(viewModel: AppViewModel, userId: Int) {
    val therapies by viewModel.therapies.collectAsState()
    var name by remember { mutableStateOf("") }
    var dosage by remember { mutableStateOf("") }
    var editMode by remember { mutableStateOf<Therapy?>(null) }
    var showDeleteDialog by remember { mutableStateOf<Therapy?>(null) }

    LaunchedEffect(userId) {
        viewModel.loadTherapies(userId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        TherapyFormSection(
            viewModel = viewModel,
            userId = userId,
            editMode = editMode,
            setEditMode = { editMode = it },
            name = name,
            setName = { name = it },
            dosage = dosage,
            setDosage = { dosage = it }
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(stringResource(R.string.current_therapies), fontSize = 12.sp)
        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn {
            items(therapies) { therapy ->
                SwipeableCard(
                    onEdit = {
                        editMode = therapy
                        name = therapy.name
                        dosage = therapy.dosage
                    },
                    onDelete = { showDeleteDialog = therapy }
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("💊 ${therapy.name}", fontSize = 11.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            "\uD83E\uDDEA ${stringResource(R.string.dosage)}: ${therapy.dosage}",
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        // 🗑️ Bestätigungsdialog zum Löschen
        if (showDeleteDialog != null) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = null },
                title = { Text(stringResource(R.string.confirm_delete_title)) },
                text = {
                    Text(
                        stringResource(
                            R.string.confirm_delete_therapy,
                            showDeleteDialog?.name ?: ""
                        )
                    )
                },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.deleteTherapy(showDeleteDialog!!)
                        showDeleteDialog = null
                    }) {
                        Text(stringResource(R.string.delete), color = MaterialTheme.colors.error)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = null }) {
                        Text(stringResource(R.string.cancel))
                    }
                }
            )
        }
    }
}
