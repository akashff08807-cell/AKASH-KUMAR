package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.ui.DoubleTapHeart
import com.example.ui.theme.TikTokRed
import kotlinx.coroutines.launch
import kotlin.random.Random

@Composable
fun FloatingHeartItem(
    heart: DoubleTapHeart,
    onAnimationEnd: (Long) -> Unit
) {
    val scale = remember { Animatable(0f) }
    val alpha = remember { Animatable(1f) }
    val yOffset = remember { Animatable(0f) }
    val rotation = remember { Random.nextFloat() * 40f - 20f }

    LaunchedEffect(heart.id) {
        launch {
            scale.animateTo(
                targetValue = 1.3f,
                animationSpec = tween(durationMillis = 200, easing = LinearOutSlowInEasing)
            )
            scale.animateTo(
                targetValue = 1.0f,
                animationSpec = tween(durationMillis = 150)
            )
        }
        launch {
            yOffset.animateTo(
                targetValue = -120f,
                animationSpec = tween(durationMillis = 750, easing = FastOutLinearInEasing)
            )
        }
        launch {
            alpha.animateTo(
                targetValue = 0f,
                animationSpec = tween(durationMillis = 750, delayMillis = 350)
            )
            onAnimationEnd(heart.id)
        }
    }

    val density = LocalDensity.current
    val xDp = with(density) { heart.x.toDp() }
    val yDp = with(density) { heart.y.toDp() }

    Box(
        modifier = Modifier
            .offset {
                IntOffset(
                    x = (heart.x - 45).toInt(),
                    y = (heart.y - 45 + yOffset.value).toInt()
                )
            }
            .scale(scale.value)
            .rotate(rotation)
            .alpha(alpha.value)
    ) {
        Icon(
            imageVector = Icons.Default.Favorite,
            contentDescription = null,
            tint = TikTokRed,
            modifier = Modifier.size(90.dp)
        )
    }
}
