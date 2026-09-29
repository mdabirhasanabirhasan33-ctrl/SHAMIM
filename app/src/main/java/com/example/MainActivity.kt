package com.example

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.ads.AdMobManager
import com.example.data.model.User
import com.example.data.model.Video
import com.example.data.repository.Short6t9Repository
import com.example.ui.auth.AuthScreen
import com.example.ui.home.HomeScreen
import com.example.ui.player.VideoPlayerScreen
import com.example.ui.profile.ProfileScreen
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.MyApplicationTheme
import kotlinx.coroutines.launch

sealed class Screen {
    object Home : Screen()
    data class Player(val video: Video) : Screen()
    object Auth : Screen()
    object Profile : Screen()
}

class MainActivity : ComponentActivity() {
    private lateinit var repository: Short6t9Repository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        repository = Short6t9Repository.getInstance(this)
        AdMobManager.initialize(this)

        val initialVideoId = extractVideoIdFromIntent(intent)

        setContent {
            MyApplicationTheme {
                Short6t9App(
                    repository = repository,
                    initialDeepLinkVideoId = initialVideoId
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
    }

    private fun extractVideoIdFromIntent(intent: Intent?): String? {
        val data: Uri? = intent?.data
        if (data != null) {
            // Scheme: short6t9://video/VID_ID
            if (data.scheme == "short6t9" && data.host == "video") {
                return data.lastPathSegment
            }
            // HTTP/HTTPS deep link: https://domain/video/VID_ID
            val path = data.path
            if (path != null && path.contains("/video/")) {
                return data.lastPathSegment
            }
        }
        return null
    }
}

@Composable
fun Short6t9App(
    repository: Short6t9Repository,
    initialDeepLinkVideoId: String? = null
) {
    val scope = rememberCoroutineScope()
    val currentUser by repository.currentUser.collectAsState(initial = null)

    var currentScreen by remember { mutableStateOf<Screen>(Screen.Home) }

    // Handle deep link resolution
    LaunchedEffect(initialDeepLinkVideoId) {
        if (!initialDeepLinkVideoId.isNullOrBlank()) {
            val video = repository.getVideoById(initialDeepLinkVideoId)
            if (video != null) {
                currentScreen = Screen.Player(video)
            }
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = DarkBackground
    ) {
        AnimatedContent(
            targetState = currentScreen,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "ScreenTransition"
        ) { screen ->
            when (screen) {
                is Screen.Home -> {
                    HomeScreen(
                        repository = repository,
                        currentUser = currentUser,
                        onVideoSelected = { video ->
                            currentScreen = Screen.Player(video)
                        },
                        onAuthClick = {
                            currentScreen = Screen.Auth
                        },
                        onProfileClick = {
                            currentScreen = Screen.Profile
                        }
                    )
                }

                is Screen.Player -> {
                    VideoPlayerScreen(
                        video = screen.video,
                        repository = repository,
                        currentUser = currentUser,
                        onNavigateBack = {
                            currentScreen = Screen.Home
                        }
                    )
                }

                is Screen.Auth -> {
                    AuthScreen(
                        repository = repository,
                        onAuthSuccess = {
                            currentScreen = Screen.Home
                        },
                        onBack = {
                            currentScreen = Screen.Home
                        }
                    )
                }

                is Screen.Profile -> {
                    val user = currentUser
                    if (user != null) {
                        ProfileScreen(
                            user = user,
                            repository = repository,
                            onBack = {
                                currentScreen = Screen.Home
                            }
                        )
                    } else {
                        // Fallback to Auth
                        AuthScreen(
                            repository = repository,
                            onAuthSuccess = {
                                currentScreen = Screen.Home
                            },
                            onBack = {
                                currentScreen = Screen.Home
                            }
                        )
                    }
                }
            }
        }
    }
}
