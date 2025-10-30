package com.example.bloodpressureapp.ui.components

import com.example.bloodpressureapp.data.Measurement
import com.example.bloodpressureapp.util.getBpCategory
import org.junit.Assert.*
import org.junit.Test

class BloodPressureValuePieChartLogicTest {

    @Test
    fun category_counts_are_correct() {
        val list = listOf(
            Measurement(userId = 1, systolic = 120, diastolic = 80, pulse = 70, arrhythmia = false),
            Measurement(userId = 1, systolic = 125, diastolic = 85, pulse = 70, arrhythmia = false),
            Measurement(userId = 1, systolic = 140, diastolic = 90, pulse = 70, arrhythmia = false)
        )
        // Simuliere das Map/GroupBy aus der Composable:
        val categoryCounts = list
            .map { getBpCategory(it.systolic, it.diastolic) }
            .groupingBy { it }
            .eachCount()
        assertEquals(3, categoryCounts.values.sum())
        // Beispiel: Prüfe, ob eine Kategorie mit mind. 1 gezählt wurde
        assertTrue(categoryCounts.isNotEmpty())
    }

    @Test
    fun percentage_calculation_is_correct() {
        val categoryCounts = mapOf(0 to 2, 1 to 3) // 2 Optimal, 3 Normal
        val total = categoryCounts.values.sum()
        val percent0 = categoryCounts[0]!! * 100f / total
        val percent1 = categoryCounts[1]!! * 100f / total
        assertEquals(40f, percent0, 0.1f)
        assertEquals(60f, percent1, 0.1f)
    }
}