package com.example.ui.create

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.FlipCameraAndroid
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.SoundTrack
import com.example.ui.camera.CameraPreview
import com.example.ui.camera.CameraViewModel
import com.example.ui.theme.TikTokCard
import com.example.ui.theme.TikTokCyan
import com.example.ui.theme.TikTokDarkSurface
import com.example.ui.theme.TikTokRed
import com.example.ui.theme.TikTokTextSecondary
import com.example.ui.theme.TikTokWhite
import com.example.ui.theme.TikTokYellow
import kotlinx.coroutines.delay

@Composable
fun CreateVideoScreen(
    trendingSounds: List<SoundTrack>,
    onClose: () -> Unit,
    onPostVideo: (caption: String, hashtags: List<String>, soundTitle: String, soundArtist: String, videoType: String, colors: List<Long>) -> Unit,
    cameraViewModel: CameraViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    BackHandler { onClose() }

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraUiState by cameraViewModel.uiState.collectAsStateWithLifecycle()

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraPermission = isGranted
        cameraViewModel.setPermissionGranted(isGranted)
        if (isGranted) {
            cameraViewModel.bindCamera(context, lifecycleOwner)
        }
    }

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        } else {
            cameraViewModel.setPermissionGranted(true)
            cameraViewModel.bindCamera(context, lifecycleOwner)
        }
    }

    DisposableEffect(lifecycleOwner) {
        onDispose {
            cameraViewModel.unbind()
        }
    }

    var isRecording by remember { mutableStateOf(false) }
    var recordingProgress by remember { mutableFloatStateOf(0f) }
    var countdownTimer by remember { mutableIntStateOf(0) }
    var isPostStep by remember { mutableStateOf(false) }

    // Controls state
    var selectedSound by remember { mutableStateOf(trendingSounds.firstOrNull() ?: SoundTrack("s1", "Trending Beat", "TikTok Sounds", "", "0:30", "1M")) }
    var showSoundPicker by remember { mutableStateOf(false) }
    var selectedSpeed by remember { mutableStateOf("1x") }
    var selectedFilter by remember { mutableStateOf("Cyber") }
    var selectedTimer by remember { mutableIntStateOf(0) } // 0 = off, 3 = 3s, 10 = 10s
    var selectedDuration by remember { mutableStateOf("15s") }

    // Post details
    var captionInput by remember { mutableStateOf("") }
    val hashtagsList = listOf("fyp", "viral", "trend", "foryou", "tiktok", "dance", "tech")
    var selectedTags by remember { mutableStateOf(listOf("fyp", "viral")) }

    // Timer countdown effect
    LaunchedEffect(countdownTimer) {
        if (countdownTimer > 0) {
            delay(1000)
            countdownTimer -= 1
            if (countdownTimer == 0) {
                isRecording = true
                cameraViewModel.captureVideo(context)
            }
        }
    }

    // Recording progress effect
    LaunchedEffect(isRecording) {
        if (isRecording) {
            val totalSeconds = if (selectedDuration == "15s") 15 else 60
            val steps = totalSeconds * 20
            for (step in 1..steps) {
                if (!isRecording) break
                delay(50)
                recordingProgress = step.toFloat() / steps.toFloat()
                if (step % 20 == 0) {
                    cameraViewModel.updateRecordingDuration(step / 20)
                }
            }
            if (isRecording) {
                isRecording = false
                cameraViewModel.stopVideoCapture()
                isPostStep = true
            }
        } else {
            recordingProgress = 0f
        }
    }

    val filterColors = when (selectedFilter) {
        "Cyber" -> listOf(0xFF0F2027, 0xFF203A43, 0xFF2C5364)
        "Warm" -> listOf(0xFF4B1248, 0xFFF0C27B, 0xFF8E2800)
        "Neon" -> listOf(0xFF3A1C71, 0xFFD76D77, 0xFFFFAF7B)
        "Noir" -> listOf(0xFF141E30, 0xFF243B55, 0xFF000000)
        else -> listOf(0xFF1F1C2C, 0xFF928DAB, 0xFF4A00E0)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .testTag("create_video_screen")
    ) {
        if (!isPostStep) {
            // CameraX Live Preview or Fallback Viewfinder Canvas
            if (hasCameraPermission && cameraUiState.isBound) {
                CameraPreview(
                    onSurfaceProviderReady = { surfaceProvider ->
                        cameraViewModel.attachSurfaceProvider(surfaceProvider)
                    },
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                // Live Viewfinder Animated Canvas
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val base = filterColors.map { Color(it) }
                    drawRect(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                base.getOrElse(0) { Color.Black },
                                base.getOrElse(1) { Color(0xFF111111) },
                                base.getOrElse(2) { Color(0xFF222222) }
                            )
                        )
                    )
                }
            }

            // Viewfinder framing grid & recording overlay
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height
                drawLine(Color.White.copy(alpha = 0.15f), Offset(w * 0.33f, 0f), Offset(w * 0.33f, h), 1f)
                drawLine(Color.White.copy(alpha = 0.15f), Offset(w * 0.66f, 0f), Offset(w * 0.66f, h), 1f)
                drawLine(Color.White.copy(alpha = 0.15f), Offset(0f, h * 0.33f), Offset(w, h * 0.33f), 1f)
                drawLine(Color.White.copy(alpha = 0.15f), Offset(0f, h * 0.66f), Offset(w, h * 0.66f), 1f)

                if (isRecording) {
                    drawRect(
                        color = TikTokRed.copy(alpha = 0.3f),
                        size = size
                    )
                }
            }

            // Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onClose,
                    modifier = Modifier.testTag("close_camera_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = TikTokWhite,
                        modifier = Modifier.size(28.dp)
                    )
                }

                // Add Sound Pill
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.Black.copy(alpha = 0.45f))
                        .clickable { showSoundPicker = !showSoundPicker }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                        .testTag("add_sound_pill")
                ) {
                    Icon(
                        imageVector = Icons.Default.MusicNote,
                        contentDescription = "Sound",
                        tint = TikTokCyan,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = selectedSound.title,
                        color = TikTokWhite,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                }

                // Flash toggle (wired to CameraViewModel)
                IconButton(
                    onClick = { cameraViewModel.toggleFlash() },
                    modifier = Modifier.testTag("flash_toggle_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.FlashOn,
                        contentDescription = "Flash",
                        tint = if (cameraUiState.isFlashOn) TikTokYellow else TikTokWhite,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // Recording Progress Bar
            if (isRecording) {
                LinearProgressIndicator(
                    progress = { recordingProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(top = 56.dp)
                        .height(3.dp),
                    color = TikTokRed,
                    trackColor = Color.White.copy(alpha = 0.2f)
                )
            }

            // Right Vertical Camera Toolbar
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 12.dp)
            ) {
                CameraToolItem(
                    icon = Icons.Default.FlipCameraAndroid,
                    label = "Flip",
                    onClick = { cameraViewModel.flipCamera(lifecycleOwner) },
                    testTag = "tool_flip"
                )

                CameraToolItem(
                    icon = Icons.Default.Speed,
                    label = selectedSpeed,
                    onClick = {
                        selectedSpeed = when (selectedSpeed) {
                            "1x" -> "2x"
                            "2x" -> "3x"
                            "3x" -> "0.5x"
                            else -> "1x"
                        }
                    },
                    testTag = "tool_speed"
                )

                CameraToolItem(
                    icon = Icons.Default.AutoAwesome,
                    label = selectedFilter,
                    onClick = {
                        selectedFilter = when (selectedFilter) {
                            "Cyber" -> "Warm"
                            "Warm" -> "Neon"
                            "Neon" -> "Noir"
                            else -> "Cyber"
                        }
                    },
                    testTag = "tool_filter"
                )

                CameraToolItem(
                    icon = Icons.Default.Timer,
                    label = if (selectedTimer == 0) "Timer" else "${selectedTimer}s",
                    isActive = selectedTimer > 0,
                    onClick = {
                        selectedTimer = when (selectedTimer) {
                            0 -> 3
                            3 -> 10
                            else -> 0
                        }
                    },
                    testTag = "tool_timer"
                )
            }

            // Countdown Overlay
            if (countdownTimer > 0) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = countdownTimer.toString(),
                        color = TikTokRed,
                        fontSize = 90.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }

            // Bottom Controls (Duration tabs & Record Button)
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(bottom = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Duration Mode Selector
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.padding(bottom = 20.dp)
                ) {
                    listOf("15s", "60s", "Photo").forEach { duration ->
                        val isSelected = selectedDuration == duration
                        Text(
                            text = duration,
                            color = if (isSelected) TikTokWhite else TikTokTextSecondary,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 14.sp,
                            modifier = Modifier
                                .clickable { selectedDuration = duration }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                .testTag("duration_$duration")
                        )
                    }
                }

                // Floating Action Button (FAB) for Recording Videos
                val infinitePulse = rememberInfiniteTransition(label = "record_ring")
                val ringScale by infinitePulse.animateFloat(
                    initialValue = 1f,
                    targetValue = 1.15f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(600, easing = FastOutSlowInEasing),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "ring"
                )

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(96.dp)
                        .testTag("record_button")
                ) {
                    // Outer Ring with animated pulse during recording
                    Box(
                        modifier = Modifier
                            .size(92.dp)
                            .scale(if (isRecording || cameraUiState.isRecording) ringScale else 1f)
                            .clip(CircleShape)
                            .border(
                                width = 3.dp,
                                color = if (isRecording || cameraUiState.isRecording) TikTokRed else TikTokWhite.copy(alpha = 0.8f),
                                shape = CircleShape
                            )
                    )

                    // Material 3 Floating Action Button (FAB) wired to cameraViewModel video capture
                    FloatingActionButton(
                        onClick = {
                            if (isRecording || cameraUiState.isRecording) {
                                isRecording = false
                                cameraViewModel.stopVideoCapture()
                                isPostStep = true
                            } else {
                                if (selectedTimer > 0) {
                                    countdownTimer = selectedTimer
                                } else {
                                    isRecording = true
                                    cameraViewModel.captureVideo(context)
                                }
                            }
                        },
                        shape = CircleShape,
                        containerColor = TikTokRed,
                        contentColor = TikTokWhite,
                        elevation = FloatingActionButtonDefaults.elevation(
                            defaultElevation = 6.dp,
                            pressedElevation = 10.dp
                        ),
                        modifier = Modifier
                            .size(76.dp)
                            .testTag("record_fab")
                    ) {
                        if (isRecording || cameraUiState.isRecording) {
                            // Stop Recording indicator
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(TikTokWhite)
                            )
                        } else {
                            // Video Record Action Icon
                            Icon(
                                imageVector = Icons.Filled.Videocam,
                                contentDescription = "Record Video",
                                tint = TikTokWhite,
                                modifier = Modifier.size(38.dp)
                            )
                        }
                    }
                }
            }

            // Sound Picker Bottom Modal
            if (showSoundPicker) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.75f))
                        .clickable { showSoundPicker = false },
                    contentAlignment = Alignment.BottomCenter
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                            .background(TikTokDarkSurface)
                            .clickable(enabled = false) {}
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Select Audio Track",
                            color = TikTokWhite,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                        trendingSounds.forEach { sound ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        selectedSound = sound
                                        showSoundPicker = false
                                    }
                                    .padding(vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(text = sound.title, color = TikTokWhite, fontWeight = FontWeight.Bold)
                                    Text(text = "${sound.artist} • ${sound.duration}", color = TikTokTextSecondary, fontSize = 12.sp)
                                }
                                if (selectedSound.id == sound.id) {
                                    Icon(Icons.Default.Check, contentDescription = null, tint = TikTokRed)
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // Post Video Step
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding()
                    .padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    IconButton(onClick = { isPostStep = false }) {
                        Icon(Icons.Default.Close, contentDescription = "Back", tint = TikTokWhite)
                    }
                    Text(
                        text = "New Post",
                        color = TikTokWhite,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(48.dp))
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Preview Thumbnail Box + Caption Input
                Row(modifier = Modifier.fillMaxWidth()) {
                    Box(
                        modifier = Modifier
                            .size(width = 90.dp, height = 120.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                Brush.verticalGradient(filterColors.map { Color(it) })
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "Preview", color = TikTokWhite, fontSize = 12.sp)
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    OutlinedTextField(
                        value = captionInput,
                        onValueChange = { captionInput = it },
                        placeholder = { Text("Write a caption and describe your video...", color = TikTokTextSecondary, fontSize = 14.sp) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = TikTokCard,
                            unfocusedContainerColor = TikTokCard,
                            focusedBorderColor = TikTokCyan,
                            unfocusedBorderColor = Color(0xFF2C2C2C),
                            focusedTextColor = TikTokWhite,
                            unfocusedTextColor = TikTokWhite
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(120.dp)
                            .testTag("caption_input_field")
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(text = "Add Hashtags", color = TikTokWhite, fontWeight = FontWeight.Bold, fontSize = 14.sp)

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    hashtagsList.forEach { tag ->
                        val isSelected = selectedTags.contains(tag)
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (isSelected) TikTokRed else TikTokCard)
                                .border(1.dp, if (isSelected) TikTokRed else Color(0xFF333333), RoundedCornerShape(16.dp))
                            .clickable {
                                selectedTags = if (isSelected) {
                                    selectedTags - tag
                                } else {
                                    selectedTags + tag
                                }
                            }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                            .testTag("tag_chip_$tag"),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "#$tag",
                                color = if (isSelected) Color.White else TikTokWhite,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Sound Info Card
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(TikTokCard)
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.MusicNote, contentDescription = null, tint = TikTokCyan)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(text = selectedSound.title, color = TikTokWhite, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text(text = selectedSound.artist, color = TikTokTextSecondary, fontSize = 11.sp)
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // Post Button
                Button(
                    onClick = {
                        val finalCaption = if (captionInput.isBlank()) "My awesome video on TikTok! ✨" else captionInput
                        onPostVideo(
                            finalCaption,
                            selectedTags,
                            selectedSound.title,
                            selectedSound.artist,
                            selectedFilter.lowercase(),
                            filterColors
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TikTokRed),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("post_to_tiktok_button")
                ) {
                    Text(
                        text = "Post to TikTok",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun CameraToolItem(
    icon: ImageVector,
    label: String,
    isActive: Boolean = false,
    onClick: () -> Unit,
    testTag: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { onClick() }
            .testTag(testTag)
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(if (isActive) TikTokRed else Color.Black.copy(alpha = 0.45f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = TikTokWhite,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            color = TikTokWhite,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
        )
    }
}
