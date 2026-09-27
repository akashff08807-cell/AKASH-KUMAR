package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.model.CommentItem
import com.example.data.model.SoundTrack
import com.example.data.model.UserProfile
import com.example.data.model.VideoItem
import com.example.data.repository.TikTokRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class MainTab {
    HOME, FRIENDS, CREATE, INBOX, PROFILE
}

enum class FeedTab {
    FOLLOWING, FOR_YOU
}

data class DoubleTapHeart(
    val id: Long = System.currentTimeMillis(),
    val x: Float,
    val y: Float
)

data class LiveChatMessage(
    val username: String,
    val text: String,
    val badge: String = ""
)

class TikTokViewModel(
    private val repository: TikTokRepository
) : ViewModel() {

    private val _currentTab = MutableStateFlow(MainTab.HOME)
    val currentTab: StateFlow<MainTab> = _currentTab.asStateFlow()

    private val _feedTab = MutableStateFlow(FeedTab.FOR_YOU)
    val feedTab: StateFlow<FeedTab> = _feedTab.asStateFlow()

    private val _isPlaying = MutableStateFlow(true)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _currentVideoIndex = MutableStateFlow(0)
    val currentVideoIndex: StateFlow<Int> = _currentVideoIndex.asStateFlow()

    // Floating heart bursts on double tap
    private val _hearts = MutableStateFlow<List<DoubleTapHeart>>(emptyList())
    val hearts: StateFlow<List<DoubleTapHeart>> = _hearts.asStateFlow()

    // Active bottom sheets & sub-screens
    private val _activeCommentsVideoId = MutableStateFlow<String?>(null)
    val activeCommentsVideoId: StateFlow<String?> = _activeCommentsVideoId.asStateFlow()

    private val _activeShareVideo = MutableStateFlow<VideoItem?>(null)
    val activeShareVideo: StateFlow<VideoItem?> = _activeShareVideo.asStateFlow()

    private val _selectedCreatorUsername = MutableStateFlow<String?>(null)
    val selectedCreatorUsername: StateFlow<String?> = _selectedCreatorUsername.asStateFlow()

    private val _selectedSound = MutableStateFlow<SoundTrack?>(null)
    val selectedSound: StateFlow<SoundTrack?> = _selectedSound.asStateFlow()

    private val _isLiveStreamOpen = MutableStateFlow(false)
    val isLiveStreamOpen: StateFlow<Boolean> = _isLiveStreamOpen.asStateFlow()

    // Search query
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _isSearchOpen = MutableStateFlow(false)
    val isSearchOpen: StateFlow<Boolean> = _isSearchOpen.asStateFlow()

    // Videos
    val allVideos: StateFlow<List<VideoItem>> = repository.allVideos
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val likedVideos: StateFlow<List<VideoItem>> = repository.likedVideos
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val bookmarkedVideos: StateFlow<List<VideoItem>> = repository.bookmarkedVideos
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Comments for active video
    private val _activeComments = MutableStateFlow<List<CommentItem>>(emptyList())
    val activeComments: StateFlow<List<CommentItem>> = _activeComments.asStateFlow()

    // Live Stream state
    private val _liveMessages = MutableStateFlow<List<LiveChatMessage>>(
        listOf(
            LiveChatMessage("sarah_vibes", "OMG love this energy!! 🔥", "👑"),
            LiveChatMessage("alex_traveler", "Greetings from Paris! 🇫🇷", "🌟"),
            LiveChatMessage("leo_beats", "That transition was so clean!", ""),
            LiveChatMessage("maya_dancer", "Sent a Rose 🌹", "💎"),
            LiveChatMessage("kevin_tok", "Hi everyone!! 👋", "")
        )
    )
    val liveMessages: StateFlow<List<LiveChatMessage>> = _liveMessages.asStateFlow()

    private val _liveLikesCount = MutableStateFlow(48200L)
    val liveLikesCount: StateFlow<Long> = _liveLikesCount.asStateFlow()

    init {
        viewModelScope.launch {
            repository.seedDatabaseIfEmpty()
        }
    }

    fun setTab(tab: MainTab) {
        _currentTab.value = tab
    }

    fun setFeedTab(feedTab: FeedTab) {
        _feedTab.value = feedTab
    }

    fun togglePlayPause() {
        _isPlaying.value = !_isPlaying.value
    }

    fun setCurrentVideoIndex(index: Int) {
        _currentVideoIndex.value = index
        _isPlaying.value = true
    }

    fun onDoubleTapVideo(video: VideoItem, x: Float, y: Float) {
        if (!video.isLiked) {
            viewModelScope.launch {
                repository.toggleLike(video)
            }
        }
        val newHeart = DoubleTapHeart(x = x, y = y)
        _hearts.value = _hearts.value + newHeart
    }

    fun removeHeart(heartId: Long) {
        _hearts.value = _hearts.value.filterNot { it.id == heartId }
    }

    fun toggleLike(video: VideoItem) {
        viewModelScope.launch {
            repository.toggleLike(video)
        }
    }

    fun toggleBookmark(video: VideoItem) {
        viewModelScope.launch {
            repository.toggleBookmark(video)
        }
    }

    fun toggleFollow(username: String, currentFollowing: Boolean) {
        viewModelScope.launch {
            repository.toggleFollow(username, currentFollowing)
        }
    }

    fun openComments(videoId: String) {
        _activeCommentsVideoId.value = videoId
        viewModelScope.launch {
            repository.getCommentsForVideo(videoId).collect { comments ->
                _activeComments.value = comments
            }
        }
    }

    fun closeComments() {
        _activeCommentsVideoId.value = null
    }

    fun addComment(videoId: String, text: String) {
        if (text.isBlank()) return
        viewModelScope.launch {
            repository.addComment(videoId, text)
        }
    }

    fun toggleCommentLike(comment: CommentItem) {
        viewModelScope.launch {
            repository.toggleCommentLike(comment)
        }
    }

    fun openShare(video: VideoItem) {
        _activeShareVideo.value = video
    }

    fun closeShare() {
        _activeShareVideo.value = null
    }

    fun openCreatorProfile(username: String) {
        _selectedCreatorUsername.value = username
    }

    fun closeCreatorProfile() {
        _selectedCreatorUsername.value = null
    }

    fun openSoundDetail(sound: SoundTrack) {
        _selectedSound.value = sound
    }

    fun openSoundByTitle(title: String, artist: String) {
        _selectedSound.value = SoundTrack(
            id = "sound_${title.hashCode()}",
            title = title,
            artist = artist,
            coverUrl = "",
            duration = "0:30",
            videoUsageCount = "1.2M videos"
        )
    }

    fun closeSoundDetail() {
        _selectedSound.value = null
    }

    fun openLiveStream() {
        _isLiveStreamOpen.value = true
    }

    fun closeLiveStream() {
        _isLiveStreamOpen.value = false
    }

    fun sendLiveHeart() {
        _liveLikesCount.value = _liveLikesCount.value + 1
    }

    fun sendLiveMessage(text: String) {
        if (text.isBlank()) return
        _liveMessages.value = _liveMessages.value + LiveChatMessage("You", text, "🔥")
    }

    fun sendLiveGift(giftName: String) {
        _liveMessages.value = _liveMessages.value + LiveChatMessage("You", "Sent a $giftName! ✨🎁", "👑")
        _liveLikesCount.value = _liveLikesCount.value + 100
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun openSearch() {
        _isSearchOpen.value = true
    }

    fun closeSearch() {
        _isSearchOpen.value = false
        _searchQuery.value = ""
    }

    fun getCreatorProfile(username: String): UserProfile {
        return repository.getCreatorProfile(username)
    }

    fun getTrendingSounds(): List<SoundTrack> {
        return repository.trendingSounds
    }

    fun createVideo(
        caption: String,
        hashtags: List<String>,
        soundTitle: String,
        soundArtist: String,
        videoType: String,
        gradientColors: List<Long>
    ) {
        viewModelScope.launch {
            repository.createNewVideo(
                caption = caption,
                hashtags = hashtags,
                soundTitle = soundTitle,
                soundArtist = soundArtist,
                videoType = videoType,
                gradientColors = gradientColors
            )
            _currentTab.value = MainTab.HOME
            _currentVideoIndex.value = 0
        }
    }
}

class TikTokViewModelFactory(
    private val repository: TikTokRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return TikTokViewModel(repository) as T
    }
}
