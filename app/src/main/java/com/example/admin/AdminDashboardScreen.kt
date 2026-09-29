package com.example.admin

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DownloadDone
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.AdMobSettings
import com.example.data.model.User
import com.example.data.model.Video
import com.example.data.repository.Short6t9Repository
import com.example.ui.theme.AmberGold
import com.example.ui.theme.CrimsonPrimary
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceCard
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.launch

@Composable
fun AdminDashboardScreen(
    adminUser: User,
    repository: Short6t9Repository,
    onLogout: () -> Unit,
    onOpenUserApp: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val allVideos by repository.getAllVideos().collectAsState(initial = emptyList())
    val totalUsers by repository.getUserCount().collectAsState(initial = 0)
    val totalViews by repository.getTotalViews().collectAsState(initial = 0)
    val totalDownloads by repository.getTotalDownloads().collectAsState(initial = 0)
    val admobSettings by repository.getAdMobSettingsFlow().collectAsState(initial = AdMobSettings())

    var showVideoDialog by remember { mutableStateOf(false) }
    var editingVideo by remember { mutableStateOf<Video?>(null) }
    var showAdMobDialog by remember { mutableStateOf(false) }
    var videoToDelete by remember { mutableStateOf<Video?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = AmberGold
                    ) {
                        Icon(
                            imageVector = Icons.Default.AdminPanelSettings,
                            contentDescription = "Admin",
                            tint = Color.Black,
                            modifier = Modifier
                                .padding(6.dp)
                                .size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Column {
                        Text(
                            text = "SHORT 6T9 ADMIN",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Black,
                            color = TextPrimary
                        )
                        Text(
                            text = "Control Center • ${adminUser.displayName}",
                            style = MaterialTheme.typography.labelSmall,
                            color = AmberGold
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Open User App shortcut
                    IconButton(
                        onClick = onOpenUserApp,
                        modifier = Modifier.testTag("admin_open_user_app_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Smartphone,
                            contentDescription = "User App",
                            tint = TextSecondary
                        )
                    }

                    // AdMob Settings Button
                    IconButton(
                        onClick = { showAdMobDialog = true },
                        modifier = Modifier.testTag("admin_open_admob_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.AttachMoney,
                            contentDescription = "AdMob Monetization",
                            tint = AmberGold
                        )
                    }

                    // Logout
                    IconButton(
                        onClick = onLogout,
                        modifier = Modifier.testTag("admin_logout_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "Logout",
                            tint = ErrorRed
                        )
                    }
                }
            }

            // Analytics Overview Cards
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MetricMiniCard(
                    title = "Users",
                    value = "$totalUsers",
                    icon = Icons.Default.Group,
                    color = CrimsonPrimary,
                    modifier = Modifier.weight(1f)
                )
                MetricMiniCard(
                    title = "Videos",
                    value = "${allVideos.size}",
                    icon = Icons.Default.VideoLibrary,
                    color = AmberGold,
                    modifier = Modifier.weight(1f)
                )
                MetricMiniCard(
                    title = "Views",
                    value = "$totalViews",
                    icon = Icons.Default.Visibility,
                    color = SuccessGreen,
                    modifier = Modifier.weight(1f)
                )
                MetricMiniCard(
                    title = "Downloads",
                    value = "$totalDownloads",
                    icon = Icons.Default.DownloadDone,
                    color = Color(0xFF38BDF8),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Sub-header with Add Video trigger
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Catalog Management (${allVideos.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Button(
                    onClick = {
                        editingVideo = null
                        showVideoDialog = true
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AmberGold),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("admin_add_video_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        tint = Color.Black,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Video", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }

            // Video Management List
            LazyColumn(
                contentPadding = PaddingValues(
                    start = 16.dp,
                    end = 16.dp,
                    top = 8.dp,
                    bottom = 90.dp
                ),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("admin_video_list")
            ) {
                items(allVideos, key = { it.id }) { video ->
                    AdminVideoRowCard(
                        video = video,
                        onEdit = {
                            editingVideo = video
                            showVideoDialog = true
                        },
                        onDelete = {
                            videoToDelete = video
                        },
                        onTogglePublish = { published ->
                            scope.launch {
                                repository.updateVideo(video.copy(isPublished = published))
                                snackbarHostState.showSnackbar(
                                    if (published) "Video published to User App" else "Video unpublished"
                                )
                            }
                        },
                        onToggleLock = { locked ->
                            scope.launch {
                                repository.updateVideo(video.copy(isLocked = locked))
                                snackbarHostState.showSnackbar(
                                    if (locked) "Video locked (requires AdMob Rewarded Ad)" else "Video unlocked for all"
                                )
                            }
                        },
                        onToggleDownload = { downloadEnabled ->
                            scope.launch {
                                repository.updateVideo(video.copy(isDownloadEnabled = downloadEnabled))
                                snackbarHostState.showSnackbar(
                                    if (downloadEnabled) "Downloads enabled for this video" else "Downloads disabled"
                                )
                            }
                        }
                    )
                }
            }
        }

        // Add / Edit Video Modal Dialog
        if (showVideoDialog) {
            AdminVideoEditDialog(
                initialVideo = editingVideo,
                onDismiss = {
                    showVideoDialog = false
                    editingVideo = null
                },
                onSave = { savedVideo ->
                    scope.launch {
                        if (editingVideo != null) {
                            repository.updateVideo(savedVideo)
                            snackbarHostState.showSnackbar("Video updated successfully!")
                        } else {
                            repository.addVideo(savedVideo)
                            snackbarHostState.showSnackbar("New video added and published!")
                        }
                        showVideoDialog = false
                        editingVideo = null
                    }
                }
            )
        }

        // AdMob Monetization Settings Modal Dialog
        if (showAdMobDialog) {
            AdminAdMobSettingsDialog(
                currentSettings = admobSettings,
                onDismiss = { showAdMobDialog = false },
                onSave = { updatedSettings ->
                    scope.launch {
                        repository.saveAdMobSettings(updatedSettings)
                        snackbarHostState.showSnackbar("AdMob settings saved successfully!")
                        showAdMobDialog = false
                    }
                }
            )
        }

        // Delete Confirmation Dialog
        videoToDelete?.let { video ->
            AlertDialog(
                onDismissRequest = { videoToDelete = null },
                title = { Text("Delete Video?", color = TextPrimary) },
                text = {
                    Text(
                        "Are you sure you want to permanently delete \"${video.title}\"? It will be immediately removed from the User App.",
                        color = TextSecondary
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            scope.launch {
                                repository.deleteVideo(video)
                                snackbarHostState.showSnackbar("Video deleted.")
                                videoToDelete = null
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ErrorRed)
                    ) {
                        Text("Delete", color = Color.White)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { videoToDelete = null }) {
                        Text("Cancel", color = TextSecondary)
                    }
                },
                containerColor = DarkSurface
            )
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

@Composable
private fun MetricMiniCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Black,
                color = TextPrimary
            )
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary,
                fontSize = 11.sp
            )
        }
    }
}

@Composable
private fun AdminVideoRowCard(
    video: Video,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onTogglePublish: (Boolean) -> Unit,
    onToggleLock: (Boolean) -> Unit,
    onToggleDownload: (Boolean) -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                // Video thumbnail
                Box(
                    modifier = Modifier
                        .size(width = 90.dp, height = 65.dp)
                        .background(DarkSurfaceCard, RoundedCornerShape(8.dp))
                ) {
                    AsyncImage(
                        model = video.thumbnailUrl,
                        contentDescription = video.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .background(DarkSurfaceCard, RoundedCornerShape(8.dp))
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Title and stats
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = video.title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = "${video.category} • ${video.duration}",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "👁 ${video.viewsCount}",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary
                        )
                        Text(
                            text = "⬇ ${video.downloadsCount}",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary
                        )
                    }
                }

                // Edit & Delete actions
                Row {
                    IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = TextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = ErrorRed,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Admin Quick Control Toggles
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = DarkSurfaceCard,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Published Toggle
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = if (video.isPublished) "Published" else "Draft",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (video.isPublished) SuccessGreen else TextMuted,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Switch(
                            checked = video.isPublished,
                            onCheckedChange = onTogglePublish,
                            colors = SwitchDefaults.colors(checkedThumbColor = SuccessGreen),
                            modifier = Modifier.size(width = 40.dp, height = 24.dp)
                        )
                    }

                    // Lock Toggle
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = if (video.isLocked) "Ad Lock" else "Free",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (video.isLocked) AmberGold else TextMuted,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Switch(
                            checked = video.isLocked,
                            onCheckedChange = onToggleLock,
                            colors = SwitchDefaults.colors(checkedThumbColor = AmberGold),
                            modifier = Modifier.size(width = 40.dp, height = 24.dp)
                        )
                    }

                    // Download Toggle
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = if (video.isDownloadEnabled) "Download" else "No DL",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (video.isDownloadEnabled) Color(0xFF38BDF8) else TextMuted,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Switch(
                            checked = video.isDownloadEnabled,
                            onCheckedChange = onToggleDownload,
                            colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF38BDF8)),
                            modifier = Modifier.size(width = 40.dp, height = 24.dp)
                        )
                    }
                }
            }
        }
    }
}
