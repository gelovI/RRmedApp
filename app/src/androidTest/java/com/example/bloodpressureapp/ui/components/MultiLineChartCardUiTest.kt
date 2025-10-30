package com.example.bloodpressureapp.ui.components

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.example.bloodpressureapp.MainActivity
import com.example.bloodpressureapp.ui.components.charts.MultiLineChartCard
import org.junit.Rule
import org.junit.Test


class MultiLineChartCardUiTest {

    @get:Rule
    val composeTestRule = createComposeRule()


    @Test
    fun shows_no_data_text_when_entries_are_empty() {
        composeTestRule.setContent {
            MultiLineChartCard(
                title = "Test",
                systolicEntries = emptyList(),
                diastolicEntries = emptyList(),
                pulseEntries = emptyList(),
                measurements = emptyList()
            )
        }


        val expectedText = "Keine Messwerte vorhanden"
        composeTestRule.onNodeWithText(expectedText).assertExists()
    }
}