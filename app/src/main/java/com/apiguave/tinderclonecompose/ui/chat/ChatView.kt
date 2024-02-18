package com.apiguave.tinderclonecompose.ui.chat

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.AsyncImage
import com.apiguave.tinderclonecompose.BuildConfig
import com.apiguave.tinderclonecompose.R
import com.apiguave.tinderclonecompose.domain.match.entity.Match
import com.apiguave.tinderclonecompose.domain.message.entity.Message
import com.apiguave.tinderclonecompose.extensions.giphyvideoplayer.VideoCache
import com.apiguave.tinderclonecompose.extensions.withLinearGradient
import com.apiguave.tinderclonecompose.ui.components.ChatFooter
import com.apiguave.tinderclonecompose.ui.components.LoadingView
import com.apiguave.tinderclonecompose.ui.theme.AntiFlashWhite
import com.apiguave.tinderclonecompose.ui.theme.Orange
import com.apiguave.tinderclonecompose.ui.theme.Pink
import com.apiguave.tinderclonecompose.ui.theme.UltramarineBlue
import com.giphy.sdk.core.GPHCore
import com.giphy.sdk.core.models.Media
import com.giphy.sdk.core.models.enums.RenditionType
import com.giphy.sdk.ui.Giphy
import com.giphy.sdk.ui.views.GPHMediaView

const val TAG = "ChatView"

@Composable
fun ChatView(
    uiState: ChatViewUiState,
    match: Match,
    messages: List<Message>,
    onArrowBackPressed: () -> Unit,
    sendMessage: (String) -> Unit,
    likeMessage: (String) -> Unit,
    unLikeMessage: (String) -> Unit,
    onGifSelected: (Media) -> Unit
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            ChatAppBar(match = match, onArrowBackPressed = onArrowBackPressed)
        },
        bottomBar = {
            ChatFooter(
                onGifSelected = onGifSelected,
                onSendClicked = sendMessage
            )
        }
    ) { padding ->
        LazyColumn(
            Modifier
                .fillMaxSize()
                .padding(padding),
            reverseLayout = true // display most recent messages first
        ) {
            items(messages.size){ index ->
                // Reverse the index calculation
                val reversedIndex = messages.size - 1 - index
                MessageItem(
                    match = match,
                    message = messages[reversedIndex], // Use reversed index
                    onLikeClicked = { likeMessage(messages[index].id) },
                    onUnLikeClicked = { unLikeMessage(messages[index].id) }
                )
            }
            item {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    textAlign = TextAlign.Center,
                    color = Color.Gray,
                    fontSize = 12.sp,
                    text = stringResource(id = R.string.you_matched_with_on, match.userName, match.formattedDate).uppercase())
            }
        }
    }

    if(uiState.isLoading){
        LoadingView()
    }
}

@Composable
fun ChatAppBar(match: Match, onArrowBackPressed: () -> Unit){
    Surface(elevation = AppBarDefaults.TopAppBarElevation){
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically){
            Box(Modifier.weight(1f)){
                IconButton(modifier = Modifier.height(IntrinsicSize.Max),onClick = onArrowBackPressed) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_chevron_left_24),
                        contentDescription = null,
                        modifier = Modifier
                            .size(40.dp)
                            .withLinearGradient(Pink, Orange)
                            .align(Alignment.TopCenter)
                    )
                }
            }

            Column(Modifier.padding(vertical = 8.dp), horizontalAlignment = Alignment.CenterHorizontally){
                AsyncImage(
                    model = match.userPicture,
                    contentScale = ContentScale.Crop,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .size(40.dp)
                        .clip(CircleShape)
                )
                Text(text = match.userName, fontSize = 13.sp,fontWeight = FontWeight.Light, color = Color.Gray,textAlign = TextAlign.Center)
            }
            Spacer(Modifier.weight(1f))
        }
    }
}

@Composable
fun MessageItem(
    match: Match,
    message: Message,
    onLikeClicked: () -> Unit,
    onUnLikeClicked: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalArrangement = if(message.isFromSender) Arrangement.End else Arrangement.Start
    ) {

        if (!message.isFromSender) {
            AsyncImage(
                model = match.userPicture,
                contentScale = ContentScale.Crop,
                contentDescription = null,
                modifier = Modifier
                    .padding(end = 8.dp)
                    .size(40.dp)
                    .clip(CircleShape)
            )
            message.text?.let {
                Text(
                    modifier = Modifier
                        .background(
                            AntiFlashWhite,
                            RoundedCornerShape(4.dp)
                        )
                        .padding(6.dp)
                        .weight(4f, false),
                    text = it,
                    color = Color.Black,
                )
            }
            message.giphyMediaId?.let { mediaId ->
                GiphyItem(mediaId)
            }
            Spacer(modifier = Modifier.weight(1f))
            if (!message.liked) {
                // Display the like button only for the recipient's messages
                // Ensure a message can only be liked if it is not already liked
                IconButton(
                    onClick = onLikeClicked,
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.outline_heart_broken_24),
                        contentDescription = null,
                        tint = Color.White
                    )
                }
            } else {
                // Display the unlike button only for the recipient's messages
                // Ensure a message can only be unliked if it is not already unliked
                IconButton(
                    onClick = onUnLikeClicked,
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_heart_broken_24),
                        contentDescription = null,
                        tint = Color.Red
                    )
                }
            }
        } else {
            if (message.liked) {
                // Show the icon at the far left if the user has liked the sender's message
                Icon(
                    modifier = Modifier.padding(end = 8.dp),
                    painter = painterResource(id = R.drawable.baseline_heart_broken_24),
                    contentDescription = null,
                    tint = Color.Red
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            message.text?.let {
                Text(
                    modifier = Modifier
                        .background(
                            UltramarineBlue,
                            RoundedCornerShape(4.dp)
                        )
                        .padding(6.dp)
                        .weight(4f, false),
                    text = it,
                    color = Color.White,
                )
            }
            message.giphyMediaId?.let { mediaId ->
                GiphyItem(mediaId = mediaId)
            }
        }
    }
}

@SuppressLint("LogNotTimber")
@Composable
fun GiphyItem(mediaId: String) {
    val GIPHY_ANDROID_SDK_KEY = BuildConfig.giphyApiKey
    Log.d(TAG, "GIPHY_ANDROID_SDK_KEY: $GIPHY_ANDROID_SDK_KEY")
    // TODO: fetch this securely from a server!!!

    // Create a state to track if the GIF is loading or if an error occurred
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf(false) }

    AndroidView(
        factory = { ctx ->
            VideoCache.initialize(ctx, 100 * 1024 * 1024)
            Giphy.configure(ctx, GIPHY_ANDROID_SDK_KEY, true)
            GPHMediaView(ctx).apply {
                GPHCore.gifById(mediaId) { result, e ->
                    if (e != null || result?.data == null) {
                        Log.e("GiphyItem", "Error loading GIF: ${e?.message}")
                        error = true
                        isLoading = false
                    } else {
                        setMedia(result.data, RenditionType.fixedWidth)
                        isLoading = false
                    }
                }
            }
        },
    )
    // Display loading indicator or error message based on the state
    if (isLoading) {
        // Display a loading indicator
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
        }
    } else if (error) {
        // Display an error message
        Text(
            text = "Error loading GIF",
            color = Color.Red,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
