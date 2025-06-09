package com.example.bloodpressureapp.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.example.bloodpressureapp.R
import com.example.bloodpressureapp.data.Measurement
import com.example.bloodpressureapp.util.getBpCategory
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.utils.ColorTemplate

@Composable
fun BloodPressureValuePieChart(
    measurements: List<Measurement>,
    modifier: Modifier = Modifier
) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            val pieChart = PieChart(context)

            val categoryCounts = measurements
                .map { getBpCategory(it.systolic, it.diastolic) }
                .groupingBy { it }
                .eachCount()

            val entries = mutableListOf<PieEntry>()

            categoryCounts.forEach { (category, count) ->
                val label = when (category) {
                    0 -> context.getString(R.string.bp_optimal)
                    1 -> context.getString(R.string.bp_normal)
                    2 -> context.getString(R.string.bp_high_normal)
                    3 -> context.getString(R.string.bp_hypertension_1)
                    4 -> context.getString(R.string.bp_hypertension_2)
                    5 -> context.getString(R.string.bp_hypertension_severe)
                    else -> context.getString(R.string.bp_unspecified)
                }
                entries.add(PieEntry(count.toFloat(), label))
            }

            val dataSet = PieDataSet(entries, "").apply {
                colors = ColorTemplate.MATERIAL_COLORS.toList()
                valueTextSize = 12f
            }

            pieChart.data = PieData(dataSet)
            pieChart.description.isEnabled = false
            pieChart.setUsePercentValues(true)
            pieChart.setDrawEntryLabels(true)
            pieChart.legend.isEnabled = true
            pieChart.invalidate()

            pieChart
        }
    )
}
