package com.example.bloodpressureapp.ui.components

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import com.example.bloodpressureapp.R
import com.example.bloodpressureapp.util.importDataForSelectedUsers
import com.example.bloodpressureapp.util.peekUsersInBackup
import com.example.bloodpressureapp.viewmodel.AppViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun ImportDataButton(viewModel: AppViewModel, onFinished: () -> Unit) {
    val context = LocalContext.current
    var showUserDialog by remember { mutableStateOf(false) }
    var jsonContent by remember { mutableStateOf<String?>(null) }

    // Dateiauswahl-Launcher
    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            val inputStream = context.contentResolver.openInputStream(uri)
            val json = inputStream?.bufferedReader().use { it?.readText() }
            if (!json.isNullOrBlank()) {
                jsonContent = json
                showUserDialog = true
            }
        }
    }

    // Zeige Dialog nur, wenn JSON vorhanden ist
    if (showUserDialog && jsonContent != null) {
        val previews = remember(jsonContent) { peekUsersInBackup(jsonContent!!) }

        if (previews.isNotEmpty()) {
            UserMultiSelectDialog(
                title = stringResource(R.string.select_users_import),
                items = previews,
                idOf = { it.id }, // Backup-IDs!
                labelOf = { it.name },
                extraOf = { "${it.measurementCount} ${context.getString(R.string.measurements)}" },
                onDismiss = { showUserDialog = false },
                onConfirm = { selectedBackupUserIds ->
                    showUserDialog = false
                    CoroutineScope(Dispatchers.Main).launch {
                        importDataForSelectedUsers(
                            context,
                            jsonContent!!,
                            viewModel,
                            selectedBackupUserIds
                        )
                        Toast.makeText(
                            context,
                            context.getString(R.string.import_opt),
                            Toast.LENGTH_SHORT
                        ).show()
                        onFinished()
                    }
                }
            )
        } else {
            // Keine Nutzer im Backup gefunden
            Toast.makeText(context, context.getString(R.string.no_users_in_backup), Toast.LENGTH_SHORT).show()
            showUserDialog = false
        }
    }

    TextButton(onClick = {
        importLauncher.launch("application/json")
    }) {
        Text(stringResource(R.string.file_import), fontSize = 11.sp)
    }
}