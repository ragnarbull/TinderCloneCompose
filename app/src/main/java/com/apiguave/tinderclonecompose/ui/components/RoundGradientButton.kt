package com.apiguave.tinderclonecompose.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalContentAlpha
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import com.apiguave.tinderclonecompose.extensions.withLinearGradient

@Composable
fun RoundGradientButton(onClick: () -> Unit, enabled: Boolean = true,  imageVector: ImageVector, color1: Color, color2: Color){
    RoundGradientButton(onClick = onClick, enabled = enabled, painter = rememberVectorPainter(image = imageVector), color1 = color1, color2 = color2 )
}

@Composable
fun RoundGradientButton(
    onClick: () -> Unit,
    enabled: Boolean = true,
    painter: Painter,
    color1: Color,
    color2: Color
){
    val contentAlpha = if (enabled) LocalContentAlpha.current else ContentAlpha.disabled
    IconButton(onClick = onClick, enabled = enabled) {
        Icon(
            painter = painter,
            modifier = Modifier
                .border(
                    2.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            if(enabled) color1 else color1.copy(alpha = contentAlpha),
                            if(enabled) color2 else color2.copy(alpha = contentAlpha),
                        )
                    ), shape = CircleShape
                )
                .padding(12.dp)
                .size(32.dp)
                .withLinearGradient(color1, color2)

            , contentDescription = null
        )
    }
}

@Composable
fun AnimatedRoundGradientButton(
    onClick: () -> Unit,
    enabled: Boolean = true,
    imageVector: ImageVector,
    color1: Color,
    color2: Color,
    size: Float
){
    AnimatedRoundGradientButton(
        onClick = onClick,
        enabled = enabled,
        painter = rememberVectorPainter(image = imageVector),
        color1 = color1,
        color2 = color2,
        size = size
    )
}

@Composable
fun AnimatedRoundGradientButton(
    onClick: () -> Unit,
    enabled: Boolean = true,
    painter: Painter,
    color1: Color,
    color2: Color,
    size: Float = 1f
){
    val contentAlpha = if (enabled) LocalContentAlpha.current else ContentAlpha.disabled
    val scale by animateFloatAsState(
        targetValue = size, // Use the passed size
        label = ""
    )

    IconButton(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier
            .graphicsLayer(
                scaleX = scale,
                scaleY = scale
            ),
    ) {
        Icon(
            painter = painter,
            modifier = Modifier
                .border(
                    2.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            if(enabled) color1 else color1.copy(alpha = contentAlpha),
                            if(enabled) color2 else color2.copy(alpha = contentAlpha),
                        )
                    ), shape = CircleShape
                )
                .padding(12.dp)
                .size(32.dp)
                .withLinearGradient(color1, color2)
                .graphicsLayer(
                    scaleX = scale,
                    scaleY = scale
                ),
            contentDescription = null
        )
    }
}
