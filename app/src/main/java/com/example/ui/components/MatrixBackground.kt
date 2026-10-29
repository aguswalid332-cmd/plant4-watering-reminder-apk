package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.example.ui.theme.CyberBlack
import com.example.ui.theme.CyberDark
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen

@Composable
fun MatrixBackground(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "matrix_anim")
    val offsetY by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 15000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "matrix_offset"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        // Base cyberpunk dark gradient
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    CyberBlack,
                    CyberDark,
                    CyberBlack
                )
            )
        )

        val width = size.width
        val height = size.height

        // Subtle digital grid lines
        val step = 40f
        var x = 0f
        while (x < width) {
            drawLine(
                color = NeonCyan.copy(alpha = 0.04f),
                start = Offset(x, 0f),
                end = Offset(x, height),
                strokeWidth = 1f
            )
            x += step
        }

        var y = 0f
        while (y < height) {
            drawLine(
                color = NeonCyan.copy(alpha = 0.04f),
                start = Offset(0f, y),
                end = Offset(width, y),
                strokeWidth = 1f
            )
            y += step
        }

        // Falling digital particles
        val columns = 16
        val colWidth = width / columns
        for (i in 0 until columns) {
            val speedFactor = ((i * 37) % 7 + 5) / 10f
            val dotY = (offsetY * speedFactor + (i * 123f)) % height
            val dotX = i * colWidth + (colWidth / 2)
            drawCircle(
                color = if (i % 2 == 0) NeonCyan.copy(alpha = 0.25f) else NeonGreen.copy(alpha = 0.25f),
                radius = 2.5f,
                center = Offset(dotX, dotY)
            )
            drawLine(
                brush = Brush.verticalGradient(
                    colors = listOf(Color.Transparent, NeonCyan.copy(alpha = 0.2f)),
                    startY = (dotY - 25f).coerceAtLeast(0f),
                    endY = dotY
                ),
                start = Offset(dotX, (dotY - 25f).coerceAtLeast(0f)),
                end = Offset(dotX, dotY),
                strokeWidth = 1.5f
            )
        }
    }
}
