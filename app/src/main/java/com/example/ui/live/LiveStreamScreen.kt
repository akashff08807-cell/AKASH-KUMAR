package com.example.ui.live

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.LiveChatMessage
import com.example.ui.theme.TikTokCard
import com.example.ui.theme.TikTokCyan
import com.example.ui.theme.TikTokDarkSurface
import com.example.ui.theme.TikTokRed
import com.example.ui.theme.TikTokTextSecondary
import com.example.ui.theme.TikTokWhite
import com.example.ui.theme.TikTokYellow
import kotlin.math.sin

data class FloatingLiveHeart(
    val id: Long = System.currentTimeMillis(),
    val color: Color,
    val initialX: Float
)

@Composable
fun LiveStreamScreen(
    messages: List<LiveChatMessage>,
    likesCount: Long,
    onClose: () -> Unit,
    onSendMessage: (String) -> Unit,
    onSendGift: (String) -> Unit,
    onSendHeart: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onClose() }

    var commentText by remember { mutableStateOf("") }
    var showGiftSheet by remember { mutableStateOf(false) }
    var isFollowingHost by remember { mutableStateOf(false) }
    val floatingHearts = remember { mutableStateListOf<FloatingLiveHeart>() }

    val listState = rememberLazyListState()
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    val livePulse = rememberInfiniteTransition(label = "live_bg")
    val bgPhase by livePulse.animateFloat(
        initialValue = 0f,
        targetValue = 6.28f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "bg_phase"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .testTag("live_stream_screen")
    ) {
        // Broadcaster Video Simulation Canvas
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            val brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFF2C3E50),
                    Color(0xFF3498DB).copy(alpha = 0.6f),
                    Color(0xFF0F2027)
                ),
                center = Offset(w * (0.5f + 0.1f * sin(bgPhase)), h * 0.45f),
                radius = w * 1.3f
            )
            drawRect(brush = brush)

            // Stage lighting rings
            drawCircle(
                color = TikTokCyan.copy(alpha = 0.2f),
                center = Offset(w * 0.5f, h * 0.45f),
                radius = w * 0.35f
            )

            // Streamer silhouette indicator
            drawCircle(
                color = Color.White.copy(alpha = 0.15f),
                center = Offset(w * 0.5f, h * 0.35f),
                radius = w * 0.16f
            )
        }

        // Top Streamer Info Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Host Pill
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color.Black.copy(alpha = 0.5f))
                    .padding(end = 12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(listOf(TikTokCyan, TikTokRed))
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "C", color = Color.White, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.width(8.dp))

                Column {
                    Text(
                        text = "Charli Dance",
                        color = TikTokWhite,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "14.2K viewers",
                        color = TikTokTextSecondary,
                        fontSize = 10.sp
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                // Follow Button
                if (!isFollowingHost) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(TikTokRed)
                            .clickable { isFollowingHost = true }
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "Follow",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Right Close Button
            IconButton(
                onClick = onClose,
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.5f))
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = TikTokWhite,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // Floating Hearts rising upwards
        for (heart in floatingHearts) {
            key(heart.id) {
                FloatingLiveHeartItem(
                    heart = heart,
                    onEnd = { floatingHearts.remove(heart) }
                )
            }
        }

        // Live Chat Feed (Bottom Left)
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth(0.75f)
                .navigationBarsPadding()
                .padding(start = 12.dp, bottom = 64.dp)
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier.height(180.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(messages) { msg ->
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color.Black.copy(alpha = 0.45f))
                            .padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (msg.badge.isNotEmpty()) {
                            Text(text = msg.badge, fontSize = 12.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                        }
                        Text(
                            text = msg.username,
                            color = TikTokCyan,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = msg.text,
                            color = TikTokWhite,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        // Bottom Controls Bar
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = commentText,
                onValueChange = { commentText = it },
                placeholder = {
                    Text("Say something nice...", color = TikTokTextSecondary, fontSize = 13.sp)
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.Black.copy(alpha = 0.5f),
                    unfocusedContainerColor = Color.Black.copy(alpha = 0.5f),
                    focusedBorderColor = TikTokCyan,
                    unfocusedBorderColor = Color(0xFF333333),
                    focusedTextColor = TikTokWhite,
                    unfocusedTextColor = TikTokWhite
                ),
                shape = RoundedCornerShape(20.dp),
                trailingIcon = {
                    if (commentText.isNotBlank()) {
                        IconButton(onClick = {
                            onSendMessage(commentText.trim())
                            commentText = ""
                        }) {
                            Icon(
                                imageVector = Icons.Default.Send,
                                contentDescription = "Send",
                                tint = TikTokRed,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .height(46.dp)
            )

            // Gift Button
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(TikTokYellow)
                    .clickable { showGiftSheet = true },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CardGiftcard,
                    contentDescription = "Send Gift",
                    tint = Color.Black,
                    modifier = Modifier.size(22.dp)
                )
            }

            // Rapid Heart Button
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(TikTokRed)
                    .clickable {
                        onSendHeart()
                        val heartColors = listOf(TikTokRed, TikTokCyan, TikTokYellow, Color(0xFFE91E63))
                        floatingHearts.add(
                            FloatingLiveHeart(
                                color = heartColors.random(),
                                initialX = (kotlin.random.Random.nextFloat() * 40f - 20f)
                            )
                        )
                    }
                    .testTag("live_heart_button"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "Heart",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        // Gift Picker Sheet
        if (showGiftSheet) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .clickable { showGiftSheet = false },
                contentAlignment = Alignment.BottomCenter
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                        .background(TikTokDarkSurface)
                        .clickable(enabled = false) {}
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Send a Gift",
                        color = TikTokWhite,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 14.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        listOf(
                            "Rose 🌹" to 1,
                            "Star ⭐" to 10,
                            "Crown 👑" to 50,
                            "Dragon 🐉" to 100
                        ).forEach { (gift, coins) ->
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .clickable {
                                        onSendGift(gift)
                                        showGiftSheet = false
                                    }
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(TikTokCard)
                                    .padding(horizontal = 14.dp, vertical = 10.dp)
                            ) {
                                Text(text = gift.split(" ")[1], fontSize = 28.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = gift.split(" ")[0], color = TikTokWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Text(text = "$coins coins", color = TikTokYellow, fontSize = 10.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FloatingLiveHeartItem(
    heart: FloatingLiveHeart,
    onEnd: () -> Unit
) {
    val progress = remember { androidx.compose.animation.core.Animatable(0f) }

    LaunchedEffect(heart.id) {
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(1500, easing = LinearEasing)
        )
        onEnd()
    }

    val y = -progress.value * 350f
    val x = heart.initialX + sin(progress.value * 6.28f) * 30f
    val alpha = (1f - progress.value).coerceIn(0f, 1f)

    Box(
        modifier = Modifier
            .offset(x = (280 + x).dp, y = (600 + y).dp)
            .scale(0.8f + progress.value * 0.4f)
    ) {
        Icon(
            imageVector = Icons.Default.Favorite,
            contentDescription = null,
            tint = heart.color.copy(alpha = alpha),
            modifier = Modifier.size(32.dp)
        )
    }
}
