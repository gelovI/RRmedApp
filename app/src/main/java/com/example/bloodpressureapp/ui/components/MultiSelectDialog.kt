package com.example.bloodpressureapp.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.AlertDialog
import androidx.compose.material.Checkbox
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Universeller Mehrfachauswahl-Dialog.
 *
 * @param title Überschrift des Dialogs
 * @param items Liste beliebiger Objekte
 * @param idOf Funktion, die die eindeutige ID des Items liefert
 * @param labelOf Funktion, die den Haupttext des Items liefert
 * @param extraOf Funktion, die optionalen Zusatztext liefert (z.B. "12 Messungen")
 * @param initiallyChecked IDs, die beim Öffnen schon ausgewählt sind
 * @param onDismiss Wird aufgerufen, wenn der Dialog geschlossen wird
 * @param onConfirm Wird mit den gewählten IDs aufgerufen
 */
@Composable
fun <T> UserMultiSelectDialog(
    title: String,
    items: List<T>,
    idOf: (T) -> Int,
    labelOf: (T) -> String,
    extraOf: (T) -> String? = { null },
    initiallyChecked: Set<Int> = emptySet(),
    onDismiss: () -> Unit,
    onConfirm: (selectedIds: Set<Int>) -> Unit
) {
    val checked = remember(items) {
        mutableStateMapOf<Int, Boolean>().apply {
            initiallyChecked.forEach { this[it] = true }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 420.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(items) { item ->
                    val id = idOf(item)
                    val label = labelOf(item)
                    val extra = extraOf(item)

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = checked[id] == true,
                            onCheckedChange = { isChecked ->
                                if (isChecked) checked[id] = true else checked.remove(id)
                            }
                        )
                        Spacer(Modifier.width(8.dp))
                        Column {
                            Text(label, style = MaterialTheme.typography.body1)
                            if (!extra.isNullOrBlank()) {
                                Text(
                                    extra,
                                    style = MaterialTheme.typography.caption
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onConfirm(checked.keys)
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Abbrechen")
            }
        }
    )
}
