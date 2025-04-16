package com.example.bloodpressureapp.ui.theme

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bloodpressureapp.R
import com.example.bloodpressureapp.viewmodel.AppViewModel
import com.example.bloodpressureapp.util.PreferenceManager

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
