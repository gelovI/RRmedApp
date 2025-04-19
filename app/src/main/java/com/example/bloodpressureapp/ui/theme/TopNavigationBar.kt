package com.example.bloodpressureapp.ui.theme

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.example.bloodpressureapp.R
import com.example.bloodpressureapp.viewmodel.AppViewModel
import com.example.bloodpressureapp.util.PreferenceManager
import com.example.bloodpressureapp.util.exportData
import com.example.bloodpressureapp.util.importData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun TopNavigationBar(
    username: String,
    viewModel: AppViewModel,
    preferenceManager: PreferenceManager
) {
    var showMenuDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showAddUserDialog by remember { mutableStateOf(false) }
    var newUserName by remember { mutableStateOf("") }

    val users by viewModel.users.collectAsState()
    val selectedUser by viewModel.selectedUser.collectAsState()

    TopAppBar(
        title = {
            Text(
                text = stringResource(R.string.welcome_user, username),
                style = MaterialTheme.typography.caption,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        actions = {
            IconButton(onClick = { showMenuDialog = true }) {
                Icon(Icons.Default.Menu, contentDescription = stringResource(R.string.menu))
            }
        }
    )

    // Menü Dialog
    if (showMenuDialog) {
        AlertDialog(
            onDismissRequest = { showMenuDialog = false },
            title = { Text(stringResource(R.string.user_options)) },
            text = {
                Column {
                    users.forEach { user ->
                        TextButton(onClick = {
                            viewModel.selectUser(user)
                            preferenceManager.setLastSelectedUserId(user.id)
                            showMenuDialog = false
                        }) {
                            Text(user.name)
                        }
                    }

                    Divider(modifier = Modifier.padding(vertical = 4.dp))

                    TextButton(onClick = {
                        showMenuDialog = false
                        showAddUserDialog = true
                    }) {
                        Text("➕ ${stringResource(R.string.create_new_user)}", fontSize = 11.sp)
                    }

                    TextButton(onClick = {
                        showMenuDialog = false
                        showDeleteDialog = true
                    }) {
                        Text(
                            "🗑️ ${stringResource(R.string.delete_active_user)}",
                            color = Color.Red,
                            fontSize = 11.sp
                        )
                    }

                    ImportDataButton(viewModel = viewModel) {
                        showMenuDialog = false
                    }

                    ExportDataButton(viewModel = viewModel) {
                        showMenuDialog = false
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showMenuDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }

    // Neuen Nutzer hinzufügen
    if (showAddUserDialog) {
        AlertDialog(
            onDismissRequest = { showAddUserDialog = false },
            text = {
                Column {
                    Text(
                        stringResource(R.string.create_new_user),
                        style = MaterialTheme.typography.subtitle1
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = newUserName,
                        onValueChange = { newUserName = it },
                        label = {
                            Text(
                                stringResource(R.string.enter_user_name),
                                fontSize = 11.sp
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (newUserName.isNotBlank()) {
                            viewModel.saveUser(newUserName)
                            newUserName = ""
                            showAddUserDialog = false
                        }
                    }
                ) {
                    Text(stringResource(R.string.save))
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddUserDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }

    // Nutzer löschen
    if (showDeleteDialog && selectedUser != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(stringResource(R.string.delete_user)) },
            text = { Text(stringResource(R.string.delete_user_confirmation, selectedUser!!.name)) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteUser(selectedUser!!)
                    showDeleteDialog = false
                }) {
                    Text(stringResource(R.string.delete), color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}

@Composable
fun ImportDataButton(viewModel: AppViewModel, onFinished: () -> Unit) {
    val context = LocalContext.current

    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            val inputStream = context.contentResolver.openInputStream(uri)
            val json = inputStream?.bufferedReader().use { it?.readText() }

            if (json != null) {
                CoroutineScope(Dispatchers.Main).launch {
                    importData(context, json, viewModel)
                    Toast.makeText(context, "✅ Import abgeschlossen", Toast.LENGTH_SHORT).show()
                    onFinished()
                }
            }
        }
    }

    TextButton(onClick = {
        importLauncher.launch("application/json")
    }) {
        Text("📥 Daten importieren", fontSize = 11.sp)
    }
}

@Composable
fun ExportDataButton(viewModel: AppViewModel, onFinished: () -> Unit) {
    val context = LocalContext.current

    TextButton(onClick = {
        CoroutineScope(Dispatchers.Main).launch {
            val json = exportData(context, viewModel)

            val fileName = "backup_${System.currentTimeMillis()}.json"
            val file = File(context.cacheDir, fileName)
            file.writeText(json)

            val uri: Uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.provider",
                file
            )

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "application/json"
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            context.startActivity(Intent.createChooser(shareIntent, "📤 Backup teilen"))

            onFinished()
        }
    }) {
        Text("📤 Daten exportieren")
    }
}


