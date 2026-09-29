package com.example.data.model

import androidx.room.Entity
import androidx.room.Index

@Entity(
    tableName = "unlock_records",
    primaryKeys = ["userId", "videoId"],
    indices = [Index(value = ["userId"]), Index(value = ["videoId"])]
)
data class UnlockRecord(
    val userId: String,
    val videoId: String,
    val unlockedAt: Long = System.currentTimeMillis()
)
