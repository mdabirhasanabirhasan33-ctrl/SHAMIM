package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "videos")
data class Video(
    @PrimaryKey
    val id: String,
    val title: String,
    val description: String,
    val videoUrl: String,
    val thumbnailUrl: String,
    val duration: String = "0:30",
    val isLocked: Boolean = true,
    val isDownloadEnabled: Boolean = true,
    val isPublished: Boolean = true,
    val viewsCount: Int = 0,
    val downloadsCount: Int = 0,
    val category: String = "Shorts",
    val createdAt: Long = System.currentTimeMillis()
)
