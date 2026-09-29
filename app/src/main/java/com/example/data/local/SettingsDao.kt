package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.AdMobSettings
import kotlinx.coroutines.flow.Flow

@Dao
interface SettingsDao {
    @Query("SELECT * FROM admob_settings WHERE id = 1 LIMIT 1")
    fun getAdMobSettingsFlow(): Flow<AdMobSettings?>

    @Query("SELECT * FROM admob_settings WHERE id = 1 LIMIT 1")
    suspend fun getAdMobSettings(): AdMobSettings?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveAdMobSettings(settings: AdMobSettings)
}
