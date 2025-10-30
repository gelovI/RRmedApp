package com.example.bloodpressureapp.ui.components

import com.example.bloodpressureapp.data.Measurement
import com.github.mikephil.charting.data.Entry
import org.junit.Assert.*
import org.junit.Test

class MultiLineChartCardLogicTest {

    private fun createEntries(measurements: List<Measurement>, selector: (Measurement) -> Float): List<Entry> {
        return measurements.mapIndexed { index, m -> Entry(index.toFloat(), selector(m)) }
    }

    @Test
    fun test_systolic_entries_generated_correctly() {
        val list = listOf(
            Measurement(userId = 1, systolic = 120, diastolic = 80, pulse = 70, arrhythmia = false),
            Measurement(userId = 1, systolic = 130, diastolic = 85, pulse = 72, arrhythmia = false)
        )
        val entries = createEntries(list) { it.systolic.toFloat() }
        assertEquals(2, entries.size)
        assertEquals(120f, entries[0].y, 0.01f)
        assertEquals(130f, entries[1].y, 0.01f)
    }

    @Test
    fun test_empty_measurements_returns_empty_entries() {
        val list = emptyList<Measurement>()
        val entries = createEntries(list) { it.systolic.toFloat() }
        assertTrue(entries.isEmpty())
    }
}