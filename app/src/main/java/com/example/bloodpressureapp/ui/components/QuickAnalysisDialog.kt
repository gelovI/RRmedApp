package com.example.bloodpressureapp.ui.components

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bloodpressureapp.R

@Composable
fun QuickAnalysisDialog(
    context: Context,
    systolic: Int,
    diastolic: Int,
    pulse: Int,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.quick_analyse)) },
        text = {
            Column {
                Text("${stringResource(R.string.analise_sys)} $systolic ${stringResource(R.string.mmHg)}", fontSize = 14.sp)
                Text("${stringResource(R.string.analise_dias)} $diastolic ${stringResource(R.string.mmHg)}", fontSize = 14.sp)
                Text("${stringResource(R.string.analise_puls)} $pulse ${stringResource(R.string.bpm)}", fontSize = 14.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Divider()
                Spacer(modifier = Modifier.height(8.dp))
                Text(stringResource(R.string.rating), fontSize = 14.sp)
                Text(analyzeValues(context, systolic, diastolic, pulse), fontSize = 13.sp)
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.close))
            }
        }
    )
}

private fun analyzeValues(
    context: Context,
    systolic: Int,
    diastolic: Int,
    pulse: Int
): String {
    val sb = StringBuilder()

    when {
        systolic < 130 && diastolic < 85 -> sb.append(context.getString(R.string.bp_normal) + "\n")
        systolic in 130..139 || diastolic in 85..89 -> sb.append(context.getString(R.string.bp_elevated) + "\n")
        else -> sb.append(context.getString(R.string.bp_high) + "\n")
    }

    when {
        pulse < 60 -> sb.append(context.getString(R.string.pulse_low) + "\n")
        pulse in 60..100 -> sb.append(context.getString(R.string.pulse_normal) + "\n")
        else -> sb.append(context.getString(R.string.pulse_high) + "\n")
    }

    return sb.toString().trim()
}

