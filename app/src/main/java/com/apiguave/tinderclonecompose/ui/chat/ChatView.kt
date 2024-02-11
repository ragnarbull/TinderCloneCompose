package com.apiguave.tinderclonecompose.ui.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
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
import coil.compose.AsyncImage
import com.apiguave.tinderclonecompose.R
import com.apiguave.tinderclonecompose.domain.match.entity.Match
import com.apiguave.tinderclonecompose.domain.message.entity.Message
import com.apiguave.tinderclonecompose.extensions.withLinearGradient
import com.apiguave.tinderclonecompose.ui.components.ChatFooter
import com.apiguave.tinderclonecompose.ui.components.LoadingView
import com.apiguave.tinderclonecompose.ui.theme.AntiFlashWhite
import com.apiguave.tinderclonecompose.ui.theme.Orange
import com.apiguave.tinderclonecompose.ui.theme.Pink
import com.apiguave.tinderclonecompose.ui.theme.UltramarineBlue

@Composable
fun ChatView(
    uiState: ChatViewUiState,
    match: Match,
    messages: List<Message>,
    onArrowBackPressed: () -> Unit,
    sendMessage: (String) -> Unit,
    likeMessage: (String) -> Unit,
    unLikeMessage: (String) -> Unit
) {

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            ChatAppBar(match = match, onArrowBackPressed = onArrowBackPressed)
        },
        bottomBar = { ChatFooter(
            onSendClicked = sendMessage
        ) }
    ) { padding ->
        LazyColumn(
            Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
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
            items(messages.size){ index ->
                MessageItem(
                    match = match,
                    message = messages[index],
                    onLikeClicked = { likeMessage(messages[index].id) },
                    onUnLikeClicked = { unLikeMessage(messages[index].id) }
                )
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
            Text(
                modifier = Modifier
                    .background(
                        AntiFlashWhite,
                        RoundedCornerShape(4.dp)
                    )
                    .padding(6.dp)
                    .weight(4f, false)
                ,
                text = message.text,
                color = Color.Black,
            )
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
            Text(
                modifier = Modifier
                    .background(
                        UltramarineBlue,
                        RoundedCornerShape(4.dp)
                    )
                    .padding(6.dp)
                    .weight(4f, false)
                ,
                text = message.text,
                color = Color.White,
            )
        }
    }
}
