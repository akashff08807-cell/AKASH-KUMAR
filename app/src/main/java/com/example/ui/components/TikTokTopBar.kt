package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LiveTv
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.FeedTab
import com.example.ui.theme.TikTokRed
import com.example.ui.theme.TikTokWhite

@Composable
fun TikTokTopBar(
    currentTab: FeedTab,
    onTabSelected: (FeedTab) -> Unit,
    onLiveClick: () -> Unit,
    onSearchClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val livePulse = rememberInfiniteTransition(label = "live_pulse")
    val liveScale by livePulse.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Left: LIVE badge button
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(Color.Black.copy(alpha = 0.35f))
                .clickable { onLiveClick() }
                .padding(horizontal = 10.dp, vertical = 6.dp)
                .testTag("live_button")
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .scale(liveScale)
                    .clip(CircleShape)
                    .background(TikTokRed)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "LIVE",
                color = TikTokWhite,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // Center: Following | For You
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            TopTabItem(
                title = "Following",
                isSelected = currentTab == FeedTab.FOLLOWING,
                onClick = { onTabSelected(FeedTab.FOLLOWING) },
                testTag = "tab_following"
            )

            TopTabItem(
                title = "For You",
                isSelected = currentTab == FeedTab.FOR_YOU,
                onClick = { onTabSelected(FeedTab.FOR_YOU) },
                testTag = "tab_for_you"
            )
        }

        // Right: Search icon
        IconButton(
            onClick = onSearchClick,
            modifier = Modifier.testTag("search_icon_button")
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = TikTokWhite,
                modifier = Modifier.size(26.dp)
            )
        }
    }
}

@Composable
private fun TopTabItem(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() }
            .testTag(testTag)
    ) {
        Text(
            text = title,
            color = if (isSelected) TikTokWhite else TikTokWhite.copy(alpha = 0.6f),
            fontSize = 17.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .width(28.dp)
                .height(2.5.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(if (isSelected) TikTokWhite else Color.Transparent)
        )
    }
}
