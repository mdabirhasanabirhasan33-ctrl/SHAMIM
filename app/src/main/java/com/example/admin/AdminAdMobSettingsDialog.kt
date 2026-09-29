package com.example.admin

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
import com.example.data.model.AdMobSettings
import com.example.ui.theme.AmberGold
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceCard
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun AdminAdMobSettingsDialog(
    currentSettings: AdMobSettings,
    onDismiss: () -> Unit,
    onSave: (AdMobSettings) -> Unit
) {
    var appId by remember { mutableStateOf(currentSettings.appId) }
    var rewardedAdUnitId by remember { mutableStateOf(currentSettings.rewardedAdUnitId) }
    var bannerAdUnitId by remember { mutableStateOf(currentSettings.bannerAdUnitId) }
    var interstitialAdUnitId by remember { mutableStateOf(currentSettings.interstitialAdUnitId) }
    var useTestAds by remember { mutableStateOf(currentSettings.useTestAds) }

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
                    text = "Google AdMob Settings",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Text(
                    text = "Configure live monetization IDs for SHORT 6T9 app",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Test Ads Switch Card
                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Use Google Test Ads Mode",
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Recommended for safety during development to avoid policy strikes",
                                color = TextMuted,
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                        Switch(
                            checked = useTestAds,
                            onCheckedChange = { useTestAds = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = AmberGold)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // AdMob App ID
                OutlinedTextField(
                    value = appId,
                    onValueChange = { appId = it },
                    label = { Text("AdMob App ID") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AmberGold,
                        unfocusedBorderColor = DarkBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("admin_admob_app_id_input")
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Rewarded Ad Unit ID (Crucial for Unlock Video)
                OutlinedTextField(
                    value = rewardedAdUnitId,
                    onValueChange = { rewardedAdUnitId = it },
                    label = { Text("Rewarded Ad Unit ID (UNLOCK VIDEO)") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AmberGold,
                        unfocusedBorderColor = DarkBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("admin_admob_rewarded_id_input")
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Banner Ad Unit ID
                OutlinedTextField(
                    value = bannerAdUnitId,
                    onValueChange = { bannerAdUnitId = it },
                    label = { Text("Banner Ad Unit ID") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AmberGold,
                        unfocusedBorderColor = DarkBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Interstitial Ad Unit ID
                OutlinedTextField(
                    value = interstitialAdUnitId,
                    onValueChange = { interstitialAdUnitId = it },
                    label = { Text("Interstitial Ad Unit ID") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AmberGold,
                        unfocusedBorderColor = DarkBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                // Reset to Test IDs button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = {
                            appId = "ca-app-pub-3940256099942544~3347511713"
                            rewardedAdUnitId = "ca-app-pub-3940256099942544/5224354917"
                            bannerAdUnitId = "ca-app-pub-3940256099942544/6300978111"
                            interstitialAdUnitId = "ca-app-pub-3940256099942544/1033173712"
                            useTestAds = true
                        }
                    ) {
                        Text("Reset to Google Official Test IDs", color = AmberGold, fontSize = 11.sp)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

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
                            onSave(
                                currentSettings.copy(
                                    appId = appId.trim(),
                                    rewardedAdUnitId = rewardedAdUnitId.trim(),
                                    bannerAdUnitId = bannerAdUnitId.trim(),
                                    interstitialAdUnitId = interstitialAdUnitId.trim(),
                                    useTestAds = useTestAds,
                                    updatedAt = System.currentTimeMillis()
                                )
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AmberGold),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("admin_save_admob_button")
                    ) {
                        Text("Save Monetization", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
