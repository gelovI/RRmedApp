package com.example.bloodpressureapp.ui.components

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bloodpressureapp.R
import com.example.bloodpressureapp.util.getBpCategory

@Composable
fun QuickAnalysisDialog(
    context: Context,
    systolic: Int,
    diastolic: Int,
    pulse: Int,
    onDismiss: () -> Unit
) {
    val scroll = rememberScrollState()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(R.string.quick_analyse),
                style = MaterialTheme.typography.h6,
                fontSize = 14.sp
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(scroll)
                    .padding(horizontal = 4.dp, vertical = 2.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Werteabschnitt
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("• ${stringResource(R.string.analise_sys)} $systolic ${stringResource(R.string.mmHg)}", fontSize = 12.sp)
                    Text("• ${stringResource(R.string.analise_dias)} $diastolic ${stringResource(R.string.mmHg)}", fontSize = 12.sp)
                    Text("• ${stringResource(R.string.analise_puls)} $pulse ${stringResource(R.string.bpm)}", fontSize = 12.sp)
                }

                Divider()

                // Bewertung
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = stringResource(R.string.rating),
                        fontSize = 14.sp,
                        style = MaterialTheme.typography.subtitle1
                    )
                    Text(
                        text = analyzeValues(context, systolic, diastolic, pulse),
                        fontSize = 12.sp,
                        lineHeight = 18.sp
                    )
                }

                // Hinweisbox
                Spacer(Modifier.height(8.dp))
                DisclaimerBox(
                    title = stringResource(R.string.analysis_disclaimer_title),
                    text  = stringResource(R.string.analysis_disclaimer_text),
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    stringResource(R.string.close),
                    color = MaterialTheme.colors.primary,
                    fontWeight = FontWeight.Bold
                )
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

    when (getBpCategory(systolic, diastolic)) {
        0 -> sb.append(context.getString(R.string.bp_optimal) + "\n")
        1 -> sb.append(context.getString(R.string.bp_normal) + "\n")
        2 -> sb.append(context.getString(R.string.bp_high_normal) + "\n")
        3 -> sb.append(context.getString(R.string.bp_hypertension_1) + "\n")
        4 -> sb.append(context.getString(R.string.bp_hypertension_2) + "\n")
        5 -> sb.append(context.getString(R.string.bp_hypertension_severe) + "\n")
        else -> sb.append(context.getString(R.string.bp_unspecified) + "\n")
    }

    when {
        pulse < 60 ->
            sb.append(context.getString(R.string.pulse_low) + "\n")
        pulse in 60..100 ->
            sb.append(context.getString(R.string.pulse_normal) + "\n")
        else ->
            sb.append(context.getString(R.string.pulse_high) + "\n")
    }

    return sb.toString().trim()
}

