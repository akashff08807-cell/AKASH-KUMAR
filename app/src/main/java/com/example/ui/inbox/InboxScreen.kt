package com.example.ui.inbox

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.TikTokCard
import com.example.ui.theme.TikTokCyan
import com.example.ui.theme.TikTokOnlineGreen
import com.example.ui.theme.TikTokRed
import com.example.ui.theme.TikTokTextSecondary
import com.example.ui.theme.TikTokWhite

data class StoryItem(val name: String, val color: Color, val hasStory: Boolean = true)
data class DirectMessage(val name: String, val handle: String, val message: String, val time: String, val unread: Boolean, val color: Color)

@Composable
fun InboxScreen(
    onCreatorClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val stories = listOf(
        StoryItem("Your Story", TikTokCyan, hasStory = false),
        StoryItem("Charli", Color(0xFFE91E63)),
        StoryItem("TechLead", Color(0xFF009688)),
        StoryItem("Chef Mario", Color(0xFFFF9800)),
        StoryItem("Kai", Color(0xFF4A90E2)),
        StoryItem("Leo", Color(0xFF9C27B0))
    )

    val dms = listOf(
        DirectMessage("Charli Dance", "charlidance", "Loved the video response! Let's duet soon!", "15m", true, Color(0xFFE91E63)),
        DirectMessage("TechLead Pro", "techlead_pro", "Check out the repo I sent you on GitHub 🚀", "1h", true, Color(0xFF009688)),
        DirectMessage("Chef Mario", "gourmet_kitchen", "Did you try the smash potato recipe yet?!", "3h", false, Color(0xFFFF9800)),
        DirectMessage("Kai Travels", "travelwithkai", "Next stop is Iceland! 🇮🇸 You should come!", "Yesterday", false, Color(0xFF4A90E2))
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .statusBarsPadding()
            .testTag("inbox_screen")
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Inbox",
                color = TikTokWhite,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Icon(
                imageVector = Icons.Default.Chat,
                contentDescription = "New Message",
                tint = TikTokWhite,
                modifier = Modifier.size(24.dp)
            )
        }

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            // Stories Row
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    stories.forEach { story ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.clickable {
                                if (story.hasStory) onCreatorClick(story.name.lowercase().replace(" ", ""))
                            }
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(62.dp)
                                    .clip(CircleShape)
                                    .then(
                                        if (story.hasStory) {
                                            Modifier.border(
                                                2.5.dp,
                                                Brush.linearGradient(listOf(TikTokCyan, TikTokRed)),
                                                CircleShape
                                            )
                                        } else {
                                            Modifier.border(1.5.dp, Color(0xFF333333), CircleShape)
                                        }
                                    )
                                    .padding(3.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(CircleShape)
                                        .background(story.color),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = story.name.take(1),
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = story.name,
                                color = TikTokWhite,
                                fontSize = 11.sp,
                                maxLines = 1
                            )
                        }
                    }
                }
            }

            // Activity section
            item {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Activities",
                    color = TikTokTextSecondary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                )
            }

            // New followers item
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { }
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(TikTokRed),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.PersonAdd, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "New followers", color = TikTokWhite, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text(text = "sarah_j and 14 others followed you", color = TikTokTextSecondary, fontSize = 12.sp)
                    }
                    Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TikTokTextSecondary)
                }
            }

            // Likes activity item
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { }
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE91E63)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Favorite, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Activity", color = TikTokWhite, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text(text = "Charli Dance and 128 others liked your video", color = TikTokTextSecondary, fontSize = 12.sp)
                    }
                    Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TikTokTextSecondary)
                }
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider(color = Color(0xFF1E1E1E), thickness = 0.5.dp)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Messages",
                    color = TikTokTextSecondary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                )
            }

            // Direct Messages
            items(dms.size) { index ->
                val dm = dms[index]
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onCreatorClick(dm.handle) }
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                        .testTag("dm_row_${dm.handle}"),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(contentAlignment = Alignment.BottomEnd) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(dm.color),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = dm.name.take(1), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        }
                        // Online indicator
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .clip(CircleShape)
                                .background(TikTokOnlineGreen)
                                .border(1.5.dp, Color.Black, CircleShape)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(text = dm.name, color = TikTokWhite, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text(text = dm.time, color = TikTokTextSecondary, fontSize = 11.sp)
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = dm.message,
                            color = if (dm.unread) TikTokWhite else TikTokTextSecondary,
                            fontWeight = if (dm.unread) FontWeight.SemiBold else FontWeight.Normal,
                            fontSize = 13.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    if (dm.unread) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(TikTokCyan)
                        )
                    }
                }
            }
        }
    }
}
