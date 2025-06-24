package com.example.bloodpressureapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ValueColumn(
    label: String,
    value: String,
    valueHeight: Dp = 38.dp
) {
    val configuration = LocalConfiguration.current
    val screenWidthDp = configuration.screenWidthDp
    val valueFontSize = if (screenWidthDp >= 720) 32 else 20

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f)
        )

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
