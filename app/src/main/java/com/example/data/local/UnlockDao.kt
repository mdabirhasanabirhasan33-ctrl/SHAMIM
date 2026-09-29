package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.UnlockRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface UnlockDao {
    @Query("SELECT COUNT(*) > 0 FROM unlock_records WHERE userId = :userId AND videoId = :videoId")
    fun isVideoUnlockedFlow(userId: String, videoId: String): Flow<Boolean>

    @Query("SELECT COUNT(*) > 0 FROM unlock_records WHERE userId = :userId AND videoId = :videoId")
    suspend fun hasUnlockRecord(userId: String, videoId: String): Boolean

    @Query("SELECT videoId FROM unlock_records WHERE userId = :userId")
    fun getUnlockedVideoIds(userId: String): Flow<List<String>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun recordUnlock(record: UnlockRecord)

    @Query("SELECT COUNT(*) FROM unlock_records WHERE userId = :userId")
    fun getUserUnlockCount(userId: String): Flow<Int>
}
