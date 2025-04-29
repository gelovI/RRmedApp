package com.example.bloodpressureapp.ui.components

import android.graphics.Color as AndroidColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidView
import com.example.bloodpressureapp.R
import com.example.bloodpressureapp.data.Measurement
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry

@Composable
fun BloodPressureValuePieChart(
    measurements: List<Measurement>,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    var normal = 0
    var elevated = 0
    var high = 0

    measurements.forEach { m ->
        when {
            m.systolic < 120 && m.diastolic < 80 -> normal++
            m.systolic in 120..139 || m.diastolic in 80..89 -> elevated++
            else -> high++
        }
    }

    val entries = listOf(
        PieEntry(normal.toFloat(), stringResource(R.string.label_normal)),
        PieEntry(elevated.toFloat(), stringResource(R.string.label_elevated)),
        PieEntry(high.toFloat(), stringResource(R.string.label_high))
    ).filter { it.value > 0f }

    val dataSet = PieDataSet(entries, stringResource(R.string.chart_categories)).apply {
        colors = listOf(AndroidColor.GREEN, AndroidColor.YELLOW, AndroidColor.RED)
        valueTextSize = 12f
        sliceSpace = 2f
    }

    val data = PieData(dataSet)

    AndroidView(
        factory = {
            PieChart(context).apply {
                this.data = data
                setUsePercentValues(true)
                description = Description().apply { text = "" }
                isDrawHoleEnabled = true
                setEntryLabelTextSize(12f)
                setEntryLabelColor(AndroidColor.BLACK)
                legend.isEnabled = true
                invalidate()
            }
        },
        modifier = modifier
    )
}