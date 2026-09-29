package com.example.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.Video
import com.example.ui.theme.AmberGold
import com.example.ui.theme.CrimsonPrimary
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceCard
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.util.UUID

@Composable
fun AdminVideoEditDialog(
    initialVideo: Video? = null,
    onDismiss: () -> Unit,
    onSave: (Video) -> Unit
) {
    val isEditMode = initialVideo != null

    var title by remember { mutableStateOf(initialVideo?.title ?: "") }
    var description by remember { mutableStateOf(initialVideo?.description ?: "") }
    var videoUrl by remember { mutableStateOf(initialVideo?.videoUrl ?: "") }
    var thumbnailUrl by remember { mutableStateOf(initialVideo?.thumbnailUrl ?: "") }
    var duration by remember { mutableStateOf(initialVideo?.duration ?: "0:30") }
    var category by remember { mutableStateOf(initialVideo?.category ?: "Shorts") }

    var isLocked by remember { mutableStateOf(initialVideo?.isLocked ?: true) }
    var isDownloadEnabled by remember { mutableStateOf(initialVideo?.isDownloadEnabled ?: true) }
    var isPublished by remember { mutableStateOf(initialVideo?.isPublished ?: true) }

    var errorMessage by remember { mutableStateOf<String?>(null) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            border = CardDefaults.outlinedCardBorder().copy(width = 1.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
            ) {
                Text(
                    text = if (isEditMode) "Edit Video Details" else "Upload / Add New Video",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Text(
                    text = "Configure metadata, stream URL, locks and downloads",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                if (errorMessage != null) {
                    Text(
                        text = errorMessage ?: "",
                        color = Color.Red,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                // Title
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Video Title") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AmberGold,
                        unfocusedBorderColor = DarkBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("admin_video_title_input")
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Description
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    maxLines = 3,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AmberGold,
                        unfocusedBorderColor = DarkBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("admin_video_desc_input")
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Video URL
                OutlinedTextField(
                    value = videoUrl,
                    onValueChange = { videoUrl = it },
                    label = { Text("Video URL (Direct MP4 / HLS / WebM)") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AmberGold,
                        unfocusedBorderColor = DarkBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("admin_video_url_input")
                )

                // Quick preset button for sample streams
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = {
                            videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4"
                            thumbnailUrl = "https://images.unsplash.com/photo-1542751371-adc38448a05e?w=800&auto=format&fit=crop"
                            duration = "0:15"
                        }
                    ) {
                        Text("Use Sample CDN Stream", color = AmberGold, fontSize = 11.sp)
                    }
                }

                // Thumbnail URL
                OutlinedTextField(
                    value = thumbnailUrl,
                    onValueChange = { thumbnailUrl = it },
                    label = { Text("Thumbnail Image URL") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AmberGold,
                        unfocusedBorderColor = DarkBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("admin_video_thumb_input")
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = duration,
                        onValueChange = { duration = it },
                        label = { Text("Duration (e.g. 0:30)") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AmberGold,
                            unfocusedBorderColor = DarkBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        value = category,
                        onValueChange = { category = it },
                        label = { Text("Category") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AmberGold,
                            unfocusedBorderColor = DarkBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Toggles Card
                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        // Publish Toggle
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Published in App", color = TextPrimary, fontWeight = FontWeight.SemiBold)
                                Text("Visible to all users on Home Feed", color = TextMuted, style = MaterialTheme.typography.labelSmall)
                            }
                            Switch(
                                checked = isPublished,
                                onCheckedChange = { isPublished = it },
                                colors = SwitchDefaults.colors(checkedThumbColor = SuccessGreen)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Lock Toggle
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Lock with Rewarded Ad", color = TextPrimary, fontWeight = FontWeight.SemiBold)
                                Text("User must watch AdMob ad to play", color = TextMuted, style = MaterialTheme.typography.labelSmall)
                            }
                            Switch(
                                checked = isLocked,
                                onCheckedChange = { isLocked = it },
                                colors = SwitchDefaults.colors(checkedThumbColor = AmberGold)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Download Toggle
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Enable Video Download", color = TextPrimary, fontWeight = FontWeight.SemiBold)
                                Text("Allows users to download MP4 file", color = TextMuted, style = MaterialTheme.typography.labelSmall)
                            }
                            Switch(
                                checked = isDownloadEnabled,
                                onCheckedChange = { isDownloadEnabled = it },
                                colors = SwitchDefaults.colors(checkedThumbColor = CrimsonPrimary)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Cancel", color = TextSecondary)
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Button(
                        onClick = {
                            if (title.isBlank() || videoUrl.isBlank()) {
                                errorMessage = "Please provide at least a title and video URL."
                            } else {
                                val savedVideo = initialVideo?.copy(
                                    title = title.trim(),
                                    description = description.trim(),
                                    videoUrl = videoUrl.trim(),
                                    thumbnailUrl = thumbnailUrl.trim().ifBlank {
                                        "https://images.unsplash.com/photo-1542751371-adc38448a05e?w=800&auto=format&fit=crop"
                                    },
                                    duration = duration.trim(),
                                    category = category.trim().ifBlank { "Shorts" },
                                    isLocked = isLocked,
                                    isDownloadEnabled = isDownloadEnabled,
                                    isPublished = isPublished
                                ) ?: Video(
                                    id = "vid_" + UUID.randomUUID().toString().take(8),
                                    title = title.trim(),
                                    description = description.trim(),
                                    videoUrl = videoUrl.trim(),
                                    thumbnailUrl = thumbnailUrl.trim().ifBlank {
                                        "https://images.unsplash.com/photo-1542751371-adc38448a05e?w=800&auto=format&fit=crop"
                                    },
                                    duration = duration.trim(),
                                    category = category.trim().ifBlank { "Shorts" },
                                    isLocked = isLocked,
                                    isDownloadEnabled = isDownloadEnabled,
                                    isPublished = isPublished
                                )
                                onSave(savedVideo)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AmberGold),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("admin_save_video_button")
                    ) {
                        Text(
                            text = if (isEditMode) "Save Changes" else "Create Video",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
