package com.example.bloodpressureapp.ui.components

import android.graphics.Color as AndroidColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.example.bloodpressureapp.data.Measurement
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry

@Composable
fun BloodPressureValueCategoryBarChart(
    measurements: List<Measurement>,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    var normal = 0
    var elevated = 0
    var high = 0

    measurements.forEach { m ->
        val category = when {
            m.systolic < 120 && m.diastolic < 80 -> "normal"
            m.systolic in 120..139 || m.diastolic in 80..89 -> "elevated"
            else -> "high"
        }

        when (category) {
            "normal" -> normal++
            "elevated" -> elevated++
            "high" -> high++
        }
    }

    val total = normal + elevated + high

    val entries = listOf(
        BarEntry(0f, if (total > 0) (normal * 100f / total) else 0f),
        BarEntry(1f, if (total > 0) (elevated * 100f / total) else 0f),
        BarEntry(2f, if (total > 0) (high * 100f / total) else 0f)
    )

    val dataSet = BarDataSet(entries, "Messwert-Kategorien").apply {
        colors = listOf(AndroidColor.GREEN, AndroidColor.YELLOW, AndroidColor.RED)
        valueTextSize = 12f
    }

    val barData = BarData(dataSet)

    AndroidView(
        factory = {
            BarChart(context).apply {
                data = barData
                xAxis.apply {
                    granularity = 1f
                    setDrawLabels(true)
                    valueFormatter = com.github.mikephil.charting.formatter.IndexAxisValueFormatter(
                        listOf("Normal", "Leicht", "Hoch")
                    )
                }
                axisLeft.axisMinimum = 0f
                axisLeft.axisMaximum = 100f
                axisRight.isEnabled = false
                description = Description().apply { text = "" }
                legend.isEnabled = false
                invalidate()
            }
        },
        modifier = modifier
    )
}
