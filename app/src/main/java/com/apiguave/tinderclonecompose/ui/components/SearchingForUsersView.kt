package com.apiguave.tinderclonecompose.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.animation.core.RepeatMode.Restart
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.IntSize

@Composable
@Preview
fun SearchingForUsersView() {
    Column(
        Modifier
            .fillMaxSize()
            .background(Color.White.copy(alpha = .6f))
            .clickable(enabled = false, onClick = {}),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SearchingForUsersAnimation()
    }
}

@Composable
@PreviewParameter(PulsarBuilderPreviewParameterProvider::class)
fun SearchingForUsersAnimation(){
    MultiplePulsarEffect()
}

@Composable
@PreviewParameter(PulsarBuilderPreviewParameterProvider::class)
fun MultiplePulsarEffect(
    nbPulsar: Int = 3,
    pulsarRadius: Float = 100f,
    pulsarColor: Color = MaterialTheme.colors.primary,
    fab: @Composable (Modifier) -> Unit = {},
) {
    var fabSize by remember { mutableStateOf(IntSize(0, 0)) }

    val effects: List<Pair<Float, Float>> = List(nbPulsar) {
        pulsarBuilder(pulsarRadius = pulsarRadius, size = fabSize.width, delay = it * 500)
    }

    Box(
        Modifier,
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier.size((pulsarRadius * 2).dp),
            onDraw = {
                for (i in 0 until nbPulsar) {
                    val (radius, alpha) = effects[i]
                    drawCircle(color = pulsarColor, radius = radius, alpha = alpha)
                }
            }
        )
        fab(
            Modifier
                .padding((pulsarRadius * 2).dp)
                .onGloballyPositioned {
                    if (it.isAttached) {
                        fabSize = it.size
                    }
                }
        )
    }
}

class PulsarBuilderPreviewParameterProvider : PreviewParameterProvider<Int> {
    override val values: Sequence<Int> = sequenceOf(1, 2, 3)
    override val count: Int = values.count()
}


@Composable
@PreviewParameter(PulsarBuilderPreviewParameterProvider::class)
fun pulsarBuilder(pulsarRadius: Float, size: Int, delay: Int): Pair<Float, Float> {
    val infiniteTransition = rememberInfiniteTransition(label = "infiniteTransition")

    val radius by infiniteTransition.animateFloat(
        initialValue = (size / 2).toFloat(),
        targetValue = size + (pulsarRadius * 2),
        animationSpec = InfiniteRepeatableSpec(
            animation = tween(3000),
            initialStartOffset = StartOffset(delay),
            repeatMode = Restart
        ), label = "radius"
    )
    val alpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = InfiniteRepeatableSpec(
            animation = tween(3000),
            initialStartOffset = StartOffset(delay + 100),
            repeatMode = Restart
        ), label = "alpha"
    )

    return radius to alpha
}
