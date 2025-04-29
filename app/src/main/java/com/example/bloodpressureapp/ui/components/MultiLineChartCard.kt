package com.example.bloodpressureapp.ui.components.charts

import android.view.ViewGroup
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.bloodpressureapp.R
import com.example.bloodpressureapp.data.Measurement
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import java.text.SimpleDateFormat
import java.util.*

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
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )

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

                        val pulseSet = LineDataSet(
                            pulseEntries,
                            context.getString(R.string.pulse)
                        ).apply {
                            color = android.graphics.Color.GREEN
                            setCircleColor(android.graphics.Color.GREEN)
                            lineWidth = 2f
                            setDrawValues(false)
                        }

                        this.data = LineData(systolicSet, diastolicSet, pulseSet)

                        val dateFormat = SimpleDateFormat("dd.MM", Locale.getDefault())
                        val labels = measurements.map { dateFormat.format(Date(it.timestamp)) }

                        xAxis.apply {
                            valueFormatter = IndexAxisValueFormatter(labels.toTypedArray())
                            labelRotationAngle = -45f
                            position = com.github.mikephil.charting.components.XAxis.XAxisPosition.BOTTOM
                            granularity = 1f
                            setDrawGridLines(false)
                        }

                        axisLeft.apply {
                            axisMinimum = 40f
                            axisMaximum = 200f
                            setDrawGridLines(false)
                        }

                        axisRight.isEnabled = false
                        description = Description().apply { text = "" }
                        legend.isEnabled = true
                        extraBottomOffset = 24f

                        invalidate()
                    }
                })
            }
        }
    }
}
