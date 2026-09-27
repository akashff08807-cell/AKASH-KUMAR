package com.example.ui.feed

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.VideoItem
import com.example.ui.DoubleTapHeart
import com.example.ui.FeedTab
import com.example.ui.components.FloatingHeartItem
import com.example.ui.components.SimulatedVideoCanvas
import com.example.ui.components.TikTokSideActions
import com.example.ui.components.TikTokTopBar
import com.example.ui.components.VideoInfoOverlay
import com.example.ui.theme.TikTokTextSecondary

@Composable
fun FeedScreen(
    videos: List<VideoItem>,
    feedTab: FeedTab,
    isPlaying: Boolean,
    currentVideoIndex: Int,
    hearts: List<DoubleTapHeart>,
    onFeedTabChange: (FeedTab) -> Unit,
    onVideoChanged: (Int) -> Unit,
    onTogglePlayPause: () -> Unit,
    onDoubleTapVideo: (VideoItem, Float, Float) -> Unit,
    onHeartAnimationEnd: (Long) -> Unit,
    onLikeClick: (VideoItem) -> Unit,
    onCommentClick: (VideoItem) -> Unit,
    onBookmarkClick: (VideoItem) -> Unit,
    onShareClick: (VideoItem) -> Unit,
    onAuthorClick: (String) -> Unit,
    onFollowClick: (String, Boolean) -> Unit,
    onSoundClick: (String, String) -> Unit,
    onLiveClick: () -> Unit,
    onSearchClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val displayVideos = if (feedTab == FeedTab.FOLLOWING) {
        videos.filter { it.isFollowing }
    } else {
        videos
    }

    if (displayVideos.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            TikTokTopBar(
                currentTab = feedTab,
                onTabSelected = onFeedTabChange,
                onLiveClick = onLiveClick,
                onSearchClick = onSearchClick,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .statusBarsPadding()
            )
            Text(
                text = if (feedTab == FeedTab.FOLLOWING) "Follow creators to see their latest videos here!" else "No videos available",
                color = TikTokTextSecondary,
                fontSize = 15.sp,
                modifier = Modifier.padding(horizontal = 32.dp)
            )
        }
        return
    }

    val pagerState = rememberPagerState(
        initialPage = currentVideoIndex.coerceIn(0, (displayVideos.size - 1).coerceAtLeast(0)),
        pageCount = { displayVideos.size }
    )

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { page ->
            onVideoChanged(page)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .testTag("feed_screen")
    ) {
        VerticalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
            key = { index -> displayVideos[index].id }
        ) { page ->
            val video = displayVideos[page]
            val isCurrentVideoPlaying = isPlaying && pagerState.currentPage == page

            Box(modifier = Modifier.fillMaxSize()) {
                // Interactive Simulated Video Canvas
                SimulatedVideoCanvas(
                    video = video,
                    isPlaying = isCurrentVideoPlaying,
                    onSingleTap = onTogglePlayPause,
                    onDoubleTap = { x, y ->
                        onDoubleTapVideo(video, x, y)
                    }
                )

                // Video Info Overlay (Bottom Left)
                VideoInfoOverlay(
                    video = video,
                    onAuthorClick = { onAuthorClick(video.authorUsername) },
                    onHashtagClick = { /* search hashtag */ onSearchClick() },
                    onSoundClick = { onSoundClick(video.soundTitle, video.soundArtist) },
                    modifier = Modifier.align(Alignment.BottomStart)
                )

                // TikTok Side Action Bar (Bottom Right)
                TikTokSideActions(
                    video = video,
                    isPlaying = isCurrentVideoPlaying,
                    onLikeClick = { onLikeClick(video) },
                    onCommentClick = { onCommentClick(video) },
                    onBookmarkClick = { onBookmarkClick(video) },
                    onShareClick = { onShareClick(video) },
                    onAvatarClick = { onAuthorClick(video.authorUsername) },
                    onFollowClick = { onFollowClick(video.authorUsername, video.isFollowing) },
                    onSoundClick = { onSoundClick(video.soundTitle, video.soundArtist) },
                    modifier = Modifier.align(Alignment.BottomEnd)
                )
            }
        }

        // Floating Double-Tap Hearts
        hearts.forEach { heart ->
            FloatingHeartItem(
                heart = heart,
                onAnimationEnd = onHeartAnimationEnd
            )
        }

        // Top Navigation Bar (Following | For You, Live, Search)
        TikTokTopBar(
            currentTab = feedTab,
            onTabSelected = onFeedTabChange,
            onLiveClick = onLiveClick,
            onSearchClick = onSearchClick,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
        )
    }
}
