package com.example.bloodpressureapp.ui.theme

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bloodpressureapp.R
import com.example.bloodpressureapp.viewmodel.AppViewModel
import com.github.mikephil.charting.data.Entry
import java.util.*
import com.example.bloodpressureapp.ui.components.BloodPressureValuePieChart
import com.example.bloodpressureapp.ui.components.charts.MultiLineChartCard

@Composable
fun StatisticsScreen(viewModel: AppViewModel) {
    val measurements by viewModel.measurements.collectAsState()

    val avgSystolic = measurements.map { it.systolic }.average().toInt()
    val avgDiastolic = measurements.map { it.diastolic }.average().toInt()
    val avgPulse = measurements.map { it.pulse }.average().toInt()

    val systolicEntries = measurements.mapIndexed { index, it ->
        Entry(index.toFloat(), it.systolic.toFloat())
    }

    val diastolicEntries = measurements.mapIndexed { index, it ->
        Entry(index.toFloat(), it.diastolic.toFloat())
    }

    val pulseEntries = measurements.mapIndexed { index, it ->
        Entry(index.toFloat(), it.pulse.toFloat())
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(elevation = 4.dp, modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        stringResource(R.string.averages),
                        style = MaterialTheme.typography.subtitle1
                    )
                    Text(
                        "${stringResource(R.string.systolic)}: $avgSystolic mmHg",
                        fontSize = 12.sp
                    )
                    Text(
                        "${stringResource(R.string.diastolic)}: $avgDiastolic mmHg",
                        fontSize = 12.sp
                    )
                    Text("${stringResource(R.string.pulse)}: $avgPulse bpm", fontSize = 12.sp)
                }
            }
        }

        item {
            MultiLineChartCard(
                title = stringResource(R.string.chart_title),
                systolicEntries = systolicEntries,
                diastolicEntries = diastolicEntries,
                pulseEntries = pulseEntries,
                measurements = measurements
            )
        }

        item {
            Card(
                elevation = 4.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "Messwert-Kategorien (gesamt)",
                        style = MaterialTheme.typography.subtitle1,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    BloodPressureValuePieChart(
                        measurements = measurements,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                    )
                }
            }
        }
    }
}