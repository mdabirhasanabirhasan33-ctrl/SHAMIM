package com.example.ui.player

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.DownloadDone
import androidx.compose.material.icons.filled.Forward10
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay10
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.exoplayer.ExoPlayer
import coil.compose.AsyncImage
import com.example.ads.AdMobManager
import com.example.data.model.User
import com.example.data.model.Video
import com.example.data.repository.Short6t9Repository
import com.example.ui.theme.AmberGold
import com.example.ui.theme.CrimsonPrimary
import com.example.ui.theme.CrimsonSecondary
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceCard
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.util.DownloadUtil
import com.example.util.ShareUtil
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun VideoPlayerScreen(
    video: Video,
    repository: Short6t9Repository,
    currentUser: User?,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    BackHandler {
        onNavigateBack()
    }

    // Unlocked status check from DB
    val isUnlockedInDb by repository.isVideoUnlockedForUser(currentUser?.id, video.id).collectAsState(initial = false)
    val isEffectivelyUnlocked = !video.isLocked || isUnlockedInDb

    val admobSettings by repository.getAdMobSettingsFlow().collectAsState(initial = com.example.data.model.AdMobSettings())

    var isPlaying by remember { mutableStateOf(isEffectivelyUnlocked) }
    var isAdLoading by remember { mutableStateOf(false) }
    var isFullscreen by remember { mutableStateOf(false) }
    var hasIncrementedView by remember { mutableStateOf(false) }

    var currentPositionMs by remember { mutableLongStateOf(0L) }
    var totalDurationMs by remember { mutableLongStateOf(1000L) }
    var isDraggingSlider by remember { mutableStateOf(false) }
    var sliderPosition by remember { mutableFloatStateOf(0f) }

    var exoPlayerInstance by remember { mutableStateOf<ExoPlayer?>(null) }
    var showControls by remember { mutableStateOf(true) }

    // Periodically update player progress
    LaunchedEffect(exoPlayerInstance, isPlaying) {
        while (true) {
            exoPlayerInstance?.let { player ->
                if (!isDraggingSlider) {
                    currentPositionMs = player.currentPosition.coerceAtLeast(0L)
                    totalDurationMs = player.duration.coerceAtLeast(1000L)
                    sliderPosition = (currentPositionMs.toFloat() / totalDurationMs.toFloat()).coerceIn(0f, 1f)
                }
            }
            delay(500)
        }
    }

    // Auto-hide controls in playback after 3 seconds
    LaunchedEffect(showControls, isPlaying) {
        if (showControls && isPlaying) {
            delay(3500)
            showControls = false
        }
    }

    val onUnlockClicked: () -> Unit = {
        if (activity != null) {
            AdMobManager.showRewardedAdForUnlock(
                activity = activity,
                settings = admobSettings,
                onLoadingStateChanged = { loading ->
                    isAdLoading = loading
                },
                onRewardEarned = {
                    scope.launch {
                        repository.unlockVideoForUser(currentUser?.id, video.id)
                        isPlaying = true
                        snackbarHostState.showSnackbar("🎉 Video unlocked! Enjoy full playback.")
                    }
                },
                onAdFailedOrCancelled = { reason ->
                    scope.launch {
                        snackbarHostState.showSnackbar(reason)
                    }
                }
            )
        } else {
            scope.launch {
                snackbarHostState.showSnackbar("Unable to initialize Ad on this device context.")
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .then(if (!isFullscreen) Modifier.verticalScroll(rememberScrollState()) else Modifier)
        ) {
            // Top Video Stage
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .then(
                        if (isFullscreen) Modifier.fillMaxSize()
                        else Modifier
                            .fillMaxWidth()
                            .height(280.dp)
                    )
                    .background(Color.Black)
            ) {
                if (isEffectivelyUnlocked) {
                    ExoPlayerComponent(
                        videoUrl = video.videoUrl,
                        isPlaying = isPlaying,
                        onPlaybackStarted = {
                            if (!hasIncrementedView) {
                                hasIncrementedView = true
                                scope.launch { repository.recordView(video.id) }
                            }
                        },
                        onPlayerReady = { player ->
                            exoPlayerInstance = player
                        }
                    )

                    // Overlay Controls on Click
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                showControls = !showControls
                            }
                    ) {
                        androidx.compose.animation.AnimatedVisibility(
                            visible = showControls,
                            enter = fadeIn(),
                            exit = fadeOut(),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.Black.copy(alpha = 0.55f))
                            ) {
                                // Top bar controls
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .statusBarsPadding()
                                        .padding(horizontal = 8.dp, vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    IconButton(
                                        onClick = {
                                            if (isFullscreen) isFullscreen = false else onNavigateBack()
                                        },
                                        modifier = Modifier.testTag("back_button")
                                    ) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                            contentDescription = "Back",
                                            tint = TextPrimary
                                        )
                                    }

                                    Text(
                                        text = video.title,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = TextPrimary,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(horizontal = 8.dp)
                                    )

                                    IconButton(
                                        onClick = { isFullscreen = !isFullscreen },
                                        modifier = Modifier.testTag("fullscreen_toggle")
                                    ) {
                                        Icon(
                                            imageVector = if (isFullscreen) Icons.Default.FullscreenExit else Icons.Default.Fullscreen,
                                            contentDescription = "Toggle Fullscreen",
                                            tint = TextPrimary
                                        )
                                    }
                                }

                                // Center transport controls
                                Row(
                                    modifier = Modifier.align(Alignment.Center),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(24.dp)
                                ) {
                                    // Rewind 10s
                                    IconButton(
                                        onClick = {
                                            exoPlayerInstance?.let { player ->
                                                val target = (player.currentPosition - 10000L).coerceAtLeast(0L)
                                                player.seekTo(target)
                                            }
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Replay10,
                                            contentDescription = "Replay 10 seconds",
                                            tint = TextPrimary,
                                            modifier = Modifier.size(36.dp)
                                        )
                                    }

                                    // Play / Pause FAB
                                    Surface(
                                        shape = CircleShape,
                                        color = CrimsonPrimary,
                                        modifier = Modifier
                                            .size(64.dp)
                                            .clickable {
                                                isPlaying = !isPlaying
                                                exoPlayerInstance?.playWhenReady = isPlaying
                                            }
                                            .testTag("play_pause_button")
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                                contentDescription = if (isPlaying) "Pause" else "Play",
                                                tint = Color.White,
                                                modifier = Modifier.size(36.dp)
                                            )
                                        }
                                    }

                                    // Forward 10s
                                    IconButton(
                                        onClick = {
                                            exoPlayerInstance?.let { player ->
                                                val target = (player.currentPosition + 10000L).coerceAtMost(player.duration)
                                                player.seekTo(target)
                                            }
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Forward10,
                                            contentDescription = "Forward 10 seconds",
                                            tint = TextPrimary,
                                            modifier = Modifier.size(36.dp)
                                        )
                                    }
                                }

                                // Bottom seek bar & time
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .align(Alignment.BottomCenter)
                                        .padding(horizontal = 16.dp, vertical = 8.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = formatDuration(currentPositionMs),
                                            style = MaterialTheme.typography.labelSmall,
                                            color = TextSecondary
                                        )
                                        Text(
                                            text = formatDuration(totalDurationMs),
                                            style = MaterialTheme.typography.labelSmall,
                                            color = TextSecondary
                                        )
                                    }

                                    Slider(
                                        value = sliderPosition,
                                        onValueChange = { pos ->
                                            isDraggingSlider = true
                                            sliderPosition = pos
                                        },
                                        onValueChangeFinished = {
                                            exoPlayerInstance?.let { player ->
                                                val targetMs = (sliderPosition * player.duration).toLong()
                                                player.seekTo(targetMs)
                                                currentPositionMs = targetMs
                                            }
                                            isDraggingSlider = false
                                        },
                                        colors = SliderDefaults.colors(
                                            thumbColor = CrimsonPrimary,
                                            activeTrackColor = CrimsonPrimary,
                                            inactiveTrackColor = Color.White.copy(alpha = 0.3f)
                                        ),
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }
                        }
                    }
                } else {
                    // LOCKED STATE POSTER & UNLOCK BUTTON
                    Box(modifier = Modifier.fillMaxSize()) {
                        AsyncImage(
                            model = video.thumbnailUrl,
                            contentDescription = video.title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )

                        // Dark gradient mask
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            Color.Black.copy(alpha = 0.6f),
                                            Color.Black.copy(alpha = 0.9f)
                                        )
                                    )
                                )
                        )

                        // Top back button
                        IconButton(
                            onClick = onNavigateBack,
                            modifier = Modifier
                                .statusBarsPadding()
                                .padding(8.dp)
                                .align(Alignment.TopStart)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = TextPrimary
                            )
                        }

                        // Center Lock Callout & UNLOCK VIDEO Button
                        Column(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = DarkSurfaceCard,
                                modifier = Modifier.size(60.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = "Locked Video",
                                        tint = AmberGold,
                                        modifier = Modifier.size(32.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = "LOCKED PREMIUM VIDEO",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = AmberGold,
                                letterSpacing = 1.sp
                            )

                            Text(
                                text = "Watch a quick AdMob rewarded ad to unlock and play this video.",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary,
                                modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                            )

                            if (isAdLoading) {
                                CircularProgressIndicator(
                                    color = CrimsonPrimary,
                                    modifier = Modifier.size(36.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Loading AdMob Rewarded Ad...", color = TextSecondary, style = MaterialTheme.typography.labelSmall)
                            } else {
                                Button(
                                    onClick = onUnlockClicked,
                                    colors = ButtonDefaults.buttonColors(containerColor = CrimsonPrimary),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .testTag("unlock_video_button")
                                        .height(50.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.LockOpen,
                                        contentDescription = null,
                                        tint = Color.White
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "UNLOCK VIDEO",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Video Details and Metadata (Only visible if not fullscreen)
            if (!isFullscreen) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    // Title and status badge
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (isEffectivelyUnlocked) SuccessGreen.copy(alpha = 0.15f) else AmberGold.copy(alpha = 0.15f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (isEffectivelyUnlocked) Icons.Default.CheckCircle else Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = if (isEffectivelyUnlocked) SuccessGreen else AmberGold,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isEffectivelyUnlocked) "UNLOCKED" else "LOCKED",
                                    color = if (isEffectivelyUnlocked) SuccessGreen else AmberGold,
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        }

                        Text(
                            text = video.category,
                            style = MaterialTheme.typography.labelMedium,
                            color = TextMuted
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = video.title,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Stats row: Views and Downloads
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Visibility,
                                contentDescription = "Views",
                                tint = TextSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${video.viewsCount} views",
                                color = TextSecondary,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.DownloadDone,
                                contentDescription = "Downloads",
                                tint = TextSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${video.downloadsCount} downloads",
                                color = TextSecondary,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Action buttons: Share & Download
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Share Button
                        OutlinedButton(
                            onClick = {
                                ShareUtil.shareVideo(context, video.id, video.title)
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("share_video_button"),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Share",
                                tint = TextPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Share Link", color = TextPrimary)
                        }

                        // Download Button (Only enabled if unlocked AND admin has enabled downloading)
                        if (video.isDownloadEnabled) {
                            Button(
                                onClick = {
                                    if (isEffectivelyUnlocked) {
                                        DownloadUtil.startDownload(
                                            context = context,
                                            videoUrl = video.videoUrl,
                                            title = video.title,
                                            onStarted = {
                                                scope.launch {
                                                    repository.recordDownload(video.id)
                                                }
                                            }
                                        )
                                    } else {
                                        scope.launch {
                                            snackbarHostState.showSnackbar("Please unlock the video first to download!")
                                        }
                                    }
                                },
                                enabled = isEffectivelyUnlocked,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = CrimsonSecondary,
                                    disabledContainerColor = DarkSurfaceCard
                                ),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .testTag("download_video_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Download,
                                    contentDescription = "Download",
                                    tint = if (isEffectivelyUnlocked) Color.White else TextMuted,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isEffectivelyUnlocked) "Download" else "Unlock to Save",
                                    color = if (isEffectivelyUnlocked) Color.White else TextMuted
                                )
                            }
                        } else {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = DarkSurfaceCard,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.padding(horizontal = 8.dp)
                                ) {
                                    Text(
                                        text = "Download Disabled",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextMuted
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Description Card
                    Card(
                        colors = CardDefaults.cardColors(containerColor = DarkSurface),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Description",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = video.description,
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondary,
                                lineHeight = 20.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // AdMob & Monetization Info Banner
                    Card(
                        colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "💡", fontSize = 20.sp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Monetized with Google AdMob Rewarded Ads",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = if (admobSettings.useTestAds) "Currently using Official AdMob Test Mode IDs." else "Live AdMob IDs configured.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(30.dp))
                }
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(16.dp)
        )
    }
}

private fun formatDuration(millis: Long): String {
    val totalSeconds = (millis / 1000).coerceAtLeast(0)
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format("%02d:%02d", minutes, seconds)
}
