package com.example.bloodpressureapp.ui.theme

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bloodpressureapp.R
import com.example.bloodpressureapp.viewmodel.AppViewModel
import com.example.bloodpressureapp.data.Therapy

@Composable
fun TherapyFormSection(
    viewModel: AppViewModel,
    userId: Int,
    editMode: Therapy?,
    setEditMode: (Therapy?) -> Unit,
    name: String,
    setName: (String) -> Unit,
    dosage: String,
    setDosage: (String) -> Unit
) {
    var showForm by remember { mutableStateOf(false) }

    // Automatisch aufklappen bei Bearbeiten
    LaunchedEffect(editMode) {
        if (editMode != null) showForm = true
    }

    TextButton(onClick = { showForm = !showForm }) {
        if (!showForm) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                tint = MaterialTheme.colors.primary
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
        Text(
            text = if (showForm) stringResource(R.string.hide_therapy_input) else stringResource(R.string.new_therapy),
            fontSize = 12.sp,
            color = MaterialTheme.colors.primary
        )
    }

    AnimatedVisibility(visible = showForm) {
        Column(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = name,
                onValueChange = setName,
                label = { Text(stringResource(R.string.medication_name), fontSize = 14.sp) },
                textStyle = LocalTextStyle.current.copy(fontSize = 14.sp),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = dosage,
                onValueChange = setDosage,
                label = { Text(stringResource(R.string.dosage_changes), fontSize = 14.sp) },
                textStyle = LocalTextStyle.current.copy(fontSize = 14.sp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    if (name.isNotBlank() && dosage.isNotBlank()) {
                        if (editMode == null) {
                            viewModel.saveTherapy(name, dosage, userId)
                        } else {
                            viewModel.updateTherapy(editMode.copy(name = name, dosage = dosage))
                            setEditMode(null)
                        }
                        setName("")
                        setDosage("")
                        showForm = false
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = stringResource(if (editMode == null) R.string.save_changes else R.string.apply_changes))
            }
        }
    }
}
