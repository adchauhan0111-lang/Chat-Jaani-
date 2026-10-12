package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.PersonalityType
import com.example.data.UserSettings
import com.example.ui.theme.WhatsAppDarkBackground
import com.example.ui.theme.WhatsAppDivider
import com.example.ui.theme.WhatsAppGreen
import com.example.ui.theme.WhatsAppRed
import com.example.ui.theme.WhatsAppSurface
import com.example.ui.theme.WhatsAppTextPrimary
import com.example.ui.theme.WhatsAppTextSecondary

@Composable
fun SettingsDialog(
    currentSettings: UserSettings,
    onDismiss: () -> Unit,
    onSave: (UserSettings) -> Unit,
    onClearChat: () -> Unit,
    onTestVoice: (String, Float, Float) -> Unit,
    onTestNotification: () -> Unit
) {
    var jaaniName by remember { mutableStateOf(currentSettings.jaaniName) }
    var userName by remember { mutableStateOf(currentSettings.userName) }
    var personality by remember { mutableStateOf(currentSettings.personality) }
    var ttsEnabled by remember { mutableStateOf(currentSettings.ttsVoiceEnabled) }
    var speechPitch by remember { mutableFloatStateOf(currentSettings.speechPitch) }
    var speechSpeed by remember { mutableFloatStateOf(currentSettings.speechSpeed) }
    var checkInsEnabled by remember { mutableStateOf(currentSettings.checkInsEnabled) }

    var showClearConfirm by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .clip(RoundedCornerShape(20.dp)),
            color = WhatsAppSurface
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Settings & Persona ⚙️",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = WhatsAppTextPrimary
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = WhatsAppTextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // AI Disclaimer Note
                Surface(
                    color = Color(0x3300A884),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "✨ Jaani is an AI companion. She is created for friendly, caring & fun Hinglish chats. Keep interactions sweet and respectful!",
                        color = Color(0xFFC7EBE2),
                        fontSize = 12.sp,
                        modifier = Modifier.padding(12.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Names section
                Text(
                    text = "Names & Nicknames",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = WhatsAppGreen
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = jaaniName,
                    onValueChange = { jaaniName = it },
                    label = { Text("Her Name / Nickname") },
                    placeholder = { Text("Jaani, Babu, Shona, Pari...") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = WhatsAppTextPrimary,
                        unfocusedTextColor = WhatsAppTextPrimary,
                        focusedBorderColor = WhatsAppGreen,
                        unfocusedBorderColor = WhatsAppDivider
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_jaani_name")
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = userName,
                    onValueChange = { userName = it },
                    label = { Text("Your Name (What she calls you)") },
                    placeholder = { Text("Babu, Rahul, Handsome...") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = WhatsAppTextPrimary,
                        unfocusedTextColor = WhatsAppTextPrimary,
                        focusedBorderColor = WhatsAppGreen,
                        unfocusedBorderColor = WhatsAppDivider
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_user_name")
                )

                Spacer(modifier = Modifier.height(18.dp))
                HorizontalDivider(color = WhatsAppDivider)
                Spacer(modifier = Modifier.height(14.dp))

                // Personality Selector
                Text(
                    text = "Personality Vibe",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = WhatsAppGreen
                )
                Spacer(modifier = Modifier.height(8.dp))

                PersonalityType.values().forEach { type ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { personality = type }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (personality == type),
                            onClick = { personality = type },
                            colors = RadioButtonDefaults.colors(selectedColor = WhatsAppGreen)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Text(
                                text = "${type.emoji} ${type.displayName}",
                                color = WhatsAppTextPrimary,
                                fontWeight = FontWeight.Medium,
                                fontSize = 14.sp
                            )
                            Text(
                                text = type.tagline,
                                color = WhatsAppTextSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))
                HorizontalDivider(color = WhatsAppDivider)
                Spacer(modifier = Modifier.height(14.dp))

                // Voice / TTS
                Text(
                    text = "Voice Call & Speech 🎙️",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = WhatsAppGreen
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Speak Responses Out Loud",
                        color = WhatsAppTextPrimary,
                        fontSize = 14.sp
                    )
                    Switch(
                        checked = ttsEnabled,
                        onCheckedChange = { ttsEnabled = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = WhatsAppGreen)
                    )
                }

                if (ttsEnabled) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Pitch: ${"%.2f".format(speechPitch)}",
                        color = WhatsAppTextSecondary,
                        fontSize = 12.sp
                    )
                    Slider(
                        value = speechPitch,
                        onValueChange = { speechPitch = it },
                        valueRange = 0.8f..1.5f,
                        colors = SliderDefaults.colors(
                            thumbColor = WhatsAppGreen,
                            activeTrackColor = WhatsAppGreen
                        )
                    )

                    Text(
                        text = "Speed: ${"%.2f".format(speechSpeed)}",
                        color = WhatsAppTextSecondary,
                        fontSize = 12.sp
                    )
                    Slider(
                        value = speechSpeed,
                        onValueChange = { speechSpeed = it },
                        valueRange = 0.8f..1.4f,
                        colors = SliderDefaults.colors(
                            thumbColor = WhatsAppGreen,
                            activeTrackColor = WhatsAppGreen
                        )
                    )

                    OutlinedButton(
                        onClick = {
                            val sample = "Hii $userName! Kaisi lag rahi hai meri aawaz?"
                            onTestVoice(sample, speechPitch, speechSpeed)
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                            contentDescription = "Test Voice",
                            tint = WhatsAppGreen
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Test Voice Sample", color = WhatsAppGreen)
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))
                HorizontalDivider(color = WhatsAppDivider)
                Spacer(modifier = Modifier.height(14.dp))

                // Notifications / Check-ins
                Text(
                    text = "Check-in Notifications 🔔",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = WhatsAppGreen
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Periodic Cute Check-ins",
                            color = WhatsAppTextPrimary,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "Sends random messages like 'Kya kar rahe ho? Yaad aa rahi thi 💕'",
                            color = WhatsAppTextSecondary,
                            fontSize = 11.sp
                        )
                    }
                    Switch(
                        checked = checkInsEnabled,
                        onCheckedChange = { checkInsEnabled = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = WhatsAppGreen)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(
                    onClick = onTestNotification,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Test Notification",
                        tint = WhatsAppGreen
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Send Test Notification Now", color = WhatsAppGreen)
                }

                Spacer(modifier = Modifier.height(18.dp))
                HorizontalDivider(color = WhatsAppDivider)
                Spacer(modifier = Modifier.height(14.dp))

                // Clear Chat History
                Button(
                    onClick = { showClearConfirm = true },
                    colors = ButtonDefaults.buttonColors(containerColor = WhatsAppRed.copy(alpha = 0.2f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("btn_clear_chat")
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Clear Chat",
                        tint = WhatsAppRed
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Clear All Chat History", color = WhatsAppRed)
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Save button
                Button(
                    onClick = {
                        val newSettings = currentSettings.copy(
                            jaaniName = jaaniName.trim().ifEmpty { "Jaani" },
                            userName = userName.trim().ifEmpty { "Babu" },
                            personality = personality,
                            ttsVoiceEnabled = ttsEnabled,
                            speechPitch = speechPitch,
                            speechSpeed = speechSpeed,
                            checkInsEnabled = checkInsEnabled
                        )
                        onSave(newSettings)
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = WhatsAppGreen),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("btn_save_settings")
                ) {
                    Text(
                        text = "Save Changes ✨",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }

    if (showClearConfirm) {
        AlertDialog(
            onDismissRequest = { showClearConfirm = false },
            title = { Text("Clear Chat?", color = WhatsAppTextPrimary) },
            text = { Text("Are you sure you want to delete all chat history with $jaaniName? Past memories will be erased.", color = WhatsAppTextSecondary) },
            confirmButton = {
                TextButton(
                    onClick = {
                        onClearChat()
                        showClearConfirm = false
                    }
                ) {
                    Text("Delete All", color = WhatsAppRed, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearConfirm = false }) {
                    Text("Cancel", color = WhatsAppTextSecondary)
                }
            },
            containerColor = WhatsAppSurface
        )
    }
}
