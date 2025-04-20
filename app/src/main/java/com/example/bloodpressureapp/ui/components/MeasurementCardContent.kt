package com.example.bloodpressureapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MeasurementCardContent(
    time: String,
    systolic: Int,
    diastolic: Int,
    pulse: Int,
    arrhythmia: Boolean,
    onInfoClick: () -> Unit
) {
    Box(modifier = Modifier.padding(16.dp)) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Zeit: $time",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .background(Color.White)
                    .padding(horizontal = 2.dp, vertical = 2.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                ValueColumn("Sys", systolic.toString())
                DividerLine()
                ValueColumn("Dias.", diastolic.toString())
                DividerLine()
                ValueColumn("Puls", pulse.toString())
                DividerLine()
                ValueColumn("Arrh.", if (arrhythmia) "Ja" else "Nein", valueFontSize = 14)
            }
        }

        IconButton(
            onClick = onInfoClick,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(16.dp)
                .background(Color(0xFF1976D2), shape = CircleShape)
                .padding(2.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = "Info",
                tint = Color.White,
                modifier = Modifier.size(14.dp)
            )
        }
    }
}
