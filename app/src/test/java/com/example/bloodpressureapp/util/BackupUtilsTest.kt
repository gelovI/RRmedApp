package com.example.bloodpressureapp.util

import android.content.Context
import com.example.bloodpressureapp.data.*
import com.example.bloodpressureapp.viewmodel.AppViewModel
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class BackupUtilsTest {

    private lateinit var viewModel: AppViewModel
    private val context = mockk<Context>(relaxed = true)

    @Before
    fun setup() {
        viewModel = mockk(relaxed = true)
    }

    @Test
    fun `exportData returns valid json`() = runTest {
        // Arrange
        val user = User(id = 1, name = "Max")
        val measurement = Measurement(id = 1, userId = 1, systolic = 120, diastolic = 80, pulse = 70, arrhythmia = false, timestamp = 1L)
        val therapy = Therapy(id = 1, userId = 1, name = "TestMed", dosage = "5mg")
        val reminder = Reminder(id = 1, userId = 1, hour = 8, minute = 30, message = "Test", repeatDaily = true)

        coEvery { viewModel.getAllUsersOnce() } returns listOf(user)
        coEvery { viewModel.getAllMeasurements() } returns listOf(measurement)
        coEvery { viewModel.getAllTherapies() } returns listOf(therapy)
        coEvery { viewModel.getAllReminders() } returns listOf(reminder)

        // Act
        val json = exportData(context, viewModel)

        // Assert
        assertTrue(json.contains("Max"))
        assertTrue(json.contains("TestMed"))
        assertTrue(json.contains("Test"))
    }
}