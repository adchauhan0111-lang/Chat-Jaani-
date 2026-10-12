package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.SentimentSatisfiedAlt
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ChatMessageEntity
import com.example.data.UserSettings
import com.example.ui.components.JaaniAvatar
import com.example.ui.components.TimepassBar
import com.example.ui.components.TypingIndicator
import com.example.ui.theme.WhatsAppCheckBlue
import com.example.ui.theme.WhatsAppDarkBackground
import com.example.ui.theme.WhatsAppGreen
import com.example.ui.theme.WhatsAppGreenLight
import com.example.ui.theme.WhatsAppHeader
import com.example.ui.theme.WhatsAppIncomingBubble
import com.example.ui.theme.WhatsAppInputBar
import com.example.ui.theme.WhatsAppOutgoingBubble
import com.example.ui.theme.WhatsAppSurface
import com.example.ui.theme.WhatsAppTextPrimary
import com.example.ui.theme.WhatsAppTextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    messages: List<ChatMessageEntity>,
    isTyping: Boolean,
    settings: UserSettings,
    onSendMessage: (String) -> Unit,
    onStartCall: () -> Unit,
    onOpenSettings: () -> Unit,
    onSpeakMessage: (String) -> Unit
) {
    val context = LocalContext.current
    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    var showMenu by remember { mutableStateOf(false) }

    // Scroll to bottom whenever messages or typing state changes
    LaunchedEffect(messages.size, isTyping) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable(onClick = onOpenSettings)
                            .padding(vertical = 4.dp, horizontal = 2.dp)
                    ) {
                        JaaniAvatar(
                            size = 40.dp,
                            showOnlineBadge = true,
                            isPulsing = isTyping
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "${settings.jaaniName} 💕",
                                color = WhatsAppTextPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (isTyping) "typing..." else "online",
                                color = if (isTyping) WhatsAppGreenLight else WhatsAppGreen,
                                fontSize = 12.sp
                            )
                        }
                    }
                },
                actions = {
                    // Big Green Call Button in Top Bar
                    IconButton(
                        onClick = onStartCall,
                        modifier = Modifier.testTag("btn_header_call")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Call,
                            contentDescription = "Voice Call",
                            tint = WhatsAppGreenLight
                        )
                    }

                    // Settings Button
                    IconButton(
                        onClick = onOpenSettings,
                        modifier = Modifier.testTag("btn_header_settings")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = WhatsAppTextPrimary
                        )
                    }

                    // Overflow Menu
                    Box {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "Menu",
                                tint = WhatsAppTextPrimary
                            )
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false },
                            modifier = Modifier.background(WhatsAppSurface)
                        ) {
                            DropdownMenuItem(
                                text = { Text("Settings & Persona", color = WhatsAppTextPrimary) },
                                onClick = {
                                    showMenu = false
                                    onOpenSettings()
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Voice Call", color = WhatsAppTextPrimary) },
                                onClick = {
                                    showMenu = false
                                    onStartCall()
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("About Jaani (AI Note)", color = WhatsAppTextPrimary) },
                                onClick = {
                                    showMenu = false
                                    Toast.makeText(
                                        context,
                                        "Jaani is an AI companion created for warm & caring conversation.",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = WhatsAppHeader
                )
            )
        },
        containerColor = WhatsAppDarkBackground
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Subtle AI Disclaimer Tag at top
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    color = Color(0x22128C7E),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "🔒 Jaani is an AI companion • Purely friendly conversation",
                        color = Color(0xFF80CBC4),
                        fontSize = 11.sp,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                    )
                }
            }

            // Messages LazyColumn
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                contentPadding = PaddingValues(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(messages, key = { it.id }) { message ->
                    ChatMessageBubble(
                        message = message,
                        onSpeak = { onSpeakMessage(message.text) },
                        onCopy = {
                            val clipboard =
                                context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("Jaani Chat", message.text))
                            Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
                        }
                    )
                }

                if (isTyping) {
                    item {
                        TypingIndicator(jaaniName = settings.jaaniName)
                    }
                }
            }

            // Timepass Bar (Jokes, Shayari, Truth or Dare, etc.)
            TimepassBar(
                onOptionSelected = { promptText ->
                    onSendMessage(promptText)
                }
            )

            // Input Bar
            ChatInputBar(
                text = inputText,
                onTextChange = { inputText = it },
                onSend = {
                    val trimmed = inputText.trim()
                    if (trimmed.isNotEmpty()) {
                        onSendMessage(trimmed)
                        inputText = ""
                    }
                },
                onMicClick = onStartCall
            )
        }
    }
}

@Composable
fun ChatMessageBubble(
    message: ChatMessageEntity,
    onSpeak: () -> Unit,
    onCopy: () -> Unit
) {
    val isUser = message.isFromUser
    val timeFormat = remember { SimpleDateFormat("hh:mm a", Locale.getDefault()) }
    val timeFormatted = timeFormat.format(Date(message.timestamp))

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .padding(horizontal = 4.dp),
            contentAlignment = if (isUser) Alignment.CenterEnd else Alignment.CenterStart
        ) {
            Surface(
                shape = RoundedCornerShape(
                    topStart = 16.dp,
                    topEnd = 16.dp,
                    bottomStart = if (isUser) 16.dp else 2.dp,
                    bottomEnd = if (isUser) 2.dp else 16.dp
                ),
                color = if (isUser) WhatsAppOutgoingBubble else WhatsAppIncomingBubble,
                modifier = Modifier
                    .clip(
                        RoundedCornerShape(
                            topStart = 16.dp,
                            topEnd = 16.dp,
                            bottomStart = if (isUser) 16.dp else 2.dp,
                            bottomEnd = if (isUser) 2.dp else 16.dp
                        )
                    )
                    .testTag(if (isUser) "user_message_bubble" else "jaani_message_bubble")
            ) {
                Column(
                    modifier = Modifier.padding(start = 12.dp, end = 12.dp, top = 8.dp, bottom = 6.dp)
                ) {
                    // Message Text
                    Text(
                        text = message.text,
                        color = WhatsAppTextPrimary,
                        fontSize = 15.sp,
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Bottom info: Speak icon + Copy icon + Timestamp + Double Ticks
                    Row(
                        modifier = Modifier.align(Alignment.End),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        // Speak button for Jaani's messages
                        if (!isUser) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                contentDescription = "Speak aloud",
                                tint = WhatsAppTextSecondary,
                                modifier = Modifier
                                    .size(14.dp)
                                    .clickable(onClick = onSpeak)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                        }

                        // Copy button
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copy message",
                            tint = WhatsAppTextSecondary,
                            modifier = Modifier
                                .size(13.dp)
                                .clickable(onClick = onCopy)
                        )

                        Spacer(modifier = Modifier.width(4.dp))

                        // Timestamp
                        Text(
                            text = timeFormatted,
                            color = WhatsAppTextSecondary,
                            fontSize = 11.sp
                        )

                        // WhatsApp Double Check Ticks for user messages
                        if (isUser) {
                            Icon(
                                imageVector = Icons.Default.DoneAll,
                                contentDescription = "Read",
                                tint = WhatsAppCheckBlue,
                                modifier = Modifier.size(15.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ChatInputBar(
    text: String,
    onTextChange: (String) -> Unit,
    onSend: () -> Unit,
    onMicClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(WhatsAppDarkBackground)
            .padding(horizontal = 8.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Rounded Input Field
        Surface(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(26.dp),
            color = WhatsAppInputBar
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.SentimentSatisfiedAlt,
                    contentDescription = "Emoji",
                    tint = WhatsAppTextSecondary,
                    modifier = Modifier.size(24.dp)
                )

                OutlinedTextField(
                    value = text,
                    onValueChange = onTextChange,
                    placeholder = {
                        Text(
                            text = "Message in Hinglish...",
                            color = WhatsAppTextSecondary,
                            fontSize = 15.sp
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = WhatsAppTextPrimary,
                        unfocusedTextColor = WhatsAppTextPrimary,
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("chat_input_field"),
                    maxLines = 4
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Action Button: Send Arrow if text present, otherwise Green Call/Mic Button
        FloatingActionButton(
            onClick = {
                if (text.isNotBlank()) {
                    onSend()
                } else {
                    onMicClick()
                }
            },
            containerColor = WhatsAppGreen,
            contentColor = Color.White,
            shape = CircleShape,
            modifier = Modifier
                .size(48.dp)
                .testTag("btn_chat_send")
        ) {
            Icon(
                imageVector = if (text.isNotBlank()) Icons.AutoMirrored.Filled.Send else Icons.Default.Mic,
                contentDescription = if (text.isNotBlank()) "Send" else "Voice Call",
                modifier = Modifier.size(22.dp)
            )
        }
    }
}
