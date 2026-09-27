package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.TikTokCyan
import com.example.ui.theme.TikTokRed
import com.example.ui.theme.TikTokWhite
import kotlin.math.sin

@Composable
fun SpinningVinylDisc(
    isPlaying: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "vinyl")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    val noteProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "note"
    )

    Box(
        modifier = modifier
            .testTag("vinyl_disc")
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        // Drifting musical notes when playing
        if (isPlaying) {
            val note1X = -12f - noteProgress * 24f + sin(noteProgress * 6.28f) * 8f
            val note1Y = -16f - noteProgress * 48f
            val alpha1 = (1f - noteProgress).coerceIn(0f, 1f)

            val note2Progress = (noteProgress + 0.5f) % 1f
            val note2X = -20f - note2Progress * 20f + sin(note2Progress * 6.28f + 1f) * 6f
            val note2Y = -12f - note2Progress * 52f
            val alpha2 = (1f - note2Progress).coerceIn(0f, 1f)

            Text(
                text = "♪",
                color = TikTokWhite.copy(alpha = alpha1),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.offset(x = note1X.dp, y = note1Y.dp)
            )

            Text(
                text = "♫",
                color = TikTokCyan.copy(alpha = alpha2),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.offset(x = note2X.dp, y = note2Y.dp)
            )
        }

        // Vinyl Record Base
        Box(
            modifier = Modifier
                .size(46.dp)
                .rotate(if (isPlaying) rotation else 0f)
                .clip(CircleShape)
                .background(Color(0xFF1E1E1E))
                .border(2.dp, Color(0xFF2E2E2E), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            // Grooved vinyl tracks
            Canvas(modifier = Modifier.size(46.dp)) {
                drawCircle(color = Color(0xFF121212), radius = size.minDimension * 0.46f)
                drawCircle(
                    color = Color(0xFF333333),
                    radius = size.minDimension * 0.36f,
                    style = Stroke(width = 1.5f)
                )
                drawCircle(
                    color = Color(0xFF282828),
                    radius = size.minDimension * 0.28f,
                    style = Stroke(width = 1.5f)
                )
            }

            // Center album sticker
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .clip(CircleShape)
                    .background(TikTokRed),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(Color.Black)
                )
            }
        }
    }
}
