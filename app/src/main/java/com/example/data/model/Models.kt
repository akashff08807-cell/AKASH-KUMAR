package com.example.data.model

data class VideoItem(
    val id: String,
    val authorUsername: String,
    val authorName: String,
    val authorAvatarUrl: String,
    val authorBio: String = "",
    val caption: String,
    val hashtags: List<String>,
    val soundTitle: String,
    val soundArtist: String,
    val soundAlbumCover: String = "",
    val likeCount: Long,
    val commentCount: Long,
    val bookmarkCount: Long,
    val shareCount: Long,
    val isLiked: Boolean = false,
    val isBookmarked: Boolean = false,
    val isFollowing: Boolean = false,
    val category: String = "For You",
    val videoGradientColors: List<Long> = listOf(0xFF0F2027, 0xFF203A43, 0xFF2C5364),
    val videoType: String = "dance", // dance, comedy, tech, food, travel, creative
    val durationSeconds: Int = 15,
    val timestamp: Long = System.currentTimeMillis()
)

data class CommentItem(
    val id: String,
    val videoId: String,
    val authorUsername: String,
    val authorName: String,
    val authorAvatarUrl: String,
    val text: String,
    val timestampFormatted: String,
    val likeCount: Long = 0,
    val isLiked: Boolean = false,
    val isCreator: Boolean = false
)

data class UserProfile(
    val username: String,
    val displayName: String,
    val avatarUrl: String,
    val bio: String,
    val followingCount: Int,
    val followersCount: String,
    val likesCount: String,
    val isVerified: Boolean = false,
    val isFollowing: Boolean = false
)

data class SoundTrack(
    val id: String,
    val title: String,
    val artist: String,
    val coverUrl: String,
    val duration: String,
    val videoUsageCount: String
)
