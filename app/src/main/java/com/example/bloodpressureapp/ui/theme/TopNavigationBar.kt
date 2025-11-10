package com.example.bloodpressureapp.ui.theme

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bloodpressureapp.R
import com.example.bloodpressureapp.ui.components.ExportDataButton
import com.example.bloodpressureapp.ui.components.ImportDataButton
import com.example.bloodpressureapp.viewmodel.AppViewModel
import com.example.bloodpressureapp.util.PreferenceManager

@Composable
fun TopNavigationBar(
    username: String,
    viewModel: AppViewModel,
    preferenceManager: PreferenceManager,
    onShowPdfExportDialog: () -> Unit
) {
    var showMenuDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showAddUserDialog by remember { mutableStateOf(false) }
    var newUserName by remember { mutableStateOf("") }

    val users by viewModel.users.collectAsState()
    val selectedUser by viewModel.selectedUser.collectAsState()

    val configuration = LocalConfiguration.current
    val screenWidthDp = configuration.screenWidthDp
    val isTablet = screenWidthDp >= 600

    val titleFontSize = if (isTablet) 19.sp else 15.sp
    val itemTitleSize = if (isTablet) 18.sp else 13.sp
    val itemSubtitleSize = if (isTablet) 14.sp else 10.sp

    TopAppBar(
        title = {
            Text(
                text = stringResource(R.string.welcome_user, username),
                fontSize = titleFontSize,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        actions = {
            IconButton(onClick = { showMenuDialog = true }) {
                Icon(
                    Icons.Filled.MoreVert,
                    contentDescription = stringResource(R.string.menu)
                )
            }
        },
        backgroundColor = MaterialTheme.colors.primary,
        contentColor = Color.White,
        elevation = 4.dp
    )

    if (showMenuDialog) {
        AlertDialog(
            onDismissRequest = { showMenuDialog = false },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = null,
                        tint = MaterialTheme.colors.primary
                    )
                    Column {
                        Text(
                            text = stringResource(R.string.user_options),
                            fontSize = if (isTablet) 20.sp else 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                        selectedUser?.let {
                            Text(
                                text = it.name,
                                fontSize = if (isTablet) 16.sp else 13.sp,
                                color = MaterialTheme.colors.primaryVariant
                            )
                        }
                    }
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ) {
                    // --- Benutzerverwaltung ---
                    Text(
                        text = stringResource(R.string.user_management),
                        fontSize = itemSubtitleSize,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colors.primary,
                        modifier = Modifier
                            .padding(start = 4.dp, top = 4.dp, bottom = 2.dp)
                    )

                    if (users.isNotEmpty()) {
                        users.forEach { user ->
                            SettingsActionRow(
                                icon = Icons.Default.Person,
                                iconTint = if (user.id == selectedUser?.id)
                                    MaterialTheme.colors.primary
                                else
                                    MaterialTheme.colors.onSurface.copy(alpha = 0.7f),
                                title = user.name,
                                subtitle = if (user.id == selectedUser?.id)
                                    stringResource(R.string.active_user)
                                else
                                    stringResource(R.string.switch_to_user),
                                titleFontSize = itemTitleSize,
                                subtitleFontSize = itemSubtitleSize
                            ) {
                                viewModel.selectUser(user)
                                preferenceManager.setLastSelectedUserId(user.id)
                                showMenuDialog = false
                            }
                        }
                    }

                    SettingsActionRow(
                        icon = Icons.Default.PersonAdd,
                        iconTint = MaterialTheme.colors.primary,
                        title = stringResource(R.string.create_new_user),
                        subtitle = stringResource(R.string.create_new_user_sub),
                        titleFontSize = itemTitleSize,
                        subtitleFontSize = itemSubtitleSize
                    ) {
                        showMenuDialog = false
                        showAddUserDialog = true
                    }

                    selectedUser?.let {
                        SettingsActionRow(
                            icon = Icons.Default.Delete,
                            iconTint = Color.Red,
                            title = stringResource(R.string.delete_active_user),
                            subtitle = stringResource(R.string.delete_active_user_sub),
                            titleFontSize = itemTitleSize,
                            subtitleFontSize = itemSubtitleSize,
                            titleColor = Color.Red
                        ) {
                            showMenuDialog = false
                            showDeleteDialog = true
                        }
                    }

                    Spacer(Modifier.height(8.dp))

                    // --- Daten & Export ---
                    Text(
                        text = stringResource(R.string.data_and_export),
                        fontSize = itemSubtitleSize,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colors.primary,
                        modifier = Modifier
                            .padding(start = 4.dp, top = 4.dp, bottom = 2.dp)
                    )

                    // Import / Export: benutze deine bestehenden Composables direkt
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp, horizontal = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ImportDataButton(viewModel = viewModel) {
                            showMenuDialog = false
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp, horizontal = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ExportDataButton(viewModel = viewModel) {
                            showMenuDialog = false
                        }
                    }

                    // PDF-Export (nur State ändern / Callback, KEIN Composable Aufruf hier)
                    SettingsActionRow(
                        icon = Icons.Default.PictureAsPdf,
                        iconTint = MaterialTheme.colors.secondary,
                        title = stringResource(R.string.overview_export_pdf),
                        subtitle = stringResource(R.string.overview_export_pdf_sub),
                        titleFontSize = itemTitleSize,
                        subtitleFontSize = itemSubtitleSize
                    ) {
                        showMenuDialog = false
                        onShowPdfExportDialog()
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
                        style = MaterialTheme.typography.subtitle1,
                        fontWeight = FontWeight.SemiBold
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
                            viewModel.saveUser(newUserName.trim())
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
            text = {
                Text(
                    stringResource(
                        R.string.delete_user_confirmation,
                        selectedUser!!.name
                    )
                )
            },
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

/**
 * Einheitliche Zeile für Aktionen im Dialog
 */
@Composable
private fun SettingsActionRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    title: String,
    subtitle: String? = null,
    titleFontSize: androidx.compose.ui.unit.TextUnit,
    subtitleFontSize: androidx.compose.ui.unit.TextUnit,
    titleColor: Color = MaterialTheme.colors.onSurface,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 8.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier
                .size(24.dp)
                .padding(end = 8.dp)
        )
        Column(
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                fontSize = titleFontSize,
                fontWeight = FontWeight.Medium,
                color = titleColor
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    fontSize = subtitleFontSize,
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f),
                    maxLines = 2
                )
            }
        }
    }
}