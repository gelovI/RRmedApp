package com.example.bloodpressureapp.ui.components

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.sp
import com.example.bloodpressureapp.util.importData
import com.example.bloodpressureapp.viewmodel.AppViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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