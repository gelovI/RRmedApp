package com.example.bloodpressureapp.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.bloodpressureapp.R

@Composable
fun SwipeableCard(
    isRevealed: Boolean,
    onReveal: () -> Unit,
    onReset: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    content: @Composable () -> Unit
) {
    val targetOffset = if (isRevealed) -120f else 0f
    var offsetX by remember { mutableFloatStateOf(targetOffset) }
    val animatedOffsetX: Dp by animateDpAsState(targetValue = offsetX.dp, label = "swipeOffset")

    val swipeThreshold = -100f

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 2.dp, vertical = 4.dp)
    ) {
        // Hintergrund mit Edit / Delete Buttons
        Row(
            modifier = Modifier
                .matchParentSize()
                .clip(RoundedCornerShape(8.dp))
                .background(Color.LightGray),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onEdit) {
                Icon(
                    Icons.Default.Edit,
                    contentDescription = stringResource(R.string.edit),
                    tint = Color.DarkGray
                )
            }
            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = stringResource(R.string.delete),
                    tint = MaterialTheme.colors.error
                )
            }
        }

        Card(
            shape = RoundedCornerShape(8.dp),
            elevation = 6.dp,
            modifier = Modifier
                .offset(x = animatedOffsetX)
                .fillMaxWidth()
                .pointerInput(isRevealed) {
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            if (offsetX < swipeThreshold) {
                                offsetX = -120f
                                onReveal()
                            } else {
                                offsetX = 0f
                                onReset()
                            }
                        },
                        onHorizontalDrag = { _, dragAmount ->
                            offsetX = (offsetX + dragAmount).coerceIn(-120f, 0f)
                        }
                    )
                }
        ) {
            content()
        }
    }
}