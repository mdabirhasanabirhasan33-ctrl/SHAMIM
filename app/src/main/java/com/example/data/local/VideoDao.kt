package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.Video
import kotlinx.coroutines.flow.Flow

@Dao
interface VideoDao {
    @Query("SELECT * FROM videos ORDER BY createdAt DESC")
    fun getAllVideos(): Flow<List<Video>>

    @Query("SELECT * FROM videos WHERE isPublished = 1 ORDER BY createdAt DESC")
    fun getPublishedVideos(): Flow<List<Video>>

    @Query("SELECT * FROM videos WHERE id = :id LIMIT 1")
    suspend fun getVideoById(id: String): Video?

    @Query("SELECT * FROM videos WHERE id = :id LIMIT 1")
    fun getVideoByIdFlow(id: String): Flow<Video?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVideo(video: Video)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVideos(videos: List<Video>)

    @Update
    suspend fun updateVideo(video: Video)

    @Delete
    suspend fun deleteVideo(video: Video)

    @Query("DELETE FROM videos WHERE id = :id")
    suspend fun deleteVideoById(id: String)

    @Query("UPDATE videos SET viewsCount = viewsCount + 1 WHERE id = :id")
    suspend fun incrementViews(id: String)

    @Query("UPDATE videos SET downloadsCount = downloadsCount + 1 WHERE id = :id")
    suspend fun incrementDownloads(id: String)

    @Query("SELECT COUNT(*) FROM videos")
    fun getTotalVideoCount(): Flow<Int>

    @Query("SELECT SUM(viewsCount) FROM videos")
    fun getTotalViewsCount(): Flow<Int?>

    @Query("SELECT SUM(downloadsCount) FROM videos")
    fun getTotalDownloadsCount(): Flow<Int?>
}
