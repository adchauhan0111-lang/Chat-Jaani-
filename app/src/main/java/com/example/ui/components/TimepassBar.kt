package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.EmojiEmotions
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.SentimentDissatisfied
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.WhatsAppDarkBackground
import com.example.ui.theme.WhatsAppGreen
import com.example.ui.theme.WhatsAppSurface
import com.example.ui.theme.WhatsAppTextPrimary
import com.example.ui.theme.WhatsAppTextSecondary

data class TimepassOption(
    val id: String,
    val title: String,
    val promptText: String,
    val icon: ImageVector,
    val color: Color
)

val defaultTimepassOptions = listOf(
    TimepassOption(
        id = "jokes",
        title = "Jokes 😂",
        promptText = "Ek mast mazedaar joke sunao na!",
        icon = Icons.Default.EmojiEmotions,
        color = Color(0xFFFFB74D)
    ),
    TimepassOption(
        id = "shayari",
        title = "Shayari ✍️",
        promptText = "Ek romantic aur cute si shayari sunao mere liye!",
        icon = Icons.Default.Favorite,
        color = Color(0xFFFF4081)
    ),
    TimepassOption(
        id = "truth_dare",
        title = "Truth / Dare 🎯",
        promptText = "Chalo Truth or Dare khelte hain! Tum choose karo mere liye.",
        icon = Icons.Default.Casino,
        color = Color(0xFFAB47BC)
    ),
    TimepassOption(
        id = "antakshari",
        title = "Antakshari 🎵",
        promptText = "Antakshari khelte hain! 'Baithe baithe kya karein...' pehla gaana tum gao!",
        icon = Icons.Default.MusicNote,
        color = Color(0xFF42A5F5)
    ),
    TimepassOption(
        id = "riddles",
        title = "Paheli 🧩",
        promptText = "Ek dimaag ghumane wali mast paheli poochho, dekhte hain main answer kar pata hu ya nahi!",
        icon = Icons.Default.Extension,
        color = Color(0xFF26A69A)
    ),
    TimepassOption(
        id = "bored",
        title = "Bore ho raha hu 😴",
        promptText = "Main bohot bore ho raha hu yaar, kuch entertaining aur mazedaar karte hain na!",
        icon = Icons.Default.SentimentDissatisfied,
        color = Color(0xFFEF5350)
    )
)

@Composable
fun TimepassBar(
    modifier: Modifier = Modifier,
    onOptionSelected: (String) -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(WhatsAppDarkBackground)
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 12.dp, vertical = 2.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            defaultTimepassOptions.forEach { option ->
                TimepassChip(
                    option = option,
                    onClick = { onOptionSelected(option.promptText) }
                )
            }
        }
    }
}

@Composable
fun TimepassChip(
    option: TimepassOption,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = WhatsAppSurface,
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .testTag("timepass_${option.id}")
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Icon(
                imageVector = option.icon,
                contentDescription = option.title,
                tint = option.color,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = option.title,
                color = WhatsAppTextPrimary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
