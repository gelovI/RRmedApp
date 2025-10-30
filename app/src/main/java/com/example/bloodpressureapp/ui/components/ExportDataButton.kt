package com.example.bloodpressureapp.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.example.bloodpressureapp.R
import com.example.bloodpressureapp.util.exportDataForUsers
import com.example.bloodpressureapp.viewmodel.AppViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun ExportDataButton(viewModel: AppViewModel, onFinished: () -> Unit) {
    val context = LocalContext.current
    var showUserDialog by remember { mutableStateOf(false) }
    val users by viewModel.users.collectAsState(initial = emptyList())

    if (showUserDialog) {
        UserMultiSelectDialog(
            title = stringResource(R.string.select_users_export),
            items = users,
            idOf = { it.id },
            labelOf = { it.name },
            onDismiss = { showUserDialog = false },
            onConfirm = { selectedIds ->
                showUserDialog = false
                if (selectedIds.isNotEmpty()) {
                    CoroutineScope(Dispatchers.Main).launch {
                        val json = exportDataForUsers(context, viewModel, selectedIds)

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

                        context.startActivity(
                            Intent.createChooser(
                                shareIntent,
                                context.getString(R.string.share_backup)
                            )
                        )

                        onFinished()
                    }
                }
            }
        )
    }

    TextButton(onClick = {
        showUserDialog = true
    }) {
        Text(stringResource(R.string.file_export), fontSize = 11.sp)
    }
}