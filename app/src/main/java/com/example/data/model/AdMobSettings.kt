package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "admob_settings")
data class AdMobSettings(
    @PrimaryKey
    val id: Int = 1,
    val appId: String = "ca-app-pub-3940256099942544~3347511713", // Official Google Test App ID
    val rewardedAdUnitId: String = "ca-app-pub-3940256099942544/5224354917", // Official Google Test Rewarded Ad Unit ID
    val bannerAdUnitId: String = "ca-app-pub-3940256099942544/6300978111", // Official Google Test Banner ID
    val interstitialAdUnitId: String = "ca-app-pub-3940256099942544/1033173712", // Official Google Test Interstitial ID
    val useTestAds: Boolean = true,
    val updatedAt: Long = System.currentTimeMillis()
)
