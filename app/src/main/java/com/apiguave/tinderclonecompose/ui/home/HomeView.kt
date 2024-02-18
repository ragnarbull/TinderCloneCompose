package com.apiguave.tinderclonecompose.ui.home

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.apiguave.tinderclonecompose.R
import com.apiguave.tinderclonecompose.domain.profilecard.entity.CurrentProfile
import com.apiguave.tinderclonecompose.domain.profilecard.entity.NewMatch
import com.apiguave.tinderclonecompose.domain.profilecard.entity.Profile
import com.apiguave.tinderclonecompose.ui.components.*
import com.apiguave.tinderclonecompose.ui.theme.Green1
import com.apiguave.tinderclonecompose.ui.theme.Green2
import com.apiguave.tinderclonecompose.ui.theme.Orange
import com.apiguave.tinderclonecompose.ui.theme.Pink
import com.apiguave.tinderclonecompose.ui.theme.Purple
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.SharedFlow
import kotlin.reflect.KFunction2
import kotlin.reflect.KSuspendFunction1

@Composable
fun HomeView(
    uiState: HomeUiState,
    navigateToDiscoverySettings: () -> Unit,
    navigateToEditProfile: () -> Unit,
    navigateToMatchList: () -> Unit,
    navigateToNewMatch: () -> Unit,
    navigateToViewUserProfile: () -> Unit,
    removeLastProfile: () -> Unit,
    getLastKnownLocationAndFetchProfiles: () -> Unit,
    swipeUserAction: KFunction2<Profile, Int, Unit>,
    newMatch: SharedFlow<NewMatch>,
    currentProfile: SharedFlow<CurrentProfile>,
    setMatch: (NewMatch) -> Unit,
    setCurrentProfile: (CurrentProfile) -> Unit,
    undoSwipe: KSuspendFunction1<Int, Unit>,
    setUserProfile: (Profile) -> Unit,
    clearLastRemovedProfile: () -> Unit,
    superLike: () -> Unit,
    boost: () -> Unit
    ) {

    val TAG = "HomeView"
    val btnInitialSize = 1f
    val btnClickedSize = 1.4f
    val btnDelayMs = 500L
    var lastSwipeDirection by remember { mutableIntStateOf(0) }
    var undoSwipeBtnClicked by remember { mutableStateOf(false) }
    var swipeLeftBtnClicked by remember { mutableStateOf(false) }
    var superLikeBtnClicked by remember { mutableStateOf(false) }
    var swipeRightBtnClicked by remember { mutableStateOf(false) }
    var boostBtnClicked by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(key1 = Unit, block = {
        newMatch.collect {
            clearLastRemovedProfile() // ensure the user can't undo the swipe
            setMatch(it)
            navigateToNewMatch()
        }
    })

    LaunchedEffect(key1 = Unit, block = {
        currentProfile.collect{
            setCurrentProfile(it)
        }
    })

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                TopBarIcon(resId = R.drawable.tinder_logo, modifier = Modifier.size(32.dp))
                Spacer(Modifier.weight(1f))
                TopBarIcon(
                    resId = R.drawable.baseline_filter_list_24,
                    onClick = navigateToDiscoverySettings
                )
            }
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                BottomBarIcon(
                    imageVector = Icons.Filled.AccountCircle,
                    onClick = navigateToEditProfile
                )
                Spacer(Modifier.weight(1f))
                BottomBarIcon(resId = R.drawable.tinder_logo, modifier = Modifier.size(32.dp))
                Spacer(Modifier.weight(1f))
                BottomBarIcon(
                    resId = R.drawable.ic_baseline_message_24,
                    onClick = navigateToMatchList
                )
            }
        },
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when(uiState){
                is HomeUiState.Error-> {
                    Spacer(Modifier.weight(1f))
                    Text(modifier = Modifier.padding(horizontal = 8.dp),
                        text = uiState.message ?: "", color = Color.Gray, fontSize = 16.sp, textAlign = TextAlign.Center)
                    Spacer(Modifier.height(12.dp))
                    GradientButton(onClick = {
                        scope.launch {
                            delay(200)
                            getLastKnownLocationAndFetchProfiles()
                        }
                    }) {
                        Text(stringResource(id = R.string.retry))
                    }
                    Spacer(Modifier.weight(1f))
                }
                HomeUiState.Loading -> {
                    Spacer(Modifier.weight(1f))
                    AnimatedGradientLogo(Modifier.fillMaxWidth(.4f))
                    Spacer(Modifier.weight(1f))
                }
                is HomeUiState.Searching -> {
                    Spacer(Modifier.weight(1f))
                    SearchingForUsersAnimation()
                    Spacer(Modifier.weight(1f))
                }
                is HomeUiState.Success -> {
                    Spacer(Modifier.weight(1f))
                    Box(Modifier.padding(horizontal = 12.dp)) {
                        Text(
                            text = stringResource(id = R.string.no_more_profiles),
                            color = Color.Gray,
                            fontSize = 20.sp
                        )
                        val localDensity = LocalDensity.current
                        var buttonRowHeightDp by remember { mutableStateOf(0.dp) }

                        val swipeStates = uiState.profileList.map { rememberSwipeableCardState() }
                        uiState.profileList.forEachIndexed { index, profile ->
                            ProfileCardView(profile,
                                modifier = Modifier.swipableCard(
                                    state = swipeStates[index],
                                    onSwiped = { direction ->
                                        lastSwipeDirection = when (direction) {
                                            SwipingDirection.Left -> {
                                                scope.launch {
                                                    swipeLeftBtnClicked = true
                                                    delay(btnDelayMs)
                                                    swipeLeftBtnClicked = false
                                                }
                                                1
                                            } // Swiped left
                                            SwipingDirection.Up -> {
                                                scope.launch {
                                                    superLikeBtnClicked = true
                                                    delay(btnDelayMs)
                                                    superLikeBtnClicked = false
                                                }
                                                2
                                            } // Super like
                                            SwipingDirection.Right -> {
                                                scope.launch {
                                                    swipeRightBtnClicked = true
                                                    delay(btnDelayMs)
                                                    swipeRightBtnClicked = false
                                                }
                                                3
                                            } // Swiped right
                                            else -> 0 // Default to 0 if direction is not recognized
                                        }
                                        swipeUserAction(profile, lastSwipeDirection)
                                        removeLastProfile()
                                    }
                                ),
                                contentModifier = Modifier.padding(bottom = buttonRowHeightDp.plus(8.dp))
                            ) {
                                setUserProfile(profile)
                                navigateToViewUserProfile()
                            }
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.BottomCenter)
                                .padding(vertical = 10.dp)
                                .onGloballyPositioned { coordinates ->
                                    buttonRowHeightDp =
                                        with(localDensity) { coordinates.size.height.toDp() }
                                },
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Spacer(Modifier.weight(.5f))
                            // Undo swipe
                            AnimatedRoundGradientButton(
                                onClick = {
                                    scope.launch {
                                        undoSwipeBtnClicked = true
                                        undoSwipe(lastSwipeDirection)
                                        lastSwipeDirection = 0 // reset to 0
                                        delay(btnDelayMs)
                                        undoSwipeBtnClicked = false
                                    }
                                },
                                enabled = swipeStates.isNotEmpty(),
                                imageVector = Icons.Filled.KeyboardArrowLeft,
                                color1 = Color.Yellow,
                                color2 = Color.Yellow,
                                size = if (!undoSwipeBtnClicked) btnInitialSize else btnClickedSize
                            )
                            Spacer(Modifier.weight(.5f))
                            // Swipe left
                            AnimatedRoundGradientButton(
                                onClick = {
                                    scope.launch {
                                        swipeLeftBtnClicked = true
                                        swipeStates.last().swipe(SwipingDirection.Left)
                                        val profile = uiState.profileList.last()
                                        lastSwipeDirection = 1
                                        swipeUserAction(profile, 1)
                                        removeLastProfile()
                                        delay(btnDelayMs)
                                        swipeLeftBtnClicked = false
                                    }
                                },
                                enabled = swipeStates.isNotEmpty(),
                                imageVector = Icons.Filled.Close,
                                color1 = Pink,
                                color2 = Orange,
                                size = if (!swipeLeftBtnClicked) btnInitialSize else btnClickedSize
                            )
                            Spacer(Modifier.weight(.5f))
                            // Super like
                            AnimatedRoundGradientButton(
                                onClick = {
                                    scope.launch {
                                        superLikeBtnClicked = true
                                        swipeStates.last().swipe(SwipingDirection.Up)
                                        val profile = uiState.profileList.last()
                                        lastSwipeDirection = 2
                                        swipeUserAction(profile, 2)
                                        removeLastProfile()
                                        superLike()
                                        delay(btnDelayMs)
                                        superLikeBtnClicked = false
                                    }
                                },
                                enabled = swipeStates.isNotEmpty(),
                                imageVector = Icons.Filled.Star,
                                color1 = Color.Blue,
                                color2 = Color.Blue,
                                size = if (!superLikeBtnClicked) btnInitialSize else btnClickedSize
                            )
                            Spacer(Modifier.weight(.5f))
                            // Swipe right
                            AnimatedRoundGradientButton(
                                onClick = {
                                    scope.launch {
                                        swipeRightBtnClicked = true
                                        swipeStates.last().swipe(SwipingDirection.Right)
                                        val profile = uiState.profileList.last()
                                        lastSwipeDirection = 3
                                        swipeUserAction(profile, 3)
                                        removeLastProfile()
                                        delay(btnDelayMs)
                                        swipeRightBtnClicked = false
                                    }
                                },
                                enabled = swipeStates.isNotEmpty(),
                                imageVector = Icons.Filled.Favorite,
                                color1 = Green1,
                                color2 = Green2,
                                size = if (!swipeRightBtnClicked) btnInitialSize else btnClickedSize
                            )
                            Spacer(Modifier.weight(.5f))
                            // Boost
                            AnimatedRoundGradientButton(
                                onClick = {
                                    scope.launch {
                                        boostBtnClicked = true
                                        boost()
                                        delay(btnDelayMs)
                                        boostBtnClicked = false
                                    }
                                },
                                enabled = swipeStates.isNotEmpty(),
                                imageVector = Icons.Filled.Refresh,
                                color1 = Purple,
                                color2 = Purple,
                                size = if (!boostBtnClicked) btnInitialSize else btnClickedSize
                            )
                            Spacer(Modifier.weight(.5f))
                        }
                    }
                    Spacer(Modifier.weight(1f))
                }
            }
        }
    }
}
