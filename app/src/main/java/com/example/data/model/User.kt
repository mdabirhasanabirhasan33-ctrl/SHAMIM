package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey
    val id: String,
    val email: String,
    val password: String,
    val displayName: String,
    val isAdmin: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
