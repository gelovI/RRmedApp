package com.example.bloodpressureapp.util

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.bloodpressureapp.data.Measurement
import com.example.bloodpressureapp.data.User
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.util.*

@RunWith(AndroidJUnit4::class)
class GenerateMeasurementPdfTest {

    @Test
    fun pdf_is_created_for_valid_data() {
        // Arrange
        val context = ApplicationProvider.getApplicationContext<Context>()
        val measurements = listOf(
            Measurement(
                id = 1,
                userId = 1,
                timestamp = System.currentTimeMillis(),
                systolic = 120,
                diastolic = 80,
                pulse = 70,
                arrhythmia = false
            )
        )
        val startDate = Date(System.currentTimeMillis() - 1000 * 60 * 60) // vor 1h
        val endDate = Date(System.currentTimeMillis() + 1000 * 60 * 60)   // in 1h
        val user = User(1, "Max Mustermann")

        // Act
        val pdfFile: File? = generateMeasurementPdf(context, measurements, startDate, endDate, user)

        // Assert
        assertNotNull("PDF-Datei sollte erstellt werden", pdfFile)
        assertTrue("PDF-Datei existiert nicht!", pdfFile!!.exists())
        assertTrue("PDF-Datei ist leer!", pdfFile.length() > 0)
        // Optional: Dateiname prüfen
        assertTrue("PDF-Dateiname enthält nicht den Usernamen!", pdfFile.name.contains(user.name))
    }

    @Test
    fun pdf_is_null_for_empty_filtered_data() {
        // Arrange
        val context = ApplicationProvider.getApplicationContext<Context>()
        val measurements = emptyList<Measurement>()
        val now = Date()
        val user = User(1, "Test User")

        // Act
        val pdfFile = generateMeasurementPdf(context, measurements, now, now, user)

        // Assert
        assertNull("Bei leerer Messwert-Liste sollte kein PDF erstellt werden!", pdfFile)
    }

    @Test
    fun pdf_is_null_when_no_data_in_range() {
        // Arrange
        val context = ApplicationProvider.getApplicationContext<Context>()
        val measurements = listOf(
            Measurement(
                id = 1,
                userId = 1,
                timestamp = System.currentTimeMillis() - 1000 * 60 * 60 * 24 * 7, // vor 7 Tagen
                systolic = 120,
                diastolic = 80,
                pulse = 70,
                arrhythmia = false
            )
        )
        val startDate = Date(System.currentTimeMillis() - 1000 * 60 * 60) // vor 1h
        val endDate = Date(System.currentTimeMillis())   // jetzt
        val user = User(1, "Test User")

        // Act
        val pdfFile = generateMeasurementPdf(context, measurements, startDate, endDate, user)

        // Assert
        assertNull("Kein Messwert im Bereich – kein PDF!", pdfFile)
    }
}