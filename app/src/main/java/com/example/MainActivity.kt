package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.ChatScreen
import com.example.ui.screens.SettingsDialog
import com.example.ui.screens.VoiceCallScreen
import com.example.ui.theme.JaaniChatTheme
import com.example.viewmodel.AppScreen
import com.example.viewmodel.JaaniChatViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            JaaniChatTheme {
                MainAppContent()
            }
        }
    }
}

@Composable
fun MainAppContent(viewModel: JaaniChatViewModel = viewModel()) {
    val messages by viewModel.messages.collectAsStateWithLifecycle()
    val isTyping by viewModel.isTyping.collectAsStateWithLifecycle()
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val lastUserSpokenText by viewModel.lastUserSpokenText.collectAsStateWithLifecycle()
    val lastJaaniSpokenText by viewModel.lastJaaniSpokenText.collectAsStateWithLifecycle()
    val inputState by viewModel.inputState.collectAsStateWithLifecycle()
    val isSpeaking by viewModel.isSpeaking.collectAsStateWithLifecycle()

    var showSettingsDialog by remember { mutableStateOf(false) }

    // Runtime Permission Launcher for Notifications and Audio
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { _ ->
        // Permissions handled
    }

    LaunchedEffect(Unit) {
        val permissionsToRequest = mutableListOf<String>()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionsToRequest.add(Manifest.permission.POST_NOTIFICATIONS)
        }
        permissionsToRequest.add(Manifest.permission.RECORD_AUDIO)
        permissionLauncher.launch(permissionsToRequest.toTypedArray())
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        when (currentScreen) {
            AppScreen.CHAT -> {
                ChatScreen(
                    messages = messages,
                    isTyping = isTyping,
                    settings = settings,
                    onSendMessage = { text -> viewModel.sendMessage(text) },
                    onStartCall = { viewModel.navigateTo(AppScreen.CALL) },
                    onOpenSettings = { showSettingsDialog = true },
                    onSpeakMessage = { text -> viewModel.speakMessage(text) }
                )
            }

            AppScreen.CALL -> {
                VoiceCallScreen(
                    jaaniName = settings.jaaniName,
                    isSpeaking = isSpeaking,
                    inputState = inputState,
                    lastUserSpokenText = lastUserSpokenText,
                    lastJaaniSpokenText = lastJaaniSpokenText,
                    onStartListening = { viewModel.startListeningForCall() },
                    onStopListening = { viewModel.stopListeningForCall() },
                    onEndCall = { viewModel.navigateTo(AppScreen.CHAT) }
                )
            }
        }

        if (showSettingsDialog) {
            SettingsDialog(
                currentSettings = settings,
                onDismiss = { showSettingsDialog = false },
                onSave = { newSettings -> viewModel.updateSettings(newSettings) },
                onClearChat = { viewModel.clearChat() },
                onTestVoice = { sample, pitch, speed -> viewModel.testVoice(sample, pitch, speed) },
                onTestNotification = { viewModel.testNotification() }
            )
        }
    }
}
