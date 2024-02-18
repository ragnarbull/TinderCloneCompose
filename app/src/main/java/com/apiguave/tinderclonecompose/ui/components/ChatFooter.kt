package com.apiguave.tinderclonecompose.ui.components

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.apiguave.tinderclonecompose.BuildConfig
import com.apiguave.tinderclonecompose.R
import com.apiguave.tinderclonecompose.extensions.giphyvideoplayer.VideoCache
import com.apiguave.tinderclonecompose.extensions.giphyvideoplayer.VideoPlayerExoPlayerImpl
import com.apiguave.tinderclonecompose.ui.chat.TAG
import com.apiguave.tinderclonecompose.ui.theme.LightLightGray
import com.apiguave.tinderclonecompose.ui.theme.UltramarineBlue
import com.giphy.sdk.core.models.Media
import com.giphy.sdk.ui.GPHContentType
import com.giphy.sdk.ui.GPHSettings
import com.giphy.sdk.ui.Giphy
import com.giphy.sdk.ui.themes.GPHTheme
import com.giphy.sdk.ui.views.dialogview.GiphyDialogView
import com.giphy.sdk.ui.views.dialogview.setup
import timber.log.Timber
import kotlin.math.roundToInt

@SuppressLint("LogNotTimber")
@Composable
fun ChatFooter(
    onGifSelected: (Media) -> Unit,
    onSendClicked: (String) -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = CircleShape,
) {
    val GIPHY_ANDROID_SDK_KEY = BuildConfig.giphyApiKey
    Log.d(TAG, "GIPHY_ANDROID_SDK_KEY: $GIPHY_ANDROID_SDK_KEY")

    // TODO: fetch this securely from a server!!!

    var showView by rememberSaveable { mutableStateOf(false) }
    var offset by rememberSaveable { mutableFloatStateOf(0f) }
    val configuration = LocalConfiguration.current
    var contentType by rememberSaveable { mutableStateOf(GPHContentType.gif) }
    var inputValue by remember { mutableStateOf("") }

    Card(
        modifier = modifier,
        border = BorderStroke(1.dp, LightLightGray),
        shape = shape
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Giphy selector button
                Button(
                    onClick = {
                        offset = 0f
                        showView = true
                    },
                    modifier = Modifier
                        .weight(1.5f),
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color.Black
                    )
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.gph_logo_button),
                        contentDescription = "Giphy icon"
                    )
                }
                // Text field for input
                // TODO: make a custom component for the cursor to be white
                BasicTextField(
                    modifier = Modifier
                        .weight(2.5f)
                        .padding(horizontal = 20.dp),
                    textStyle = LocalTextStyle.current.copy(color = Color.White),
                    value = inputValue,
                    onValueChange = { inputValue = it },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                    keyboardActions = KeyboardActions {
                        onSendClicked(inputValue)
                        inputValue = ""
                    },
                )
                // Send button
                TextButton(
                    onClick = {
                        onSendClicked(inputValue)
                        inputValue = ""
                    },
                    modifier = Modifier.weight(1f),
                    enabled = inputValue.isNotBlank(),
                    colors = ButtonDefaults.textButtonColors(contentColor = UltramarineBlue)
                ) {
                    Text(stringResource(id = R.string.send))
                }
            }
        }
    }

    // Giphy dialog
    AnimatedVisibility(
        visible = showView,
        enter = slideInVertically(
            initialOffsetY = { it },
            animationSpec = tween(durationMillis = 500, easing = LinearOutSlowInEasing)
        ),
        exit = slideOutVertically(
            targetOffsetY = { it },
            animationSpec = tween(durationMillis = 500, easing = LinearOutSlowInEasing)
        ),
        modifier = Modifier.offset {
            IntOffset(0, offset.roundToInt())
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(LocalConfiguration.current.screenHeightDp.dp)
                .background(Color.Black.copy(alpha = 0.5f))
        ) {
            AndroidView(
                factory = { ctx ->
                    VideoCache.initialize(ctx, 100 * 1024 * 1024)
                    Giphy.configure(ctx, GIPHY_ANDROID_SDK_KEY, true)
                    val settings =
                        GPHSettings(theme = GPHTheme.Light, stickerColumnCount = 3)
                    GiphyDialogView(ctx).apply {
                        setup(
                            settings.copy(selectedContentType = contentType),
                            videoPlayer = { playerView, repeatable, showCaptions ->
                                VideoPlayerExoPlayerImpl(
                                    playerView,
                                    repeatable,
                                    showCaptions
                                )
                            }
                        )
                        this.listener = object : GiphyDialogView.Listener {
                            @SuppressLint("LogNotTimber")
                            override fun onGifSelected(
                                media: Media,
                                searchTerm: String?,
                                selectedContentType: GPHContentType
                            ) {
                                onGifSelected(media)
                                showView = false
                            }

                            override fun onClosed(selectedContentType: GPHContentType) {
                                contentType = selectedContentType
                            }

                            override fun didSearchTerm(term: String) {
                                Timber.d("didSearchTerm: $term")
                            }

                            override fun onFocusSearch() {
                                Timber.d("onFocusSearch")
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(LocalConfiguration.current.screenHeightDp.dp)
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(35.dp)
                    .background(Color.Transparent)
                    .pointerInput(Unit) {
                        detectVerticalDragGestures(
                            onVerticalDrag = { change, dragAmount ->
                                offset += change.positionChange().y
                                if (offset > configuration.screenHeightDp.dp.toPx() * 0.6 || dragAmount > 50) {
                                    showView = false
                                }
                            },
                            onDragEnd = {
                                if (offset <= configuration.screenHeightDp.dp.toPx() * 0.6) {
                                    offset = 0f
                                }
                            },
                            onDragCancel = {
                                if (offset <= configuration.screenHeightDp.dp.toPx() * 0.6) {
                                    offset = 0f
                                }
                            }
                        )
                    }
            )
        }
    }
}
