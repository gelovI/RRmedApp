package com.example.bloodpressureapp.util

import com.example.bloodpressureapp.viewmodel.AppViewModel
import io.mockk.*
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import android.content.Context

class ImportBackupUtilsTest {

    private lateinit var viewModel: AppViewModel
    private val context: Context = mockk(relaxed = true)

    @Before
    fun setup() {
        viewModel = mockk(relaxed = true)
    }

    @Test
    fun `importData should insert users, measurements, therapies, and reminders`() = runTest {
        // 1. Testdaten als JSON (1 User, 1 Measurement, 1 Therapy, 1 Reminder)
        val jsonContent = """
            {
              "users": [{"id":1,"name":"Max"}],
              "measurements": [{"id":10,"userId":1,"systolic":120,"diastolic":80,"pulse":75,"arrhythmia":false,"timestamp":1718993027000}],
              "therapies": [{"id":20,"userId":1,"name":"BetaBlocker","dosage":"1x täglich"}],
              "reminders": [{"id":30,"userId":1,"hour":8,"minute":0,"message":"Take meds","repeatDaily":true,"days":"","createdAt":1718993027000}]
            }
        """.trimIndent()

        // 2. Beim User-Save kommt eine neue Id zurück (hier 123)
        coEvery { viewModel.saveUserAndReturnId(any()) } returns 123

        // 3. Alle Daten-Aufrufe beobachten
        coJustRun { viewModel.saveMeasurement(any(), any(), any(), any(), any(), any()) }
        coJustRun { viewModel.saveTherapy(any(), any(), any()) }
        coJustRun { viewModel.addReminder(any(), any(), any(), any(), any(), any(), any()) }

        // 4. Import ausführen
        importData(context, jsonContent, viewModel)

        // 5. Überprüfen: Neue UserId wurde verwendet
        coVerify { viewModel.saveUserAndReturnId("Max") }
        coVerify {
            viewModel.saveMeasurement(
                systolic = 120,
                diastolic = 80,
                pulse = 75,
                arrhythmia = false,
                userId = 123,   // Die neue UserId!
                timestamp = 1718993027000
            )
        }
        coVerify {
            viewModel.saveTherapy(123, "BetaBlocker", "1x täglich")
        }
        coVerify {
            viewModel.addReminder(
                context = context,
                userId = 123,
                hour = 8,
                minute = 0,
                message = "Take meds",
                repeatDaily = true,
                days = ""
            )
        }
    }
}
