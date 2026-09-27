package com.example.ui.components

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.HeartBroken
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Splitscreen
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.VideoItem
import com.example.ui.theme.TikTokCard
import com.example.ui.theme.TikTokCyan
import com.example.ui.theme.TikTokDarkSurface
import com.example.ui.theme.TikTokRed
import com.example.ui.theme.TikTokTextSecondary
import com.example.ui.theme.TikTokWhite
import com.example.ui.theme.TikTokYellow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShareBottomSheet(
    video: VideoItem,
    onDismiss: () -> Unit,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
) {
    val context = LocalContext.current
    val sentMap = remember { mutableStateMapOf<String, Boolean>() }

    val friends = listOf(
        "Alex" to Color(0xFF4A90E2),
        "Jessica" to Color(0xFFE91E63),
        "Marcus" to Color(0xFF009688),
        "Emily" to Color(0xFFFF9800),
        "David" to Color(0xFF9C27B0),
        "Sophia" to Color(0xFF3F51B5)
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = TikTokDarkSurface,
        dragHandle = null,
        modifier = Modifier.testTag("share_bottom_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(vertical = 16.dp)
        ) {
            Text(
                text = "Send to",
                color = TikTokWhite,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Friends horizontal scroll
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                friends.forEach { (name, color) ->
                    val isSent = sentMap[name] == true
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clickable {
                                sentMap[name] = !isSent
                                Toast.makeText(
                                    context,
                                    if (!isSent) "Sent to $name!" else "Unsent",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            .testTag("share_friend_$name")
                    ) {
                        Box(
                            modifier = Modifier
                                .size(54.dp)
                                .clip(CircleShape)
                                .background(if (isSent) TikTokRed else color),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (isSent) "✓" else name.take(1),
                                color = Color.White,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (isSent) "Sent" else name,
                            color = if (isSent) TikTokRed else TikTokWhite,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))
            HorizontalDivider(color = Color(0xFF242424), thickness = 0.5.dp)
            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Share to",
                color = TikTokTextSecondary,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Action buttons horizontal scroll
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                ShareActionItem(
                    icon = Icons.Default.Repeat,
                    label = "Repost",
                    bgColor = TikTokYellow,
                    onClick = {
                        Toast.makeText(context, "Reposted to your profile!", Toast.LENGTH_SHORT).show()
                        onDismiss()
                    }
                )
                ShareActionItem(
                    icon = Icons.Default.ContentCopy,
                    label = "Copy Link",
                    bgColor = Color(0xFF2C5364),
                    onClick = {
                        Toast.makeText(context, "Link copied to clipboard!", Toast.LENGTH_SHORT).show()
                        onDismiss()
                    }
                )
                ShareActionItem(
                    icon = Icons.Default.Download,
                    label = "Save Video",
                    bgColor = Color(0xFF1B5E20),
                    onClick = {
                        Toast.makeText(context, "Video saved to gallery!", Toast.LENGTH_SHORT).show()
                        onDismiss()
                    }
                )
                ShareActionItem(
                    icon = Icons.Default.Splitscreen,
                    label = "Duet",
                    bgColor = TikTokCyan,
                    iconTint = Color.Black,
                    onClick = {
                        Toast.makeText(context, "Opening Duet Studio...", Toast.LENGTH_SHORT).show()
                        onDismiss()
                    }
                )
                ShareActionItem(
                    icon = Icons.Default.AutoAwesome,
                    label = "Use Effect",
                    bgColor = Color(0xFF6A1B9A),
                    onClick = {
                        Toast.makeText(context, "Applying effect filter...", Toast.LENGTH_SHORT).show()
                        onDismiss()
                    }
                )
                ShareActionItem(
                    icon = Icons.Default.HeartBroken,
                    label = "Not Interested",
                    bgColor = Color(0xFF333333),
                    onClick = {
                        Toast.makeText(context, "We will show fewer videos like this", Toast.LENGTH_SHORT).show()
                        onDismiss()
                    }
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Close / Cancel Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(TikTokCard)
                    .clickable { onDismiss() }
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Cancel",
                    color = TikTokWhite,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun ShareActionItem(
    icon: ImageVector,
    label: String,
    bgColor: Color,
    iconTint: Color = Color.White,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { onClick() }
            .width(62.dp)
    ) {
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .background(bgColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = iconTint,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = label,
            color = TikTokWhite,
            fontSize = 11.sp,
            textAlign = TextAlign.Center,
            maxLines = 1
        )
    }
}
