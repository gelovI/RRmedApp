package com.example.bloodpressureapp.ui.theme

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bloodpressureapp.viewmodel.AppViewModel
import com.example.bloodpressureapp.R
import kotlinx.coroutines.launch

@Composable
fun MeasurementScreen(viewModel: AppViewModel, userId: Int) {
    var systolic by remember { mutableStateOf("") }
    var diastolic by remember { mutableStateOf("") }
    var pulse by remember { mutableStateOf("") }
    var arrhythmia by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val snackbarMessage = stringResource(R.string.measurement_saved)

    val isValidInput = systolic.toIntOrNull() in 40..250 &&
            diastolic.toIntOrNull() in 30..150 &&
            pulse.toIntOrNull() in 30..200

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = systolic,
                onValueChange = { systolic = it },
                label = { Text(stringResource(R.string.systolic)) },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                isError = systolic.isNotBlank() && systolic.toIntOrNull() !in 40..250
            )

            OutlinedTextField(
                value = diastolic,
                onValueChange = { diastolic = it },
                label = { Text(stringResource(R.string.diastolic)) },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                isError = diastolic.isNotBlank() && diastolic.toIntOrNull() !in 30..150
            )

            OutlinedTextField(
                value = pulse,
                onValueChange = { pulse = it },
                label = { Text(stringResource(R.string.pulse)) },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                isError = pulse.isNotBlank() && pulse.toIntOrNull() !in 30..200
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = arrhythmia, onCheckedChange = { arrhythmia = it })
                Text(stringResource(R.string.arrhythmia))
            }

            Button(
                onClick = {
                    viewModel.saveMeasurement(
                        systolic.toInt(),
                        diastolic.toInt(),
                        pulse.toInt(),
                        arrhythmia,
                        userId
                    )
                    systolic = ""
                    diastolic = ""
                    pulse = ""
                    arrhythmia = false

                    scope.launch {
                        snackbarHostState.showSnackbar(
                            message = snackbarMessage
                        )
                    }
                },
                enabled = isValidInput,
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.Green)
            ) {
                Text(stringResource(R.string.save), color = Color.White)
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp),
            snackbar = { _ ->
                Snackbar {
                    Text(
                        text = snackbarMessage,
                        fontSize = 10.sp
                    )
                }
            }
        )
    }
}