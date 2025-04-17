package com.example.bloodpressureapp.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun QuickAnalysisDialog(
    systolic: Int,
    diastolic: Int,
    pulse: Int,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("📊 Schnellanalyse") },
        text = {
            Column {
                Text("➤ Systolisch: $systolic mmHg", fontSize = 14.sp)
                Text("➤ Diastolisch: $diastolic mmHg", fontSize = 14.sp)
                Text("➤ Puls: $pulse bpm", fontSize = 14.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Divider()
                Spacer(modifier = Modifier.height(8.dp))
                Text("💡 Bewertung:", fontSize = 14.sp)
                Text(analyzeValues(systolic, diastolic, pulse), fontSize = 13.sp)
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Schließen")
            }
        }
    )
}

private fun analyzeValues(systolic: Int, diastolic: Int, pulse: Int): String {
    val sb = StringBuilder()

    when {
        systolic < 130 && diastolic < 85 -> sb.append("🟢 Blutdruck im normalen Bereich.\n")
        systolic in 130..139 || diastolic in 85..89 -> sb.append("🟡 Leicht erhöhter Blutdruck.\n")
        else -> sb.append("🔴 Hoher Blutdruck! Ärztliche Abklärung empfohlen.\n")
    }

    when {
        pulse < 60 -> sb.append("⚠️ Puls ist niedrig (Bradykardie).\n")
        pulse in 60..100 -> sb.append("✅ Puls im normalen Bereich.\n")
        else -> sb.append("⚠️ Puls ist erhöht (Tachykardie).\n")
    }

    return sb.toString().trim()
}
