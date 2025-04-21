package com.example.bloodpressureapp.ui.theme

import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.bloodpressureapp.R
import com.example.bloodpressureapp.viewmodel.AppViewModel
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import java.text.SimpleDateFormat
import java.util.*
import com.example.bloodpressureapp.data.Measurement
import com.example.bloodpressureapp.ui.components.BloodPressureValuePieChart

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

@Composable
fun MultiLineChartCard(
    title: String,
    systolicEntries: List<Entry>,
    diastolicEntries: List<Entry>,
    pulseEntries: List<Entry>,
    measurements: List<Measurement>
) {
    Card(
        elevation = 4.dp,
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .padding(bottom = 8.dp)
    ) {
        Column(Modifier.padding(8.dp)) {
            Text(text = title, fontSize = 12.sp)

            if (systolicEntries.isEmpty() && diastolicEntries.isEmpty() && pulseEntries.isEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = stringResource(R.string.no_measurements_available),
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f),
                    style = MaterialTheme.typography.body2
                )
            } else {
                AndroidView(factory = { context ->
                    LineChart(context).apply {
                        layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT)

                        val systolicSet = LineDataSet(
                            systolicEntries,
                            context.getString(R.string.systolic)
                        ).apply {
                            color = android.graphics.Color.RED
                            setCircleColor(android.graphics.Color.RED)
                            lineWidth = 2f
                            setDrawValues(false)
                        }

                        val diastolicSet = LineDataSet(
                            diastolicEntries,
                            context.getString(R.string.diastolic)
                        ).apply {
                            color = android.graphics.Color.BLUE
                            setCircleColor(android.graphics.Color.BLUE)
                            lineWidth = 2f
                            setDrawValues(false)
                        }

                        val pulseSet =
                            LineDataSet(pulseEntries, context.getString(R.string.pulse)).apply {
                                color = android.graphics.Color.GREEN
                                setCircleColor(android.graphics.Color.GREEN)
                                lineWidth = 2f
                                setDrawValues(false)
                            }

                        this.data = LineData(systolicSet, diastolicSet, pulseSet)

                        val dateFormat = SimpleDateFormat("dd.MM", Locale.getDefault())
                        val labels = measurements.map {
                            dateFormat.format(Date(it.timestamp))
                        }

                        val xAxis = this.xAxis
                        xAxis.valueFormatter = IndexAxisValueFormatter(labels.toTypedArray())
                        xAxis.labelRotationAngle = -45f
                        xAxis.position =
                            com.github.mikephil.charting.components.XAxis.XAxisPosition.BOTTOM
                        xAxis.granularity = 1f
                        xAxis.setDrawGridLines(false)

                        axisLeft.axisMinimum = 40f
                        axisLeft.axisMaximum = 200f
                        axisLeft.setDrawGridLines(false)

                        axisRight.isEnabled = false
                        description = Description().apply { text = "" }
                        legend.isEnabled = true
                        this.extraBottomOffset = 24f

                        invalidate()
                    }
                })
            }
        }
    }
}