package com.example.bloodpressureapp.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DisclaimerBox(
    title: String,
    text: String,
    modifier: Modifier = Modifier
) {
    val error = MaterialTheme.colors.error
    val bg    = error.copy(alpha = 0.08f)   // sanftes Rot
    val stroke = error.copy(alpha = 0.30f)

    Surface(
        color = bg,
        shape = MaterialTheme.shapes.medium,  // abgerundete Ecken
        border = BorderStroke(1.dp, stroke),
        modifier = modifier
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.Info,
                contentDescription = null,
                tint = error,
                modifier = Modifier.size(18.dp)
            )
            Spacer(Modifier.width(8.dp))
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.subtitle2,
                    color = error
                )
                Text(
                    text = text,
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )
            }
        }
    }
}