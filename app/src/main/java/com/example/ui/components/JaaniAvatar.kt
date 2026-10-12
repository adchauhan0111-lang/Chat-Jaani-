package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.WhatsAppGreenLight
import com.example.ui.theme.WhatsAppPink

@Composable
fun JaaniAvatar(
    modifier: Modifier = Modifier,
    size: Dp = 42.dp,
    showOnlineBadge: Boolean = true,
    isPulsing: Boolean = false
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse_avatar")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isPulsing) 1.25f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = if (isPulsing) 0.6f else 0f,
        targetValue = 0.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.size(size)
    ) {
        // Pulsing outer ripple when speaking in call
        if (isPulsing) {
            Canvas(
                modifier = Modifier
                    .size(size * 1.45f)
            ) {
                drawCircle(
                    color = WhatsAppPink.copy(alpha = pulseAlpha),
                    radius = (size.toPx() / 2f) * pulseScale,
                    center = center,
                    style = Stroke(width = 6f)
                )
                drawCircle(
                    color = WhatsAppGreenLight.copy(alpha = pulseAlpha * 0.7f),
                    radius = (size.toPx() / 2f) * (pulseScale * 1.15f),
                    center = center,
                    style = Stroke(width = 3f)
                )
            }
        }

        // Main Avatar Circle
        Box(
            modifier = Modifier
                .size(size)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFFF8DA1),
                            Color(0xFFFF4B72),
                            Color(0xFF6B1D4F)
                        )
                    )
                )
                .border(2.dp, Color(0x66FFFFFF), CircleShape)
        ) {
            Canvas(modifier = Modifier.size(size)) {
                val w = this.size.width
                val h = this.size.height

                // Soft background glow
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFFFFA4B6), Color(0xFFC2185B)),
                        center = Offset(w * 0.5f, h * 0.4f),
                        radius = w * 0.6f
                    ),
                    radius = w * 0.5f,
                    center = center
                )

                // Hair silhouette behind
                drawCircle(
                    color = Color(0xFF1F121E),
                    radius = w * 0.38f,
                    center = Offset(w * 0.5f, h * 0.46f)
                )

                // Face skin tone
                drawCircle(
                    color = Color(0xFFFFD1BA),
                    radius = w * 0.26f,
                    center = Offset(w * 0.5f, h * 0.44f)
                )

                // Cute cheeks blush
                drawCircle(
                    color = Color(0x66FF5277),
                    radius = w * 0.08f,
                    center = Offset(w * 0.35f, h * 0.47f)
                )
                drawCircle(
                    color = Color(0x66FF5277),
                    radius = w * 0.08f,
                    center = Offset(w * 0.65f, h * 0.47f)
                )

                // Sparkly eyes
                drawCircle(
                    color = Color(0xFF2C1E26),
                    radius = w * 0.045f,
                    center = Offset(w * 0.38f, h * 0.42f)
                )
                drawCircle(
                    color = Color.White,
                    radius = w * 0.016f,
                    center = Offset(w * 0.37f, h * 0.41f)
                )

                drawCircle(
                    color = Color(0xFF2C1E26),
                    radius = w * 0.045f,
                    center = Offset(w * 0.62f, h * 0.42f)
                )
                drawCircle(
                    color = Color.White,
                    radius = w * 0.016f,
                    center = Offset(w * 0.61f, h * 0.41f)
                )

                // Gentle smile
                val smilePath = Path().apply {
                    moveTo(w * 0.42f, h * 0.52f)
                    quadraticTo(w * 0.5f, h * 0.57f, w * 0.58f, h * 0.52f)
                }
                drawPath(
                    path = smilePath,
                    color = Color(0xFFC2185B),
                    style = Stroke(width = w * 0.035f)
                )

                // Hair bangs/fringe
                val bangsPath = Path().apply {
                    moveTo(w * 0.22f, h * 0.36f)
                    quadraticTo(w * 0.5f, h * 0.26f, w * 0.78f, h * 0.36f)
                    quadraticTo(w * 0.6f, h * 0.38f, w * 0.5f, h * 0.32f)
                    quadraticTo(w * 0.4f, h * 0.38f, w * 0.22f, h * 0.36f)
                }
                drawPath(bangsPath, color = Color(0xFF1F121E))

                // Cute hair flower clip
                drawCircle(
                    color = Color(0xFFFFD54F),
                    radius = w * 0.07f,
                    center = Offset(w * 0.72f, h * 0.28f)
                )
                drawCircle(
                    color = Color(0xFFFF4081),
                    radius = w * 0.035f,
                    center = Offset(w * 0.72f, h * 0.28f)
                )

                // Shoulders / dress neckline
                val dressPath = Path().apply {
                    moveTo(w * 0.15f, h)
                    quadraticTo(w * 0.5f, h * 0.72f, w * 0.85f, h)
                    close()
                }
                drawPath(
                    path = dressPath,
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFFFF2A6D), Color(0xFF880E4F))
                    )
                )
            }
        }

        // Online indicator badge
        if (showOnlineBadge && size < 60.dp) {
            Box(
                modifier = Modifier
                    .size(size * 0.28f)
                    .align(Alignment.BottomEnd)
                    .offset(x = 1.dp, y = 1.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF00E676))
                    .border(1.5.dp, Color(0xFF1F2C34), CircleShape)
            )
        }
    }
}
