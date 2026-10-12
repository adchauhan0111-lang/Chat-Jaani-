package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeDown
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.service.SpeechInputState
import com.example.ui.components.JaaniAvatar
import com.example.ui.theme.WhatsAppDarkBackground
import com.example.ui.theme.WhatsAppGreenLight
import com.example.ui.theme.WhatsAppRed
import com.example.ui.theme.WhatsAppSurface
import com.example.ui.theme.WhatsAppTextPrimary
import com.example.ui.theme.WhatsAppTextSecondary
import kotlinx.coroutines.delay

@Composable
fun VoiceCallScreen(
    jaaniName: String,
    isSpeaking: Boolean,
    inputState: SpeechInputState,
    lastUserSpokenText: String,
    lastJaaniSpokenText: String,
    onStartListening: () -> Unit,
    onStopListening: () -> Unit,
    onEndCall: () -> Unit
) {
    BackHandler {
        onEndCall()
    }

    var callDurationSeconds by remember { mutableIntStateOf(0) }
    var isCallConnected by remember { mutableStateOf(false) }
    var isMuted by remember { mutableStateOf(false) }
    var isSpeakerOn by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        // Simulate quick ringing for 1.5 seconds then connected
        delay(1500)
        isCallConnected = true
        while (true) {
            delay(1000)
            callDurationSeconds++
        }
    }

    val minutes = callDurationSeconds / 60
    val seconds = callDurationSeconds % 60
    val durationFormatted = "%02d:%02d".format(minutes, seconds)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0F1A1C),
                        Color(0xFF0B141A),
                        Color(0xFF142426)
                    )
                )
            )
            .padding(horizontal = 24.dp, vertical = 32.dp)
            .testTag("voice_call_screen")
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            // Security badge
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Encrypted",
                    tint = WhatsAppTextSecondary,
                    modifier = Modifier.size(12.dp)
                )
                Text(
                    text = " End-to-end AI encrypted call",
                    color = WhatsAppTextSecondary,
                    fontSize = 11.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Contact Name
            Text(
                text = "$jaaniName 💕",
                color = WhatsAppTextPrimary,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Call status / timer
            Text(
                text = if (!isCallConnected) "Ringing..." else durationFormatted,
                color = if (!isCallConnected) WhatsAppGreenLight else WhatsAppTextSecondary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Avatar with animated pulse wave
            JaaniAvatar(
                size = 140.dp,
                showOnlineBadge = false,
                isPulsing = isSpeaking || inputState == SpeechInputState.LISTENING
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Live status prompt
            val statusLabel = when {
                isSpeaking -> "$jaaniName is speaking... 🗣️"
                inputState == SpeechInputState.LISTENING -> "Listening to you... 🎙️"
                inputState == SpeechInputState.PROCESSING -> "Thinking... 🤔"
                else -> "Tap the mic below to speak"
            }

            Surface(
                color = if (isSpeaking) Color(0x33FF4081) else if (inputState == SpeechInputState.LISTENING) Color(0x3300E676) else Color(0x22FFFFFF),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.padding(horizontal = 12.dp)
            ) {
                Text(
                    text = statusLabel,
                    color = WhatsAppTextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Live Subtitles Card
            AnimatedVisibility(
                visible = lastUserSpokenText.isNotEmpty() || lastJaaniSpokenText.isNotEmpty(),
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Surface(
                    color = WhatsAppSurface.copy(alpha = 0.8f),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        if (lastUserSpokenText.isNotEmpty()) {
                            Text(
                                text = "You: $lastUserSpokenText",
                                color = WhatsAppGreenLight,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        if (lastJaaniSpokenText.isNotEmpty()) {
                            Text(
                                text = "$jaaniName: $lastJaaniSpokenText",
                                color = WhatsAppTextPrimary,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // In-call Controls Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Speaker toggle
                IconButton(
                    onClick = { isSpeakerOn = !isSpeakerOn },
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(if (isSpeakerOn) Color(0x4400A884) else Color(0x22FFFFFF))
                ) {
                    Icon(
                        imageVector = if (isSpeakerOn) Icons.AutoMirrored.Filled.VolumeUp else Icons.AutoMirrored.Filled.VolumeDown,
                        contentDescription = "Speaker",
                        tint = if (isSpeakerOn) WhatsAppGreenLight else Color.White
                    )
                }

                // Push to talk / Voice input button
                FloatingActionButton(
                    onClick = {
                        if (inputState == SpeechInputState.LISTENING) {
                            onStopListening()
                        } else {
                            onStartListening()
                        }
                    },
                    containerColor = if (inputState == SpeechInputState.LISTENING) WhatsAppGreenLight else WhatsAppSurface,
                    contentColor = if (inputState == SpeechInputState.LISTENING) Color.Black else Color.White,
                    shape = CircleShape,
                    modifier = Modifier
                        .size(72.dp)
                        .testTag("btn_call_mic")
                ) {
                    Icon(
                        imageVector = if (isMuted) Icons.Default.MicOff else Icons.Default.Mic,
                        contentDescription = "Microphone",
                        modifier = Modifier.size(32.dp)
                    )
                }

                // End call red button
                FloatingActionButton(
                    onClick = onEndCall,
                    containerColor = WhatsAppRed,
                    contentColor = Color.White,
                    shape = CircleShape,
                    modifier = Modifier
                        .size(64.dp)
                        .testTag("btn_end_call")
                ) {
                    Icon(
                        imageVector = Icons.Default.CallEnd,
                        contentDescription = "End Call",
                        modifier = Modifier.size(30.dp)
                    )
                }
            }
        }
    }
}
