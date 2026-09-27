package com.example.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.VideoItem
import com.example.ui.theme.TikTokCyan
import com.example.ui.theme.TikTokWhite

@Composable
fun VideoInfoOverlay(
    video: VideoItem,
    onAuthorClick: () -> Unit,
    onHashtagClick: (String) -> Unit,
    onSoundClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth(0.78f)
            .padding(start = 14.dp, bottom = 12.dp),
        verticalArrangement = Arrangement.Bottom
    ) {
        // Author handle + verified badge
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clickable { onAuthorClick() }
                .testTag("author_handle_${video.authorUsername}")
        ) {
            Text(
                text = "@${video.authorUsername}",
                color = TikTokWhite,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Verified",
                tint = TikTokCyan,
                modifier = Modifier.size(15.dp)
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Caption with hashtags and see more/less toggle
        val annotatedCaption = buildAnnotatedString {
            append(video.caption)
            if (video.hashtags.isNotEmpty()) {
                append(" ")
                video.hashtags.forEach { tag ->
                    withStyle(
                        style = SpanStyle(
                            color = TikTokWhite,
                            fontWeight = FontWeight.Bold
                        )
                    ) {
                        append("#$tag ")
                    }
                }
            }
        }

        Column(
            modifier = Modifier
                .animateContentSize()
                .clickable { isExpanded = !isExpanded }
        ) {
            Text(
                text = annotatedCaption,
                color = TikTokWhite.copy(alpha = 0.95f),
                fontSize = 14.sp,
                lineHeight = 18.sp,
                maxLines = if (isExpanded) 8 else 2,
                overflow = TextOverflow.Ellipsis
            )

            if (!isExpanded && video.caption.length > 70) {
                Text(
                    text = "See more",
                    color = TikTokWhite.copy(alpha = 0.6f),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Music Sound Bar Ticker
        val marqueeTransition = rememberInfiniteTransition(label = "marquee")
        val marqueeOffset by marqueeTransition.animateFloat(
            initialValue = 0f,
            targetValue = -120f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 5000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "sound_ticker"
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(Color.Black.copy(alpha = 0.35f))
                .clickable { onSoundClick() }
                .padding(horizontal = 8.dp, vertical = 4.dp)
                .testTag("sound_ticker")
        ) {
            Icon(
                imageVector = Icons.Default.MusicNote,
                contentDescription = "Sound",
                tint = TikTokWhite,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Box(
                modifier = Modifier
                    .width(180.dp)
                    .clip(RoundedCornerShape(4.dp))
            ) {
                Text(
                    text = "${video.soundTitle} - ${video.soundArtist}   •   ♫   •   ${video.soundTitle}",
                    color = TikTokWhite,
                    fontSize = 12.sp,
                    maxLines = 1,
                    modifier = Modifier.offset(x = marqueeOffset.dp)
                )
            }
        }
    }
}
