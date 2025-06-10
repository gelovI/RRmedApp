package com.example.bloodpressureapp.ui.components

import android.content.Context
import android.graphics.Color
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.bloodpressureapp.R
import com.example.bloodpressureapp.data.Measurement
import com.example.bloodpressureapp.util.getBpCategory
import com.example.bloodpressureapp.util.getBpCategoryLabel
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.*

@Composable
fun BloodPressureValuePieChart(
    measurements: List<Measurement>,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val categoryCounts = remember(measurements) {
        measurements
            .map { getBpCategory(it.systolic, it.diastolic) }
            .groupingBy { it }
            .eachCount()
    }

    val total = categoryCounts.values.sum()

    val entries = categoryCounts.map { (_, count) ->
        PieEntry(count.toFloat())
    }

    val colors = listOf(
        Color.parseColor("#8BC34A"), // Optimal
        Color.parseColor("#FFEB3B"), // Normal
        Color.parseColor("#03A9F4"), // Hochnormal
        Color.parseColor("#FF9800"), // Hypertonie 1
        Color.parseColor("#F44336"), // Hypertonie 2
        Color.parseColor("#9C27B0")  // Schwere
    )

    Column(modifier = modifier) {
        AndroidView(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp),
            factory = { context ->
            PieChart(context).apply {
                val dataSet = PieDataSet(entries, "").apply {
                    this.colors = colors
                    valueTextSize = 12f
                }

                this.data = PieData(dataSet).apply {
                    setValueTextColor(Color.BLACK)
                    setValueTextSize(12f)
                }

                setUsePercentValues(true)
                setDrawEntryLabels(false)
                legend.isEnabled = false
                description.isEnabled = false
                setEntryLabelColor(Color.TRANSPARENT)
                invalidate()
            }
        })

        Spacer(modifier = Modifier.height(12.dp))

        // 🔽 Legende unter dem Diagramm
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            categoryCounts.toSortedMap().forEach { (category, count) ->
                val label = getBpCategoryLabel(context, category)
                val percent = count * 100f / total
                val color = colors.getOrElse(category) { Color.GRAY }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(start = 12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .background(androidx.compose.ui.graphics.Color(color), CircleShape)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "$label: ${"%.1f".format(percent)}%",
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}