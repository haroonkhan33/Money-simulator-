package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.sp
import com.example.model.FloatingText

@Composable
fun FloatingTextOverlay(
    floatingTexts: List<FloatingText>,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        floatingTexts.forEach { item ->
            SingleFloatingText(item = item)
        }
    }
}

@Composable
private fun SingleFloatingText(item: FloatingText) {
    val offsetY = remember { Animatable(0f) }
    val alpha = remember { Animatable(1f) }

    LaunchedEffect(item.id) {
        offsetY.animateTo(
            targetValue = -90f,
            animationSpec = tween(durationMillis = 700)
        )
    }

    LaunchedEffect(item.id) {
        alpha.animateTo(
            targetValue = 0f,
            animationSpec = tween(durationMillis = 700)
        )
    }

    Text(
        text = item.text,
        color = Color(0xFF00FF88),
        style = TextStyle(
            fontSize = 18.sp,
            fontWeight = FontWeight.ExtraBold,
            shadow = Shadow(
                color = Color(0x8000FF88),
                blurRadius = 8f
            )
        ),
        modifier = Modifier
            .offset {
                IntOffset(
                    x = item.x.toInt() - 40,
                    y = (item.y + offsetY.value).toInt()
                )
            }
            .alpha(alpha.value)
    )
}
