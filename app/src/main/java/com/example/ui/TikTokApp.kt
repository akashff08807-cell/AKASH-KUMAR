package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.CommentsBottomSheet
import com.example.ui.components.ShareBottomSheet
import com.example.ui.components.TikTokBottomBar
import com.example.ui.create.CreateVideoScreen
import com.example.ui.discover.DiscoverScreen
import com.example.ui.feed.FeedScreen
import com.example.ui.inbox.InboxScreen
import com.example.ui.live.LiveStreamScreen
import com.example.ui.profile.ProfileScreen
import com.example.ui.sound.SoundDetailScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TikTokApp(
    viewModel: TikTokViewModel,
    modifier: Modifier = Modifier
) {
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val feedTab by viewModel.feedTab.collectAsStateWithLifecycle()
    val isPlaying by viewModel.isPlaying.collectAsStateWithLifecycle()
    val currentVideoIndex by viewModel.currentVideoIndex.collectAsStateWithLifecycle()
    val hearts by viewModel.hearts.collectAsStateWithLifecycle()

    val allVideos by viewModel.allVideos.collectAsStateWithLifecycle()
    val likedVideos by viewModel.likedVideos.collectAsStateWithLifecycle()
    val bookmarkedVideos by viewModel.bookmarkedVideos.collectAsStateWithLifecycle()

    val activeCommentsVideoId by viewModel.activeCommentsVideoId.collectAsStateWithLifecycle()
    val activeComments by viewModel.activeComments.collectAsStateWithLifecycle()
    val activeShareVideo by viewModel.activeShareVideo.collectAsStateWithLifecycle()

    val selectedCreatorUsername by viewModel.selectedCreatorUsername.collectAsStateWithLifecycle()
    val selectedSound by viewModel.selectedSound.collectAsStateWithLifecycle()
    val isLiveStreamOpen by viewModel.isLiveStreamOpen.collectAsStateWithLifecycle()
    val isSearchOpen by viewModel.isSearchOpen.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()

    val liveMessages by viewModel.liveMessages.collectAsStateWithLifecycle()
    val liveLikesCount by viewModel.liveLikesCount.collectAsStateWithLifecycle()

    val isFullScreenOverlay = isLiveStreamOpen || isSearchOpen || currentTab == MainTab.CREATE || selectedSound != null || selectedCreatorUsername != null

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black),
        containerColor = Color.Black,
        bottomBar = {
            if (!isFullScreenOverlay) {
                TikTokBottomBar(
                    currentTab = currentTab,
                    onTabSelected = { tab ->
                        viewModel.setTab(tab)
                    }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = if (!isFullScreenOverlay) innerPadding.calculateBottomPadding() else 0.dp)
        ) {
            when {
                // 1. Live Stream Screen
                isLiveStreamOpen -> {
                    LiveStreamScreen(
                        messages = liveMessages,
                        likesCount = liveLikesCount,
                        onClose = { viewModel.closeLiveStream() },
                        onSendMessage = { viewModel.sendLiveMessage(it) },
                        onSendGift = { viewModel.sendLiveGift(it) },
                        onSendHeart = { viewModel.sendLiveHeart() }
                    )
                }

                // 2. Discover / Search Screen
                isSearchOpen -> {
                    DiscoverScreen(
                        searchQuery = searchQuery,
                        videos = allVideos,
                        trendingSounds = viewModel.getTrendingSounds(),
                        onSearchChange = { viewModel.setSearchQuery(it) },
                        onBack = { viewModel.closeSearch() },
                        onVideoClick = { video ->
                            viewModel.closeSearch()
                            viewModel.setTab(MainTab.HOME)
                            val targetIndex = allVideos.indexOfFirst { it.id == video.id }
                            if (targetIndex != -1) {
                                viewModel.setCurrentVideoIndex(targetIndex)
                            }
                        },
                        onSoundClick = { sound ->
                            viewModel.closeSearch()
                            viewModel.openSoundDetail(sound)
                        }
                    )
                }

                // 3. Creator Profile Screen (when viewing another user)
                selectedCreatorUsername != null -> {
                    val creator = viewModel.getCreatorProfile(selectedCreatorUsername!!)
                    val creatorVideos = allVideos.filter { it.authorUsername == creator.username }
                    ProfileScreen(
                        profile = creator,
                        userVideos = creatorVideos,
                        likedVideos = emptyList(),
                        bookmarkedVideos = emptyList(),
                        isCurrentUser = false,
                        onBack = { viewModel.closeCreatorProfile() },
                        onFollowClick = {
                            viewModel.toggleFollow(creator.username, creator.isFollowing)
                        },
                        onVideoClick = { video ->
                            viewModel.closeCreatorProfile()
                            viewModel.setTab(MainTab.HOME)
                            val targetIndex = allVideos.indexOfFirst { it.id == video.id }
                            if (targetIndex != -1) {
                                viewModel.setCurrentVideoIndex(targetIndex)
                            }
                        }
                    )
                }

                // 4. Sound Detail Screen
                selectedSound != null -> {
                    val sound = selectedSound!!
                    val soundVideos = allVideos.filter { it.soundTitle == sound.title }
                    SoundDetailScreen(
                        sound = sound,
                        videos = if (soundVideos.isNotEmpty()) soundVideos else allVideos.take(4),
                        onBack = { viewModel.closeSoundDetail() },
                        onUseSound = {
                            viewModel.closeSoundDetail()
                            viewModel.setTab(MainTab.CREATE)
                        },
                        onVideoClick = { video ->
                            viewModel.closeSoundDetail()
                            viewModel.setTab(MainTab.HOME)
                            val targetIndex = allVideos.indexOfFirst { it.id == video.id }
                            if (targetIndex != -1) {
                                viewModel.setCurrentVideoIndex(targetIndex)
                            }
                        }
                    )
                }

                // 5. Main Tabs
                else -> {
                    when (currentTab) {
                        MainTab.HOME -> {
                            FeedScreen(
                                videos = allVideos,
                                feedTab = feedTab,
                                isPlaying = isPlaying,
                                currentVideoIndex = currentVideoIndex,
                                hearts = hearts,
                                onFeedTabChange = { viewModel.setFeedTab(it) },
                                onVideoChanged = { viewModel.setCurrentVideoIndex(it) },
                                onTogglePlayPause = { viewModel.togglePlayPause() },
                                onDoubleTapVideo = { video, x, y ->
                                    viewModel.onDoubleTapVideo(video, x, y)
                                },
                                onHeartAnimationEnd = { heartId ->
                                    viewModel.removeHeart(heartId)
                                },
                                onLikeClick = { viewModel.toggleLike(it) },
                                onCommentClick = { viewModel.openComments(it.id) },
                                onBookmarkClick = { viewModel.toggleBookmark(it) },
                                onShareClick = { viewModel.openShare(it) },
                                onAuthorClick = { viewModel.openCreatorProfile(it) },
                                onFollowClick = { username, following ->
                                    viewModel.toggleFollow(username, following)
                                },
                                onSoundClick = { title, artist ->
                                    viewModel.openSoundByTitle(title, artist)
                                },
                                onLiveClick = { viewModel.openLiveStream() },
                                onSearchClick = { viewModel.openSearch() }
                            )
                        }

                        MainTab.FRIENDS -> {
                            // Friends Feed (Following tab)
                            FeedScreen(
                                videos = allVideos,
                                feedTab = FeedTab.FOLLOWING,
                                isPlaying = isPlaying,
                                currentVideoIndex = 0,
                                hearts = hearts,
                                onFeedTabChange = { viewModel.setFeedTab(it) },
                                onVideoChanged = { viewModel.setCurrentVideoIndex(it) },
                                onTogglePlayPause = { viewModel.togglePlayPause() },
                                onDoubleTapVideo = { video, x, y ->
                                    viewModel.onDoubleTapVideo(video, x, y)
                                },
                                onHeartAnimationEnd = { heartId ->
                                    viewModel.removeHeart(heartId)
                                },
                                onLikeClick = { viewModel.toggleLike(it) },
                                onCommentClick = { viewModel.openComments(it.id) },
                                onBookmarkClick = { viewModel.toggleBookmark(it) },
                                onShareClick = { viewModel.openShare(it) },
                                onAuthorClick = { viewModel.openCreatorProfile(it) },
                                onFollowClick = { username, following ->
                                    viewModel.toggleFollow(username, following)
                                },
                                onSoundClick = { title, artist ->
                                    viewModel.openSoundByTitle(title, artist)
                                },
                                onLiveClick = { viewModel.openLiveStream() },
                                onSearchClick = { viewModel.openSearch() }
                            )
                        }

                        MainTab.CREATE -> {
                            CreateVideoScreen(
                                trendingSounds = viewModel.getTrendingSounds(),
                                onClose = { viewModel.setTab(MainTab.HOME) },
                                onPostVideo = { caption, tags, soundTitle, soundArtist, videoType, colors ->
                                    viewModel.createVideo(
                                        caption,
                                        tags,
                                        soundTitle,
                                        soundArtist,
                                        videoType,
                                        colors
                                    )
                                }
                            )
                        }

                        MainTab.INBOX -> {
                            InboxScreen(
                                onCreatorClick = { viewModel.openCreatorProfile(it) }
                            )
                        }

                        MainTab.PROFILE -> {
                            val myProfile = viewModel.getCreatorProfile("current_user")
                            val myVideos = allVideos.filter { it.authorUsername == "current_user" }
                            ProfileScreen(
                                profile = myProfile,
                                userVideos = myVideos,
                                likedVideos = likedVideos,
                                bookmarkedVideos = bookmarkedVideos,
                                isCurrentUser = true,
                                onVideoClick = { video ->
                                    viewModel.setTab(MainTab.HOME)
                                    val targetIndex = allVideos.indexOfFirst { it.id == video.id }
                                    if (targetIndex != -1) {
                                        viewModel.setCurrentVideoIndex(targetIndex)
                                    }
                                }
                            )
                        }
                    }
                }
            }

            // Bottom Sheets
            if (activeCommentsVideoId != null) {
                CommentsBottomSheet(
                    comments = activeComments,
                    onDismiss = { viewModel.closeComments() },
                    onSendComment = { text ->
                        activeCommentsVideoId?.let { videoId ->
                            viewModel.addComment(videoId, text)
                        }
                    },
                    onLikeComment = { comment ->
                        viewModel.toggleCommentLike(comment)
                    }
                )
            }

            if (activeShareVideo != null) {
                ShareBottomSheet(
                    video = activeShareVideo!!,
                    onDismiss = { viewModel.closeShare() }
                )
            }
        }
    }
}
