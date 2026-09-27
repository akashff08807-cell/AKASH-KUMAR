package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "videos")
data class VideoEntity(
    @PrimaryKey val id: String,
    val authorUsername: String,
    val authorName: String,
    val authorAvatarUrl: String,
    val authorBio: String,
    val caption: String,
    val hashtagsJoined: String, // comma-separated
    val soundTitle: String,
    val soundArtist: String,
    val soundAlbumCover: String,
    val likeCount: Long,
    val commentCount: Long,
    val bookmarkCount: Long,
    val shareCount: Long,
    val isLiked: Boolean,
    val isBookmarked: Boolean,
    val isFollowing: Boolean,
    val category: String,
    val videoColorsJoined: String, // hex colors comma-separated
    val videoType: String,
    val durationSeconds: Int,
    val timestamp: Long
)

@Entity(tableName = "comments")
data class CommentEntity(
    @PrimaryKey val id: String,
    val videoId: String,
    val authorUsername: String,
    val authorName: String,
    val authorAvatarUrl: String,
    val text: String,
    val timestampFormatted: String,
    val likeCount: Long,
    val isLiked: Boolean,
    val isCreator: Boolean
)
