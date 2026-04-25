package com.example.rickandmortyapp.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.example.rickandmortyapp.ui.theme.PrimaryLavender
import com.example.rickandmortyapp.ui.theme.PrimaryPurple

@Composable
fun AnimatedGradientText(
    text: String,
    fontSize: TextUnit = 30.sp,
    fontWeight: FontWeight = FontWeight.SemiBold
) {

    val infiniteTransition = rememberInfiniteTransition(label = "gradient_animation")

    val animatedOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 3500,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "gradient_offset"
    )

    Text(
        text = text,

        fontSize = fontSize,

        fontWeight = fontWeight,

        style = TextStyle(

            brush = Brush.linearGradient(

                colors = listOf(
                    PrimaryPurple,
                    PrimaryLavender
                ),

                start = Offset(animatedOffset, 0f),

                end = Offset(animatedOffset + 400f, 0f)
            )
        ),

        color = MaterialTheme.colorScheme.onBackground
    )
}