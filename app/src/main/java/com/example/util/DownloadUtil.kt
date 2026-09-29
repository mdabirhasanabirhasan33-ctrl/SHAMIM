package com.example.util

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.widget.Toast

object DownloadUtil {
    fun startDownload(
        context: Context,
        videoUrl: String,
        title: String,
        onStarted: () -> Unit
    ) {
        try {
            val uri = Uri.parse(videoUrl)
            val fileName = "SHORT_6T9_${title.replace(Regex("[^a-zA-Z0-9]"), "_")}.mp4"

            val request = DownloadManager.Request(uri)
                .setTitle("Downloading: $title")
                .setDescription("SHORT 6T9 Video Download")
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

            val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as? DownloadManager
            downloadManager?.enqueue(request)

            Toast.makeText(context, "Download started for: $title", Toast.LENGTH_SHORT).show()
            onStarted()
        } catch (e: Exception) {
            Toast.makeText(context, "Download error: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
        }
    }
}
