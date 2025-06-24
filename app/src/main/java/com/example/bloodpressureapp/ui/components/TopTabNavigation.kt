package com.example.bloodpressureapp.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.*
import androidx.compose.material.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.ShowChart

@Composable
fun TopTabNavigation(
    selectedIndex: Int,
    onTabSelected: (Int) -> Unit
) {
    val tabs = listOf(
        Icons.Default.Favorite,
        Icons.Filled.List,
        Icons.Filled.ShowChart,
        Icons.Default.MedicalServices,
        Icons.Default.Alarm
    )

    TabRow(
        selectedTabIndex = selectedIndex,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        backgroundColor = MaterialTheme.colors.surface,
        contentColor = MaterialTheme.colors.primary,
        indicator = { tabPositions ->
            TabRowDefaults.Indicator(
                modifier = Modifier
                    .tabIndicatorOffset(tabPositions[selectedIndex])
                    .height(3.dp),
                color = MaterialTheme.colors.primary
            )
        }
    ) {
        tabs.forEachIndexed { index, icon ->
            val selected = selectedIndex == index

            val iconColor by animateColorAsState(
                if (selected) MaterialTheme.colors.primary else Color.LightGray
            )

            Tab(
                selected = selected,
                onClick = { onTabSelected(index) },
                icon = {
                    Icon(
                        imageVector = icon,
                        contentDescription = "Tab $index",
                        tint = iconColor
                    )
                }
            )
        }
    }
}
