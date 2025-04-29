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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bloodpressureapp.R

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
                ValueColumn(stringResource(R.string.overview_systolic), systolic.toString())
                DividerLine()
                ValueColumn(stringResource(R.string.overview_diastolic), diastolic.toString())
                DividerLine()
                ValueColumn(stringResource(R.string.overview_pulse), pulse.toString())
                DividerLine()
                ValueColumn(stringResource(R.string.overview_arrhythmia), if (arrhythmia) stringResource(R.string.overview_yes) else stringResource(R.string.overview_no), valueFontSize = 14)
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
                contentDescription = stringResource(R.string.info_btn),
                tint = Color.White,
                modifier = Modifier.size(14.dp)
            )
        }
    }
}
