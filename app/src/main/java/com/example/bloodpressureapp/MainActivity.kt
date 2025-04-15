package com.example.bloodpressureapp

import com.example.bloodpressureapp.util.PreferenceManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.room.Room
import com.example.bloodpressureapp.data.AppDatabase
import com.example.bloodpressureapp.viewmodel.AppViewModel
import com.example.bloodpressureapp.ui.theme.*
import com.example.bloodpressureapp.ui.components.BottomNavigationArrows
import com.example.bloodpressureapp.ui.components.TopTabNavigation
import com.example.bloodpressureapp.data.MIGRATION_1_2
import com.example.bloodpressureapp.data.MIGRATION_2_3
import android.Manifest
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker.PERMISSION_GRANTED


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "bloodpressure-db"
        )
            .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
            .build()

        val dao = db.dao()
        val viewModel = AppViewModel(dao)
        val preferenceManager = PreferenceManager(applicationContext)

        setContent {
            val permissionLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestPermission()
            ) { isGranted ->
                if (!isGranted) {
                    Toast.makeText(this, "Benachrichtigungen deaktiviert", Toast.LENGTH_SHORT).show()
                }
            }

            LaunchedEffect(Unit) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    val granted = ContextCompat.checkSelfPermission(
                        this@MainActivity,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) == PERMISSION_GRANTED

                    if (!granted) {
                        permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                }
            }

            var screenIndex by remember { mutableIntStateOf(0) }

            val users by viewModel.users.collectAsState()
            val selectedUser by viewModel.selectedUser.collectAsState()
            val username = selectedUser?.name ?: "..."

            val lastUserId = preferenceManager.getLastSelectedUserId()

            LaunchedEffect(users) {
                if (users.isNotEmpty() && selectedUser == null) {
                    val last = users.find { it.id == lastUserId } ?: users.last()
                    viewModel.selectUser(last)
                    screenIndex = 0
                }
            }

            Box(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(screenIndex) {
                            detectHorizontalDragGestures { _, dragAmount ->
                                if (dragAmount > 50) screenIndex =
                                    (screenIndex - 1).coerceAtLeast(0)
                                if (dragAmount < -50) screenIndex =
                                    (screenIndex + 1).coerceAtMost(4)
                            }
                        }
                ) {
                    // 🔝 Top Navigation Bar mit Benutzername
                    TopNavigationBar(
                        username = username,
                        viewModel = viewModel,
                        preferenceManager = preferenceManager
                    )

                    TopTabNavigation(
                        selectedIndex = screenIndex,
                        onTabSelected = { screenIndex = it }
                    )


                    // 📦 Hauptinhalt der aktuellen View
                    Box(modifier = Modifier.weight(1f)) {
                        when (screenIndex) {
                            0 -> selectedUser?.let {
                                MeasurementScreen(viewModel = viewModel, userId = it.id)
                            } ?: Text("Kein Nutzer ausgewählt.")

                            1 -> OverviewScreen(viewModel = viewModel)

                            2 -> StatisticsScreen(viewModel = viewModel)

                            3 -> selectedUser?.let {
                                TherapyScreen(viewModel = viewModel, userId = it.id)
                            } ?: Text("Kein Nutzer ausgewählt.")

                            4 -> selectedUser?.let {
                                ReminderScreen(viewModel = viewModel, userId = it.id)
                            } ?: Text("Kein Nutzer ausgewählt.")
                        }

                        BottomNavigationArrows(
                            onPrevious = { screenIndex = (screenIndex - 1).coerceAtLeast(0) },
                            onNext = { screenIndex = (screenIndex + 1).coerceAtMost(4) },
                            modifier = Modifier.align(Alignment.BottomCenter)
                        )
                    }
                }
            }
        }
    }
}
