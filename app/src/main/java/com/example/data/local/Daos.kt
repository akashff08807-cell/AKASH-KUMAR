package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface VideoDao {
    @Query("SELECT * FROM videos ORDER BY timestamp DESC")
    fun getAllVideos(): Flow<List<VideoEntity>>

    @Query("SELECT * FROM videos WHERE isLiked = 1 ORDER BY timestamp DESC")
    fun getLikedVideos(): Flow<List<VideoEntity>>

    @Query("SELECT * FROM videos WHERE isBookmarked = 1 ORDER BY timestamp DESC")
    fun getBookmarkedVideos(): Flow<List<VideoEntity>>

    @Query("SELECT * FROM videos WHERE authorUsername = :username ORDER BY timestamp DESC")
    fun getVideosByAuthor(username: String): Flow<List<VideoEntity>>

    @Query("SELECT * FROM videos WHERE soundTitle = :soundTitle ORDER BY timestamp DESC")
    fun getVideosBySound(soundTitle: String): Flow<List<VideoEntity>>

    @Query("SELECT * FROM videos WHERE id = :id LIMIT 1")
    suspend fun getVideoById(id: String): VideoEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVideo(video: VideoEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVideos(videos: List<VideoEntity>)

    @Query("UPDATE videos SET isLiked = :isLiked, likeCount = :likeCount WHERE id = :id")
    suspend fun updateLikeStatus(id: String, isLiked: Boolean, likeCount: Long)

    @Query("UPDATE videos SET isBookmarked = :isBookmarked, bookmarkCount = :bookmarkCount WHERE id = :id")
    suspend fun updateBookmarkStatus(id: String, isBookmarked: Boolean, bookmarkCount: Long)

    @Query("UPDATE videos SET isFollowing = :isFollowing WHERE authorUsername = :username")
    suspend fun updateFollowStatus(username: String, isFollowing: Boolean)

    @Query("UPDATE videos SET commentCount = commentCount + 1 WHERE id = :videoId")
    suspend fun incrementCommentCount(videoId: String)

    @Query("SELECT COUNT(*) FROM videos")
    suspend fun getVideoCount(): Int
}

@Dao
interface CommentDao {
    @Query("SELECT * FROM comments WHERE videoId = :videoId ORDER BY isCreator DESC, likeCount DESC")
    fun getCommentsForVideo(videoId: String): Flow<List<CommentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComment(comment: CommentEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComments(comments: List<CommentEntity>)

    @Query("UPDATE comments SET isLiked = :isLiked, likeCount = :likeCount WHERE id = :commentId")
    suspend fun updateCommentLike(commentId: String, isLiked: Boolean, likeCount: Long)
}
