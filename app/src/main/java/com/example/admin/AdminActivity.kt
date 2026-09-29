package com.example.admin

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.MainActivity
import com.example.data.model.User
import com.example.data.repository.Short6t9Repository
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.MyApplicationTheme

class AdminActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val repository = Short6t9Repository.getInstance(this)

        setContent {
            MyApplicationTheme {
                val currentUser by repository.currentUser.collectAsState(initial = null)
                var authenticatedAdmin by remember { mutableStateOf<User?>(if (currentUser?.isAdmin == true) currentUser else null) }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = DarkBackground
                ) {
                    val admin = authenticatedAdmin
                    if (admin != null) {
                        AdminDashboardScreen(
                            adminUser = admin,
                            repository = repository,
                            onLogout = {
                                authenticatedAdmin = null
                                repository.logout()
                            },
                            onOpenUserApp = {
                                val intent = Intent(this@AdminActivity, MainActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                        )
                    } else {
                        AdminAuthScreen(
                            repository = repository,
                            onAdminAuthenticated = { user ->
                                authenticatedAdmin = user
                            },
                            onOpenUserApp = {
                                val intent = Intent(this@AdminActivity, MainActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                        )
                    }
                }
            }
        }
    }
}
