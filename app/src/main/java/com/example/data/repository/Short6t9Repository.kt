package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.data.local.AppDatabase
import com.example.data.model.AdMobSettings
import com.example.data.model.UnlockRecord
import com.example.data.model.User
import com.example.data.model.Video
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

class Short6t9Repository(context: Context) {
    private val database = AppDatabase.getInstance(context)
    private val userDao = database.userDao()
    private val videoDao = database.videoDao()
    private val unlockDao = database.unlockDao()
    private val settingsDao = database.settingsDao()

    private val prefs: SharedPreferences =
        context.getSharedPreferences("short6t9_session", Context.MODE_PRIVATE)

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: Flow<User?> = _currentUser.asStateFlow()

    init {
        // Restore session if exists
        val savedUserId = prefs.getString("logged_in_user_id", null)
        if (savedUserId != null) {
            CoroutineScope(Dispatchers.IO).launch {
                val user = userDao.getUserById(savedUserId)
                _currentUser.value = user
            }
        }

        // Ensure database has default data even if onCreate was bypassed
        CoroutineScope(Dispatchers.IO).launch {
            ensureDefaultData()
        }
    }

    private suspend fun ensureDefaultData() {
        if (userDao.getUserByEmail("admin@short6t9.com") == null) {
            userDao.insertUser(
                User(
                    id = "admin_01",
                    email = "admin@short6t9.com",
                    password = "admin6t9",
                    displayName = "SHORT 6T9 Admin",
                    isAdmin = true
                )
            )
        }
        if (userDao.getUserByEmail("user@short6t9.com") == null) {
            userDao.insertUser(
                User(
                    id = "user_demo",
                    email = "user@short6t9.com",
                    password = "user1234",
                    displayName = "Alex Hunter",
                    isAdmin = false
                )
            )
        }
        if (settingsDao.getAdMobSettings() == null) {
            settingsDao.saveAdMobSettings(
                AdMobSettings(
                    id = 1,
                    appId = "ca-app-pub-3940256099942544~3347511713",
                    rewardedAdUnitId = "ca-app-pub-3940256099942544/5224354917",
                    bannerAdUnitId = "ca-app-pub-3940256099942544/6300978111",
                    interstitialAdUnitId = "ca-app-pub-3940256099942544/1033173712",
                    useTestAds = true
                )
            )
        }
    }

    suspend fun register(email: String, pass: String, name: String): Result<User> = withContext(Dispatchers.IO) {
        val trimmedEmail = email.trim().lowercase()
        val existing = userDao.getUserByEmail(trimmedEmail)
        if (existing != null) {
            return@withContext Result.failure(Exception("Email already registered. Please log in."))
        }
        val user = User(
            id = "user_" + UUID.randomUUID().toString().take(8),
            email = trimmedEmail,
            password = pass,
            displayName = name.ifBlank { "User" },
            isAdmin = false
        )
        userDao.insertUser(user)
        setSession(user)
        Result.success(user)
    }

    suspend fun login(email: String, pass: String): Result<User> = withContext(Dispatchers.IO) {
        val trimmedEmail = email.trim().lowercase()
        val user = userDao.getUserByEmail(trimmedEmail)
        if (user == null || user.password != pass) {
            return@withContext Result.failure(Exception("Invalid email or password"))
        }
        setSession(user)
        Result.success(user)
    }

    suspend fun loginAdmin(email: String, pass: String): Result<User> = withContext(Dispatchers.IO) {
        val trimmedEmail = email.trim().lowercase()
        val user = userDao.getUserByEmail(trimmedEmail)
        if (user == null || user.password != pass) {
            return@withContext Result.failure(Exception("Invalid admin credentials"))
        }
        if (!user.isAdmin) {
            return@withContext Result.failure(Exception("Access denied: Not an administrator"))
        }
        Result.success(user)
    }

    private fun setSession(user: User) {
        prefs.edit().putString("logged_in_user_id", user.id).apply()
        _currentUser.value = user
    }

    fun logout() {
        prefs.edit().remove("logged_in_user_id").apply()
        _currentUser.value = null
    }

    // Video streams
    fun getPublishedVideos(): Flow<List<Video>> = videoDao.getPublishedVideos().flowOn(Dispatchers.IO)
    fun getAllVideos(): Flow<List<Video>> = videoDao.getAllVideos().flowOn(Dispatchers.IO)
    fun getVideoByIdFlow(id: String): Flow<Video?> = videoDao.getVideoByIdFlow(id).flowOn(Dispatchers.IO)

    suspend fun getVideoById(id: String): Video? = withContext(Dispatchers.IO) {
        videoDao.getVideoById(id)
    }

    fun isVideoUnlockedForUser(userId: String?, videoId: String): Flow<Boolean> {
        val effectiveUserId = userId ?: _currentUser.value?.id ?: "guest"
        return unlockDao.isVideoUnlockedFlow(effectiveUserId, videoId).flowOn(Dispatchers.IO)
    }

    suspend fun unlockVideoForUser(userId: String?, videoId: String) = withContext(Dispatchers.IO) {
        val effectiveUserId = userId ?: _currentUser.value?.id ?: "guest"
        unlockDao.recordUnlock(
            UnlockRecord(
                userId = effectiveUserId,
                videoId = videoId
            )
        )
    }

    suspend fun recordView(videoId: String) = withContext(Dispatchers.IO) {
        videoDao.incrementViews(videoId)
    }

    suspend fun recordDownload(videoId: String) = withContext(Dispatchers.IO) {
        videoDao.incrementDownloads(videoId)
    }

    // Admin Video CRUD
    suspend fun addVideo(video: Video) = withContext(Dispatchers.IO) {
        videoDao.insertVideo(video)
    }

    suspend fun updateVideo(video: Video) = withContext(Dispatchers.IO) {
        videoDao.updateVideo(video)
    }

    suspend fun deleteVideo(video: Video) = withContext(Dispatchers.IO) {
        videoDao.deleteVideo(video)
    }

    suspend fun deleteVideoById(id: String) = withContext(Dispatchers.IO) {
        videoDao.deleteVideoById(id)
    }

    // Analytics
    fun getUserCount(): Flow<Int> = userDao.getUserCount().flowOn(Dispatchers.IO)
    fun getTotalVideoCount(): Flow<Int> = videoDao.getTotalVideoCount().flowOn(Dispatchers.IO)
    fun getTotalViews(): Flow<Int> = videoDao.getTotalViewsCount().map { it ?: 0 }.flowOn(Dispatchers.IO)
    fun getTotalDownloads(): Flow<Int> = videoDao.getTotalDownloadsCount().map { it ?: 0 }.flowOn(Dispatchers.IO)

    // AdMob Settings
    fun getAdMobSettingsFlow(): Flow<AdMobSettings> = settingsDao.getAdMobSettingsFlow()
        .map { it ?: AdMobSettings() }
        .flowOn(Dispatchers.IO)

    suspend fun getAdMobSettings(): AdMobSettings = withContext(Dispatchers.IO) {
        settingsDao.getAdMobSettings() ?: AdMobSettings()
    }

    suspend fun saveAdMobSettings(settings: AdMobSettings) = withContext(Dispatchers.IO) {
        settingsDao.saveAdMobSettings(settings)
    }

    companion object {
        @Volatile
        private var instance: Short6t9Repository? = null

        fun getInstance(context: Context): Short6t9Repository {
            return instance ?: synchronized(this) {
                instance ?: Short6t9Repository(context.applicationContext).also { instance = it }
            }
        }
    }
}
