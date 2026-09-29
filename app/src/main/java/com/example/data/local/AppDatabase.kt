package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.AdMobSettings
import com.example.data.model.UnlockRecord
import com.example.data.model.User
import com.example.data.model.Video
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [User::class, Video::class, UnlockRecord::class, AdMobSettings::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun videoDao(): VideoDao
    abstract fun unlockDao(): UnlockDao
    abstract fun settingsDao(): SettingsDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "short6t9_database"
                )
                    .addCallback(DatabaseCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class DatabaseCallback : Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                CoroutineScope(Dispatchers.IO).launch {
                    populateInitialData(database)
                }
            }
        }

        private suspend fun populateInitialData(database: AppDatabase) {
            val userDao = database.userDao()
            val videoDao = database.videoDao()
            val settingsDao = database.settingsDao()

            // Default Admin Account
            userDao.insertUser(
                User(
                    id = "admin_01",
                    email = "admin@short6t9.com",
                    password = "admin6t9",
                    displayName = "SHORT 6T9 Admin",
                    isAdmin = true
                )
            )

            // Default User Account
            userDao.insertUser(
                User(
                    id = "user_demo",
                    email = "user@short6t9.com",
                    password = "user1234",
                    displayName = "Alex Hunter",
                    isAdmin = false
                )
            )

            // Default Initial AdMob Settings
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

            // Curated High-Definition Initial Videos
            val initialVideos = listOf(
                Video(
                    id = "vid_6t9_01",
                    title = "Neon Cyber Drift • 6T9 Special",
                    description = "High octane night chase through glowing futuristic streets. Unlock to watch full high bitrate video & download!",
                    videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4",
                    thumbnailUrl = "https://images.unsplash.com/photo-1542751371-adc38448a05e?w=800&auto=format&fit=crop",
                    duration = "0:15",
                    isLocked = true,
                    isDownloadEnabled = true,
                    isPublished = true,
                    viewsCount = 1240,
                    downloadsCount = 380,
                    category = "Action"
                ),
                Video(
                    id = "vid_6t9_02",
                    title = "Hyper Speed Escape • Extreme Stunts",
                    description = "Adrenaline packed parkour and hyper speed action. Pure cinematic visual soundscape.",
                    videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4",
                    thumbnailUrl = "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=800&auto=format&fit=crop",
                    duration = "0:15",
                    isLocked = false,
                    isDownloadEnabled = true,
                    isPublished = true,
                    viewsCount = 890,
                    downloadsCount = 145,
                    category = "Trending"
                ),
                Video(
                    id = "vid_6t9_03",
                    title = "Urban Sunset Meltdown • Ultra 4K",
                    description = "Golden hour vibes across metropolitan skyscrapers with vibrant synth beats. Unlock to watch!",
                    videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerJoyBlazes.mp4",
                    thumbnailUrl = "https://images.unsplash.com/photo-1508739773434-c26b3d09e071?w=800&auto=format&fit=crop",
                    duration = "0:15",
                    isLocked = true,
                    isDownloadEnabled = true,
                    isPublished = true,
                    viewsCount = 2150,
                    downloadsCount = 610,
                    category = "Trending"
                ),
                Video(
                    id = "vid_6t9_04",
                    title = "Midnight Quantum Core Meltdown",
                    description = "Sci-fi VFX showcase of power plant explosion and futuristic mecha warfare.",
                    videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerMeltdowns.mp4",
                    thumbnailUrl = "https://images.unsplash.com/photo-1511512578047-dfb367046420?w=800&auto=format&fit=crop",
                    duration = "0:15",
                    isLocked = true,
                    isDownloadEnabled = false,
                    isPublished = true,
                    viewsCount = 540,
                    downloadsCount = 0,
                    category = "Shorts"
                ),
                Video(
                    id = "vid_6t9_05",
                    title = "Tokyo Underground Beats & Street Life",
                    description = "Night market neon reflections, rain, and lo-fi hip hop bass. Free watch for all viewers.",
                    videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/WeAreGoingOnBullrun.mp4",
                    thumbnailUrl = "https://images.unsplash.com/photo-1509198397868-475647b2a1e5?w=800&auto=format&fit=crop",
                    duration = "0:30",
                    isLocked = false,
                    isDownloadEnabled = true,
                    isPublished = true,
                    viewsCount = 3420,
                    downloadsCount = 920,
                    category = "Shorts"
                )
            )

            videoDao.insertVideos(initialVideos)
        }
    }
}
