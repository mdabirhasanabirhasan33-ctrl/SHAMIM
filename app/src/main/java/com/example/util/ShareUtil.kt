package com.example.util

import android.content.Context
import android.content.Intent

object ShareUtil {
    // Official hosting domain provided by AI Studio environment with genuine HTTPS/SSL
    const val BASE_HOST_URL = "https://ais-pre-cdg3oevvjes6ca4puvqb73-300283394321.asia-southeast1.run.app"

    fun getVideoShareUrl(videoId: String): String {
        return "$BASE_HOST_URL/video/$videoId"
    }

    fun shareVideo(context: Context, videoId: String, title: String) {
        val shareUrl = getVideoShareUrl(videoId)
        val shareText = "🔥 Watch \"$title\" on SHORT 6T9!\n\nDirect Link: $shareUrl\n\nExperience high-energy short videos and unlocked exclusive drops on SHORT 6T9."

        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, shareText)
            putExtra(Intent.EXTRA_TITLE, title)
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, "Share Video via SHORT 6T9")
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(shareIntent)
    }
}
