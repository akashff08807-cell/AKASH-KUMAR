package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.data.model.VideoItem
import com.example.ui.theme.TikTokCyan
import com.example.ui.theme.TikTokRed
import com.example.ui.theme.TikTokWhite
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun SimulatedVideoCanvas(
    video: VideoItem,
    isPlaying: Boolean,
    onSingleTap: () -> Unit,
    onDoubleTap: (x: Float, y: Float) -> Unit,
    modifier: Modifier = Modifier
) {
    var progress by remember(video.id) { mutableFloatStateOf(0f) }

    LaunchedEffect(video.id, isPlaying) {
        if (isPlaying) {
            val totalSteps = (video.durationSeconds * 30).coerceAtLeast(60)
            while (true) {
                delay(1000L / 30L)
                progress = (progress + 1f / totalSteps) % 1f
            }
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "video_fx")
    val pulsePhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6.28318f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulse_phase"
    )

    val beatScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "beat"
    )

    val waveOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wave"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .testTag("video_player_${video.id}")
            .pointerInput(video.id) {
                detectTapGestures(
                    onTap = { onSingleTap() },
                    onDoubleTap = { offset ->
                        onDoubleTap(offset.x, offset.y)
                    }
                )
            }
    ) {
        // High fidelity visual canvas simulation
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            // 1. Dynamic background gradient from video theme colors
            val baseColors = video.videoGradientColors.map { Color(it) }
            val centerColor = baseColors.getOrElse(0) { Color(0xFF0F2027) }
            val midColor = baseColors.getOrElse(1) { Color(0xFF203A43) }
            val accentColor = baseColors.getOrElse(2) { Color(0xFF2C5364) }

            val brush = Brush.radialGradient(
                colors = listOf(
                    midColor.copy(alpha = 0.85f),
                    centerColor.copy(alpha = 0.95f),
                    Color(0xFF060608)
                ),
                center = Offset(
                    width * (0.5f + 0.15f * sin(pulsePhase)),
                    height * (0.4f + 0.15f * cos(pulsePhase))
                ),
                radius = width * 1.2f * (if (isPlaying) beatScale else 1f)
            )
            drawRect(brush = brush)

            // 2. Audio frequency visualizer bars dancing in the background
            val barCount = 28
            val barWidth = (width / barCount) * 0.7f
            val baseBarY = height * 0.58f

            for (i in 0 until barCount) {
                val factor = sin(pulsePhase * 2f + i * 0.45f) * 0.5f + 0.5f
                val activeHeight = if (isPlaying) (30f + factor * 140f * beatScale) else 20f
                val barX = (i + 0.5f) * (width / barCount)

                val barColor = if (i % 2 == 0) {
                    TikTokCyan.copy(alpha = 0.22f + factor * 0.25f)
                } else {
                    TikTokRed.copy(alpha = 0.22f + factor * 0.25f)
                }

                drawLine(
                    color = barColor,
                    start = Offset(barX, baseBarY - activeHeight / 2),
                    end = Offset(barX, baseBarY + activeHeight / 2),
                    strokeWidth = barWidth,
                    cap = StrokeCap.Round
                )
            }

            // 3. Ambient lighting ripples / neon aura rings
            val ringRadius = (width * 0.35f) * (if (isPlaying) beatScale else 1f)
            drawCircle(
                color = accentColor.copy(alpha = 0.25f),
                radius = ringRadius,
                center = Offset(width * 0.5f, height * 0.45f),
                style = Stroke(width = 8f)
            )
            drawCircle(
                color = TikTokCyan.copy(alpha = 0.15f),
                radius = ringRadius * 1.35f,
                center = Offset(width * 0.5f, height * 0.45f),
                style = Stroke(width = 4f)
            )

            // 4. Content theme visual silhouettes and particle effects
            when (video.videoType) {
                "dance" -> {
                    // Stage spotlight sweeps & disco sparkles
                    val spotX = width * (0.5f + 0.35f * sin(pulsePhase))
                    drawLine(
                        brush = Brush.verticalGradient(
                            listOf(Color.White.copy(alpha = 0.25f), Color.Transparent)
                        ),
                        start = Offset(spotX, 0f),
                        end = Offset(width * 0.5f, height * 0.8f),
                        strokeWidth = width * 0.5f
                    )
                }
                "tech" -> {
                    // Neon grid waves & cyber lines
                    for (lineIndex in 1..5) {
                        val yPos = height * 0.2f + lineIndex * 70f
                        drawLine(
                            color = TikTokCyan.copy(alpha = 0.2f),
                            start = Offset(0f, yPos),
                            end = Offset(width, yPos),
                            strokeWidth = 2f
                        )
                    }
                }
                "food" -> {
                    // Golden warmth glow
                    drawCircle(
                        brush = Brush.radialGradient(
                            listOf(Color(0xFFFFB300).copy(alpha = 0.3f), Color.Transparent),
                            center = Offset(width * 0.5f, height * 0.45f),
                            radius = width * 0.5f
                        ),
                        center = Offset(width * 0.5f, height * 0.45f),
                        radius = width * 0.5f
                    )
                }
                "travel" -> {
                    // Horizon mountains path
                    val mountainPath = Path().apply {
                        moveTo(0f, height * 0.55f)
                        lineTo(width * 0.3f, height * 0.42f)
                        lineTo(width * 0.55f, height * 0.5f)
                        lineTo(width * 0.8f, height * 0.38f)
                        lineTo(width, height * 0.52f)
                        lineTo(width, height * 0.7f)
                        lineTo(0f, height * 0.7f)
                        close()
                    }
                    drawPath(
                        path = mountainPath,
                        color = Color(0xFF001F3F).copy(alpha = 0.4f)
                    )
                }
            }

            // 5. Floating ambient dust/beat particles
            for (p in 0..12) {
                val pX = (width * ((p * 0.083f + waveOffset * 0.2f) % 1f))
                val pY = (height * ((p * 0.077f + pulsePhase * 0.05f) % 0.8f + 0.1f))
                val pRadius = 2f + (p % 3) * 2f
                drawCircle(
                    color = Color.White.copy(alpha = 0.35f + (p % 4) * 0.1f),
                    radius = pRadius,
                    center = Offset(pX, pY)
                )
            }

            // 6. Vignette overlays for readability of captions & actions
            // Bottom gradient
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.85f)),
                    startY = height * 0.6f,
                    endY = height
                )
            )
            // Top gradient
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(Color.Black.copy(alpha = 0.75f), Color.Transparent),
                    startY = 0f,
                    endY = height * 0.22f
                )
            )
        }

        // Center Pause Indicator
        AnimatedVisibility(
            visible = !isPlaying,
            enter = scaleIn(initialScale = 0.5f) + fadeIn(),
            exit = scaleOut(targetScale = 1.3f) + fadeOut(),
            modifier = Modifier.align(Alignment.Center)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(76.dp)
                    .background(Color.Black.copy(alpha = 0.45f), shape = CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Paused",
                    tint = TikTokWhite.copy(alpha = 0.9f),
                    modifier = Modifier.size(48.dp)
                )
            }
        }

        // Bottom Progress Indicator (TikTok thin scrubber)
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(2.dp),
            color = TikTokWhite.copy(alpha = 0.8f),
            trackColor = Color.White.copy(alpha = 0.15f)
        )
    }
}
