package com.example.bloodpressureapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ValueColumn(
    label: String,
    value: String,
    valueFontSize: Int = 20,
    valueHeight: Dp = 28.dp
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, style = MaterialTheme.typography.caption)

        Box(
            modifier = Modifier
                .height(valueHeight),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = value,
                fontSize = valueFontSize.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun DividerLine() {
    Box(
        modifier = Modifier
            .height(40.dp)
            .width(1.dp)
            .background(Color.Black.copy(alpha = 0.6f))
    )
}
