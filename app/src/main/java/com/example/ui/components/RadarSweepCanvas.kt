package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen

@Composable
fun RadarSweepCanvas(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "radar_sweep")
    val angle by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "radar_angle"
    )

    Canvas(modifier = modifier) {
        val center = Offset(size.width / 2, size.height / 2)
        val radius = minOf(size.width, size.height) / 2

        // Concentric radar rings
        drawCircle(
            color = NeonCyan.copy(alpha = 0.25f),
            radius = radius * 0.95f,
            center = center,
            style = Stroke(width = 1.5f)
        )
        drawCircle(
            color = NeonCyan.copy(alpha = 0.15f),
            radius = radius * 0.65f,
            center = center,
            style = Stroke(width = 1f)
        )
        drawCircle(
            color = NeonCyan.copy(alpha = 0.10f),
            radius = radius * 0.35f,
            center = center,
            style = Stroke(width = 1f)
        )

        // Crosshairs
        drawLine(
            color = NeonCyan.copy(alpha = 0.12f),
            start = Offset(center.x - radius, center.y),
            end = Offset(center.x + radius, center.y),
            strokeWidth = 1f
        )
        drawLine(
            color = NeonCyan.copy(alpha = 0.12f),
            start = Offset(center.x, center.y - radius),
            end = Offset(center.x, center.y + radius),
            strokeWidth = 1f
        )

        // Rotating radar beam
        rotate(degrees = angle, pivot = center) {
            drawLine(
                brush = Brush.horizontalGradient(
                    colors = listOf(Color.Transparent, NeonGreen.copy(alpha = 0.5f), NeonCyan),
                    startX = center.x,
                    endX = center.x + radius
                ),
                start = center,
                end = Offset(center.x + radius, center.y),
                strokeWidth = 2.5f
            )
        }
    }
}
