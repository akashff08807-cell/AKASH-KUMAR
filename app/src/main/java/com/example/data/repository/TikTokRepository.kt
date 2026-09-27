package com.example.data.repository

import com.example.data.local.CommentDao
import com.example.data.local.CommentEntity
import com.example.data.local.VideoDao
import com.example.data.local.VideoEntity
import com.example.data.model.CommentItem
import com.example.data.model.SoundTrack
import com.example.data.model.UserProfile
import com.example.data.model.VideoItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID

class TikTokRepository(
    private val videoDao: VideoDao,
    private val commentDao: CommentDao
) {

    val allVideos: Flow<List<VideoItem>> = videoDao.getAllVideos().map { list ->
        list.map { it.toDomainModel() }
    }

    val likedVideos: Flow<List<VideoItem>> = videoDao.getLikedVideos().map { list ->
        list.map { it.toDomainModel() }
    }

    val bookmarkedVideos: Flow<List<VideoItem>> = videoDao.getBookmarkedVideos().map { list ->
        list.map { it.toDomainModel() }
    }

    fun getCommentsForVideo(videoId: String): Flow<List<CommentItem>> {
        return commentDao.getCommentsForVideo(videoId).map { list ->
            list.map { it.toDomainModel() }
        }
    }

    fun getVideosBySound(soundTitle: String): Flow<List<VideoItem>> {
        return videoDao.getVideosBySound(soundTitle).map { list ->
            list.map { it.toDomainModel() }
        }
    }

    fun getVideosByAuthor(username: String): Flow<List<VideoItem>> {
        return videoDao.getVideosByAuthor(username).map { list ->
            list.map { it.toDomainModel() }
        }
    }

    suspend fun toggleLike(video: VideoItem) {
        val newLiked = !video.isLiked
        val newCount = if (newLiked) video.likeCount + 1 else (video.likeCount - 1).coerceAtLeast(0)
        videoDao.updateLikeStatus(video.id, newLiked, newCount)
    }

    suspend fun toggleBookmark(video: VideoItem) {
        val newBookmarked = !video.isBookmarked
        val newCount = if (newBookmarked) video.bookmarkCount + 1 else (video.bookmarkCount - 1).coerceAtLeast(0)
        videoDao.updateBookmarkStatus(video.id, newBookmarked, newCount)
    }

    suspend fun toggleFollow(username: String, currentFollowing: Boolean) {
        videoDao.updateFollowStatus(username, !currentFollowing)
    }

    suspend fun addComment(videoId: String, text: String) {
        val newComment = CommentEntity(
            id = UUID.randomUUID().toString(),
            videoId = videoId,
            authorUsername = "current_user",
            authorName = "You",
            authorAvatarUrl = "",
            text = text,
            timestampFormatted = "Just now",
            likeCount = 0,
            isLiked = false,
            isCreator = false
        )
        commentDao.insertComment(newComment)
        videoDao.incrementCommentCount(videoId)
    }

    suspend fun toggleCommentLike(comment: CommentItem) {
        val newLiked = !comment.isLiked
        val newCount = if (newLiked) comment.likeCount + 1 else (comment.likeCount - 1).coerceAtLeast(0)
        commentDao.updateCommentLike(comment.id, newLiked, newCount)
    }

    suspend fun createNewVideo(
        caption: String,
        hashtags: List<String>,
        soundTitle: String,
        soundArtist: String,
        videoType: String,
        gradientColors: List<Long>
    ) {
        val newVideo = VideoEntity(
            id = UUID.randomUUID().toString(),
            authorUsername = "current_user",
            authorName = "You",
            authorAvatarUrl = "",
            authorBio = "Creating awesome short clips on TikTok ✨",
            caption = caption,
            hashtagsJoined = hashtags.joinToString(","),
            soundTitle = soundTitle,
            soundArtist = soundArtist,
            soundAlbumCover = "",
            likeCount = 1,
            commentCount = 0,
            bookmarkCount = 0,
            shareCount = 0,
            isLiked = true,
            isBookmarked = false,
            isFollowing = true,
            category = "For You",
            videoColorsJoined = gradientColors.joinToString(",") { it.toString() },
            videoType = videoType,
            durationSeconds = 15,
            timestamp = System.currentTimeMillis()
        )
        videoDao.insertVideo(newVideo)
    }

    suspend fun seedDatabaseIfEmpty() {
        if (videoDao.getVideoCount() == 0) {
            val seedVideos = getSeedVideos()
            videoDao.insertVideos(seedVideos)

            val seedComments = getSeedComments()
            commentDao.insertComments(seedComments)
        }
    }

    // Static creator profile lookup
    fun getCreatorProfile(username: String): UserProfile {
        return when (username) {
            "charlidance" -> UserProfile(
                username = "charlidance",
                displayName = "Charli Dance 💃",
                avatarUrl = "",
                bio = "Dancer & Choreographer 🌟 Live every Friday! Inquiries: charli@dance.co",
                followingCount = 184,
                followersCount = "12.4M",
                likesCount = "188.2M",
                isVerified = true,
                isFollowing = false
            )
            "techlead_pro" -> UserProfile(
                username = "techlead_pro",
                displayName = "TechLead Pro 💻",
                avatarUrl = "",
                bio = "Android Engineer & Tech Creator | Building Kotlin apps with Compose | Subscribe on YT 🔔",
                followingCount = 210,
                followersCount = "840K",
                likesCount = "15.3M",
                isVerified = true,
                isFollowing = true
            )
            "gourmet_kitchen" -> UserProfile(
                username = "gourmet_kitchen",
                displayName = "Chef Mario 👨‍🍳",
                avatarUrl = "",
                bio = "Simplifying culinary magic at home ✨ 15-min recipes you'll actually make. Book link below ⬇️",
                followingCount = 95,
                followersCount = "3.1M",
                likesCount = "45.7M",
                isVerified = true,
                isFollowing = false
            )
            "travelwithkai" -> UserProfile(
                username = "travelwithkai",
                displayName = "Kai Travels ✈️",
                avatarUrl = "",
                bio = "Nomad exploring 50+ countries 🗺️ Drone shots & hidden gems. Let's wander!",
                followingCount = 340,
                followersCount = "1.8M",
                likesCount = "29.4M",
                isVerified = true,
                isFollowing = false
            )
            "current_user" -> UserProfile(
                username = "current_user",
                displayName = "Alex Morgan",
                avatarUrl = "",
                bio = "Living for good vibes, music, and short videos 🎬 Tap follow to join the journey!",
                followingCount = 142,
                followersCount = "1.2K",
                likesCount = "28.6K",
                isVerified = false,
                isFollowing = false
            )
            else -> UserProfile(
                username = username,
                displayName = username.replaceFirstChar { it.uppercase() },
                avatarUrl = "",
                bio = "TikTok creator sharing daily moments ✨",
                followingCount = 50,
                followersCount = "120K",
                likesCount = "1.4M",
                isVerified = false,
                isFollowing = false
            )
        }
    }

    val trendingSounds: List<SoundTrack> = listOf(
        SoundTrack("s1", "Electro Bounce Beat", "DJ Neon", "", "0:30", "1.4M videos"),
        SoundTrack("s2", "Lo-Fi Beats - Code Chill", "Lofi Girl", "", "0:45", "890K videos"),
        SoundTrack("s3", "Sizzle & Crunch ASMR", "Chef Mario", "", "0:15", "420K videos"),
        SoundTrack("s4", "Golden Hour Symphony", "Aurora Strings", "", "0:30", "2.1M videos"),
        SoundTrack("s5", "Tokyo Drift Phonk", "Bass Boosters", "", "0:25", "3.6M videos"),
        SoundTrack("s6", "Synthwave Horizon", "RetroWave 80s", "", "0:35", "740K videos")
    )

    private fun VideoEntity.toDomainModel(): VideoItem {
        val hashtagsList = if (hashtagsJoined.isBlank()) emptyList() else hashtagsJoined.split(",").map { it.trim() }
        val colorsList = if (videoColorsJoined.isBlank()) {
            listOf(0xFF0F2027, 0xFF203A43, 0xFF2C5364)
        } else {
            videoColorsJoined.split(",").mapNotNull { it.trim().toLongOrNull() }
        }

        return VideoItem(
            id = id,
            authorUsername = authorUsername,
            authorName = authorName,
            authorAvatarUrl = authorAvatarUrl,
            authorBio = authorBio,
            caption = caption,
            hashtags = hashtagsList,
            soundTitle = soundTitle,
            soundArtist = soundArtist,
            soundAlbumCover = soundAlbumCover,
            likeCount = likeCount,
            commentCount = commentCount,
            bookmarkCount = bookmarkCount,
            shareCount = shareCount,
            isLiked = isLiked,
            isBookmarked = isBookmarked,
            isFollowing = isFollowing,
            category = category,
            videoGradientColors = if (colorsList.isNotEmpty()) colorsList else listOf(0xFF0F2027, 0xFF203A43, 0xFF2C5364),
            videoType = videoType,
            durationSeconds = durationSeconds,
            timestamp = timestamp
        )
    }

    private fun CommentEntity.toDomainModel(): CommentItem {
        return CommentItem(
            id = id,
            videoId = videoId,
            authorUsername = authorUsername,
            authorName = authorName,
            authorAvatarUrl = authorAvatarUrl,
            text = text,
            timestampFormatted = timestampFormatted,
            likeCount = likeCount,
            isLiked = isLiked,
            isCreator = isCreator
        )
    }

    private fun getSeedVideos(): List<VideoEntity> {
        val now = System.currentTimeMillis()
        return listOf(
            VideoEntity(
                id = "vid_1",
                authorUsername = "charlidance",
                authorName = "Charli Dance",
                authorAvatarUrl = "",
                authorBio = "Dancer & Choreographer 🌟 Live every Friday!",
                caption = "Can't stop dancing to this beat! Who else tried this trend? dc: @alex 💃✨ Drop a ❤️ if you know this move!",
                hashtagsJoined = "dance,trend,fyp,vibes,dancer",
                soundTitle = "Electro Bounce Beat",
                soundArtist = "DJ Neon",
                soundAlbumCover = "",
                likeCount = 284200,
                commentCount = 3840,
                bookmarkCount = 42100,
                shareCount = 18900,
                isLiked = false,
                isBookmarked = false,
                isFollowing = false,
                category = "For You",
                videoColorsJoined = "${0xFF3A1C71},${0xFFD76D77},${0xFFFFAF7B}",
                videoType = "dance",
                durationSeconds = 15,
                timestamp = now - 1000 * 60 * 30
            ),
            VideoEntity(
                id = "vid_2",
                authorUsername = "techlead_pro",
                authorName = "TechLead Pro",
                authorAvatarUrl = "",
                authorBio = "Android Engineer & Tech Creator",
                caption = "Top 3 Jetpack Compose animation tricks you didn't know existed ⚡📱 Building buttery smooth 60fps Android UI in minutes!",
                hashtagsJoined = "coding,android,developer,techtok,kotlin",
                soundTitle = "Lo-Fi Beats - Code Chill",
                soundArtist = "Lofi Girl",
                soundAlbumCover = "",
                likeCount = 145600,
                commentCount = 1920,
                bookmarkCount = 68400,
                shareCount = 9800,
                isLiked = true,
                isBookmarked = true,
                isFollowing = true,
                category = "For You",
                videoColorsJoined = "${0xFF0F2027},${0xFF203A43},${0xFF2C5364}",
                videoType = "tech",
                durationSeconds = 30,
                timestamp = now - 1000 * 60 * 90
            ),
            VideoEntity(
                id = "vid_3",
                authorUsername = "gourmet_kitchen",
                authorName = "Chef Mario",
                authorAvatarUrl = "",
                authorBio = "Simplifying culinary magic at home ✨",
                caption = "Ultra-crispy Golden Garlic Butter Smash Potatoes! 🥔🧄 Hear that satisfying crunch at 0:08?! Save this for dinner tonight!",
                hashtagsJoined = "foodie,cooking,recipe,asmr,potatoes",
                soundTitle = "Sizzle & Crunch ASMR",
                soundArtist = "Chef Mario",
                soundAlbumCover = "",
                likeCount = 412300,
                commentCount = 5210,
                bookmarkCount = 95200,
                shareCount = 34500,
                isLiked = false,
                isBookmarked = false,
                isFollowing = false,
                category = "For You",
                videoColorsJoined = "${0xFF5C258D},${0xFF4389A2},${0xFF1B2A47}",
                videoType = "food",
                durationSeconds = 25,
                timestamp = now - 1000 * 60 * 240
            ),
            VideoEntity(
                id = "vid_4",
                authorUsername = "travelwithkai",
                authorName = "Kai Travels",
                authorAvatarUrl = "",
                authorBio = "Nomad exploring 50+ countries 🗺️",
                caption = "Waking up above the cloud sea in Zermatt, Switzerland 🇨🇭🏔️ Still feels like a fairytale dream. Who would you go with?",
                hashtagsJoined = "travel,wanderlust,switzerland,nature,mountains",
                soundTitle = "Golden Hour Symphony",
                soundArtist = "Aurora Strings",
                soundAlbumCover = "",
                likeCount = 590400,
                commentCount = 6430,
                bookmarkCount = 120500,
                shareCount = 41200,
                isLiked = false,
                isBookmarked = false,
                isFollowing = false,
                category = "For You",
                videoColorsJoined = "${0xFF000428},${0xFF004E92},${0xFF0083B0}",
                videoType = "travel",
                durationSeconds = 18,
                timestamp = now - 1000 * 60 * 480
            ),
            VideoEntity(
                id = "vid_5",
                authorUsername = "laughwithleo",
                authorName = "Leo Comedy",
                authorAvatarUrl = "",
                authorBio = "Daily laughs to get through the week 😂",
                caption = "Explaining to my grandma that I work remotely from my laptop 😭💀 'So you just stare at letters all day?' Yes grandma, exactly.",
                hashtagsJoined = "comedy,relatable,humor,funny,grandma",
                soundTitle = "Funny Quirky Melody",
                soundArtist = "Comedy Club",
                soundAlbumCover = "",
                likeCount = 780200,
                commentCount = 9430,
                bookmarkCount = 48200,
                shareCount = 62100,
                isLiked = false,
                isBookmarked = false,
                isFollowing = false,
                category = "For You",
                videoColorsJoined = "${0xFF4B1248},${0xFFF0C27B},${0xFF8E2800}",
                videoType = "comedy",
                durationSeconds = 20,
                timestamp = now - 1000 * 60 * 720
            ),
            VideoEntity(
                id = "vid_6",
                authorUsername = "streetstyle_tok",
                authorName = "Tokyo Drip",
                authorAvatarUrl = "",
                authorBio = "Harajuku & Shibuya street fashion 🇯🇵",
                caption = "Tokyo street style check! 🖤 Cyberpunk cyberpunk aesthetics in Shibuya crossing at 2 AM. Rate this fit 1-10!",
                hashtagsJoined = "fashion,streetwear,tokyo,fitcheck,style",
                soundTitle = "Tokyo Drift Phonk",
                soundArtist = "Bass Boosters",
                soundAlbumCover = "",
                likeCount = 330100,
                commentCount = 2840,
                bookmarkCount = 55400,
                shareCount = 15200,
                isLiked = false,
                isBookmarked = false,
                isFollowing = false,
                category = "For You",
                videoColorsJoined = "${0xFF141E30},${0xFF243B55},${0xFF00F2FE}",
                videoType = "fashion",
                durationSeconds = 16,
                timestamp = now - 1000 * 60 * 960
            ),
            VideoEntity(
                id = "vid_7",
                authorUsername = "ai_creators",
                authorName = "AI Visionary",
                authorAvatarUrl = "",
                authorBio = "Pushing creative boundaries with AI & Code",
                caption = "Built this futuristic glowing neon particle simulator on Android! ✨ Swipe up for more creative tech art!",
                hashtagsJoined = "ai,creativetech,future,neon,digitalart",
                soundTitle = "Synthwave Horizon",
                soundArtist = "RetroWave 80s",
                soundAlbumCover = "",
                likeCount = 210800,
                commentCount = 3120,
                bookmarkCount = 49000,
                shareCount = 14300,
                isLiked = false,
                isBookmarked = false,
                isFollowing = false,
                category = "For You",
                videoColorsJoined = "${0xFF2B0938},${0xFF660066},${0xFF00C6FF}",
                videoType = "creative",
                durationSeconds = 22,
                timestamp = now - 1000 * 60 * 1200
            )
        )
    }

    private fun getSeedComments(): List<CommentEntity> {
        return listOf(
            CommentEntity(
                id = "c_1",
                videoId = "vid_1",
                authorUsername = "dancefan99",
                authorName = "Sarah J",
                authorAvatarUrl = "",
                text = "The transition at 0:05 was INSANE! How many takes did this take?! 🔥",
                timestampFormatted = "2h ago",
                likeCount = 4280,
                isLiked = true,
                isCreator = false
            ),
            CommentEntity(
                id = "c_2",
                videoId = "vid_1",
                authorUsername = "charlidance",
                authorName = "Charli Dance",
                authorAvatarUrl = "",
                text = "Haha about 24 takes! The energy was so worth it though! Love you guys ❤️",
                timestampFormatted = "1h ago",
                likeCount = 12400,
                isLiked = true,
                isCreator = true
            ),
            CommentEntity(
                id = "c_3",
                videoId = "vid_1",
                authorUsername = "beat_master",
                authorName = "Alex",
                authorAvatarUrl = "",
                text = "Nailed the choreography!! Thanks for tagging me! 🙌",
                timestampFormatted = "45m ago",
                likeCount = 1890,
                isLiked = false,
                isCreator = false
            ),
            CommentEntity(
                id = "c_4",
                videoId = "vid_2",
                authorUsername = "code_ninja",
                authorName = "DevDan",
                authorAvatarUrl = "",
                text = "That custom modifier chaining example blew my mind. Implementing it right now!",
                timestampFormatted = "3h ago",
                likeCount = 890,
                isLiked = false,
                isCreator = false
            ),
            CommentEntity(
                id = "c_5",
                videoId = "vid_3",
                authorUsername = "foodlover_amy",
                authorName = "Amy",
                authorAvatarUrl = "",
                text = "Making these tonight for movie night with garlic mayo dip! 😋🤤",
                timestampFormatted = "4h ago",
                likeCount = 1240,
                isLiked = false,
                isCreator = false
            )
        )
    }
}
