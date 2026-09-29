package com.example.ads

import android.app.Activity
import android.content.Context
import android.util.Log
import com.example.data.model.AdMobSettings
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback

object AdMobManager {
    private const val TAG = "AdMobManager"
    private var isInitialized = false

    // Official Google Test Rewarded Ad Unit ID
    const val TEST_REWARDED_AD_UNIT_ID = "ca-app-pub-3940256099942544/5224354917"

    fun initialize(context: Context) {
        if (!isInitialized) {
            try {
                MobileAds.initialize(context.applicationContext) { status ->
                    Log.d(TAG, "MobileAds initialized: $status")
                }
                isInitialized = true
            } catch (e: Exception) {
                Log.e(TAG, "Error initializing MobileAds", e)
            }
        }
    }

    /**
     * Loads and shows a Rewarded Ad.
     * When reward is earned, [onRewardEarned] is invoked.
     * If user cancels or ad fails, [onAdFailedOrCancelled] is called with reason.
     */
    fun showRewardedAdForUnlock(
        activity: Activity,
        settings: AdMobSettings,
        onLoadingStateChanged: (Boolean) -> Unit,
        onRewardEarned: () -> Unit,
        onAdFailedOrCancelled: (String) -> Unit
    ) {
        initialize(activity)

        val unitId = if (settings.useTestAds || settings.rewardedAdUnitId.isBlank()) {
            TEST_REWARDED_AD_UNIT_ID
        } else {
            settings.rewardedAdUnitId
        }

        onLoadingStateChanged(true)
        val adRequest = AdRequest.Builder().build()

        RewardedAd.load(
            activity,
            unitId,
            adRequest,
            object : RewardedAdLoadCallback() {
                override fun onAdLoaded(rewardedAd: RewardedAd) {
                    onLoadingStateChanged(false)
                    var rewardEarned = false

                    rewardedAd.fullScreenContentCallback = object : FullScreenContentCallback() {
                        override fun onAdDismissedFullScreenContent() {
                            if (rewardEarned) {
                                onRewardEarned()
                            } else {
                                onAdFailedOrCancelled("Ad closed before reward was completed. Video remains locked.")
                            }
                        }

                        override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                            Log.e(TAG, "Ad failed to show: ${adError.message}")
                            onAdFailedOrCancelled("Ad failed to show: ${adError.message}")
                        }

                        override fun onAdShowedFullScreenContent() {
                            Log.d(TAG, "Rewarded Ad showed fullscreen content")
                        }
                    }

                    rewardedAd.show(activity) { rewardItem ->
                        rewardEarned = true
                        Log.d(TAG, "User earned reward: ${rewardItem.amount} ${rewardItem.type}")
                    }
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    onLoadingStateChanged(false)
                    Log.e(TAG, "Ad failed to load: ${loadAdError.message}")
                    onAdFailedOrCancelled("Failed to load Ad (${loadAdError.message}). Please check internet connection.")
                }
            }
        )
    }
}
