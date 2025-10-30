package com.example.bloodpressureapp.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bloodpressureapp.R

@Composable
fun MeasurementCardContent(
    time: String,
    systolic: Int,
    diastolic: Int,
    pulse: Int,
    arrhythmia: Boolean,
    onInfoClick: () -> Unit,
    showCheckbox: Boolean = false,
    checked: Boolean = false,
    onCheckedChange: (Boolean) -> Unit = {},
) {
    Column(Modifier.fillMaxWidth().padding(16.dp)) {

        // Kopfzeile
        Box(Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
            if (showCheckbox) {
                Checkbox(
                    checked = checked,
                    onCheckedChange = onCheckedChange,
                    modifier = Modifier.align(Alignment.CenterStart)
                )
            } else {
                Spacer(Modifier.width(48.dp).align(Alignment.CenterStart))
            }

            Text(
                text = stringResource(R.string.reminder_time, time),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .align(Alignment.Center)
                    .background(Color.White)
                    .padding(horizontal = 2.dp, vertical = 2.dp)
            )
            Box(modifier = Modifier.align(Alignment.CenterEnd)) {
                InfoCirclePulsing(onClick = onInfoClick, enabled = true)
            }
        }

        Spacer(Modifier.height(16.dp))

        // Wertezeile (unverändert)
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            ValueColumn(stringResource(R.string.overview_systolic), systolic.toString())
            DividerLine()
            ValueColumn(stringResource(R.string.overview_diastolic), diastolic.toString())
            DividerLine()
            ValueColumn(stringResource(R.string.overview_pulse), pulse.toString())
            DividerLine()
            ValueColumn(
                stringResource(R.string.overview_arrhythmia),
                if (arrhythmia) stringResource(R.string.overview_yes) else stringResource(R.string.overview_no)
            )
        }
    }
}

@Composable
fun InfoCirclePulsing(
    onClick: () -> Unit,
    pulseColor: Color = Color(0xFF1976D2),
    enabled: Boolean = true,         // Pulse ein/aus
) {
    val bg = pulseColor
    val fg = Color.White

    val t by rememberInfiniteTransition(label = "pulse")
        .animateFloat(
            initialValue = 0f,
            targetValue   = 1f,
            animationSpec = infiniteRepeatable(
                animation  = tween(durationMillis = 1500, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "t"
        )

    // Container zeichnet die Wellen hinter dem Button
    Box(
        modifier = Modifier
            .size(28.dp) // Button-Größe, Wellen gehen darüber hinaus
            .drawBehind {
                if (!enabled) return@drawBehind
                val baseR = size.minDimension / 2f
                val maxR  = baseR * 2.1f  // wie weit die Welle rausgeht

                repeat(3) { i ->
                    val phase = ((t + i / 3f) % 1f)
                    val r     = androidx.compose.ui.util.lerp(baseR, maxR, phase)
                    val a     = (1f - phase) * 0.35f
                    drawCircle(
                        color = pulseColor.copy(alpha = a),
                        radius = r,
                        center = center
                    )
                }
            }
            .clip(CircleShape)
            .background(bg)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(bounded = true, radius = 16.dp),
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Outlined.Info,
            contentDescription = stringResource(R.string.info_btn),
            tint = fg,
            modifier = Modifier.size(16.dp)
        )
    }
}
