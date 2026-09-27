package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Reply
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.VideoItem
import com.example.ui.theme.TikTokCyan
import com.example.ui.theme.TikTokRed
import com.example.ui.theme.TikTokWhite
import com.example.ui.theme.TikTokYellow

@Composable
fun TikTokSideActions(
    video: VideoItem,
    isPlaying: Boolean,
    onLikeClick: () -> Unit,
    onCommentClick: () -> Unit,
    onBookmarkClick: () -> Unit,
    onShareClick: () -> Unit,
    onAvatarClick: () -> Unit,
    onFollowClick: () -> Unit,
    onSoundClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(end = 8.dp, bottom = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Creator Avatar + Follow badge
        Box(
            modifier = Modifier.padding(bottom = 6.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            listOf(TikTokCyan, TikTokRed)
                        )
                    )
                    .border(1.5.dp, Color.White, CircleShape)
                    .testTag("creator_avatar_${video.id}")
                    .clickable { onAvatarClick() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = video.authorName.take(1).uppercase(),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            }

            // Follow Plus Badge
            if (!video.isFollowing) {
                Box(
                    modifier = Modifier
                        .offset(y = 10.dp)
                        .size(22.dp)
                        .clip(CircleShape)
                        .background(TikTokRed)
                        .border(1.dp, Color.White, CircleShape)
                        .testTag("follow_button_${video.id}")
                        .clickable { onFollowClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Follow",
                        tint = Color.White,
                        modifier = Modifier.size(15.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(2.dp))

        // 2. Like Button
        val likeScale by animateFloatAsState(
            targetValue = if (video.isLiked) 1.25f else 1.0f,
            animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
            label = "like_bounce"
        )
        ActionButtonItem(
            icon = if (video.isLiked) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
            iconTint = if (video.isLiked) TikTokRed else TikTokWhite,
            countText = formatCount(video.likeCount),
            testTag = "like_button",
            scale = likeScale,
            onClick = onLikeClick
        )

        // 3. Comments Button
        ActionButtonItem(
            icon = Icons.Outlined.ChatBubbleOutline,
            iconTint = TikTokWhite,
            countText = formatCount(video.commentCount),
            testTag = "comments_button",
            onClick = onCommentClick
        )

        // 4. Bookmark / Favorite Button
        val bookmarkScale by animateFloatAsState(
            targetValue = if (video.isBookmarked) 1.2f else 1.0f,
            animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
            label = "bookmark_bounce"
        )
        ActionButtonItem(
            icon = if (video.isBookmarked) Icons.Default.Bookmark else Icons.Outlined.BookmarkBorder,
            iconTint = if (video.isBookmarked) TikTokYellow else TikTokWhite,
            countText = formatCount(video.bookmarkCount),
            testTag = "bookmark_button",
            scale = bookmarkScale,
            onClick = onBookmarkClick
        )

        // 5. Share Button
        ActionButtonItem(
            icon = Icons.Default.Reply,
            iconTint = TikTokWhite,
            countText = formatCount(video.shareCount),
            testTag = "share_button",
            onClick = onShareClick
        )

        Spacer(modifier = Modifier.height(4.dp))

        // 6. Spinning Vinyl Record
        SpinningVinylDisc(
            isPlaying = isPlaying,
            onClick = onSoundClick
        )
    }
}

@Composable
private fun ActionButtonItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    countText: String,
    testTag: String,
    scale: Float = 1f,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .testTag(testTag)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() }
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(44.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier
                    .size(36.dp)
                    .scale(scale)
            )
        }
        Text(
            text = countText,
            color = TikTokWhite,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center
        )
    }
}

private fun formatCount(count: Long): String {
    return when {
        count >= 1_000_000 -> String.format("%.1fM", count / 1_000_000.0)
        count >= 10_000 -> String.format("%.1fK", count / 1_000.0)
        count >= 1_000 -> String.format("%.1fK", count / 1_000.0)
        else -> count.toString()
    }
}
