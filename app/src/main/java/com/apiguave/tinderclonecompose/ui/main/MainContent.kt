package com.apiguave.tinderclonecompose.ui.main

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.animation.*
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import org.koin.androidx.compose.getViewModel
import androidx.navigation.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.apiguave.tinderclonecompose.domain.discoverysettingscard.entity.CurrentDiscoverySettings
import com.apiguave.tinderclonecompose.domain.match.entity.Match
import com.apiguave.tinderclonecompose.ui.chat.ChatView
import com.apiguave.tinderclonecompose.ui.chat.ChatViewModel
import com.apiguave.tinderclonecompose.ui.components.AddPictureView
import com.apiguave.tinderclonecompose.ui.components.LoadingView
import com.apiguave.tinderclonecompose.ui.discoverysettings.DiscoverySettingsView
import com.apiguave.tinderclonecompose.ui.discoverysettings.DiscoverySettingsViewModel
import com.apiguave.tinderclonecompose.ui.editprofile.EditProfileView
import com.apiguave.tinderclonecompose.ui.editprofile.EditProfileViewModel
import com.apiguave.tinderclonecompose.ui.home.HomeView
import com.apiguave.tinderclonecompose.ui.home.HomeViewModel
import com.apiguave.tinderclonecompose.ui.login.LoginView
import com.apiguave.tinderclonecompose.ui.login.LoginViewModel
import com.apiguave.tinderclonecompose.ui.matchlist.MatchListView
import com.apiguave.tinderclonecompose.ui.matchlist.MatchListViewModel
import com.apiguave.tinderclonecompose.ui.newmatch.NewMatchView
import com.apiguave.tinderclonecompose.ui.newmatch.NewMatchViewModel
import com.apiguave.tinderclonecompose.ui.signup.SignUpView
import com.apiguave.tinderclonecompose.ui.signup.SignUpViewModel
import com.apiguave.tinderclonecompose.ui.theme.TinderCloneComposeTheme
import com.apiguave.tinderclonecompose.ui.viewuserprofile.ViewUserProfileView
import com.apiguave.tinderclonecompose.ui.viewuserprofile.ViewUserProfileViewModel
import com.google.accompanist.navigation.animation.composable
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import org.koin.androidx.compose.koinViewModel

@SuppressLint("LogNotTimber")
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun MainContent(signInClient: GoogleSignInClient){
    val TAG = "MainContent"

    TinderCloneComposeTheme {
        val navController = rememberNavController()

        val homeViewModel: HomeViewModel = getViewModel()
        val viewUserProfileViewModel: ViewUserProfileViewModel = getViewModel()
        val chatViewModel: ChatViewModel = getViewModel()
        val newMatchViewModel: NewMatchViewModel = getViewModel()
        val discoverySettingsViewModel: DiscoverySettingsViewModel = getViewModel()
        val editProfileViewModel: EditProfileViewModel = getViewModel()
        val signUpViewModel: SignUpViewModel = getViewModel()
        NavHost(navController = navController, startDestination = Routes.Login) {

            animatedComposable(Routes.Login) {
                val loginViewModel: LoginViewModel = koinViewModel()
                val uiState by loginViewModel.uiState.collectAsState()

                LoginView(
                    signInClient = signInClient,
                    uiState = uiState,
                    onNavigateToSignUp = {
                        navController.navigate(Routes.SignUp)
                    },
                    onNavigateToHome = {
                        navController.navigate(Routes.Home){
                            popUpTo(Routes.Login){
                                inclusive = true
                            }
                        }
                    },
                    onSignIn = loginViewModel::signIn

                )
            }

            animatedComposable(Routes.SignUp) {
                val uiState by signUpViewModel.uiState.collectAsState()
                SignUpView(
                    uiState = uiState,
                    signInClient = signInClient,
                    onAddPicture = {
                        navController.navigate(Routes.getAddPictureRoute(Routes.SignUp))
                    },
                    onNavigateToHome = {
                        navController.navigate(Routes.Home){
                            popUpTo(Routes.SignUp){
                                inclusive = true
                            }
                        }
                    },
                    removePictureAt = signUpViewModel::removePictureAt,
                    signUp = signUpViewModel::signUp
                )
            }

            animatedComposable(Routes.AddPicture, arguments = listOf(navArgument(Arguments.Caller){ type = NavType.StringType})){
                AddPictureView(
                    onCloseClicked = navController::popBackStack
                    ,
                    onReceiveUri = { uri, caller ->
                        if(caller == Routes.SignUp){
                            signUpViewModel.addPicture(uri)
                        }else if(caller == Routes.EditProfile){
                            editProfileViewModel.addPicture(uri)
                        }
                        navController.popBackStack()
                    },
                    caller = it.arguments?.getString(Arguments.Caller)
                )
            }

            animatedComposable(Routes.Home, animationType = AnimationType.HOME) {
                val viewModel: HomeViewModel = koinViewModel()
                val uiState by viewModel.uiState.collectAsState()

                HomeView(
                    uiState = uiState,
                    navigateToDiscoverySettings = {
                        navController.navigate(Routes.DiscoverySettings)
                    },
                    navigateToEditProfile = {
                        navController.navigate(Routes.EditProfile)
                    },
                    navigateToMatchList = {
                        navController.navigate(Routes.MatchList)
                    },
                    navigateToNewMatch = {
                        navController.navigate(Routes.NewMatch)
                    },
                    navigateToViewUserProfile = {
                        navController.navigate(Routes.ViewUserProfile)
                    },
                    removeLastProfile = viewModel::removeLastProfile,
                    getLastKnownLocationAndFetchProfiles = viewModel::getLastKnownLocationAndFetchProfiles,
                    swipeUserAction = viewModel::swipeUserAction,
                    setMatch = newMatchViewModel::setMatch,
                    setCurrentProfile = editProfileViewModel::setCurrentProfile,
                    newMatch = viewModel.newMatch,
                    currentProfile = viewModel.currentProfile,
                    undoSwipe = viewModel::undoSwipe,
                    setUserProfile = viewUserProfileViewModel::setUserProfile,
                    clearLastRemovedProfile = viewModel::clearLastRemovedProfile,
                    superLike = viewModel::superLike,
                    boost = viewModel::boost
                )
            }

            animatedComposable(Routes.ViewUserProfile){
                val userProfile by viewUserProfileViewModel.userProfile.collectAsState()

                userProfile?.let { it1 ->
                    ViewUserProfileView(
                        userProfile = it1,
                        onArrowBackPressed =  navController::popBackStack,
                    )
                }
            }

            animatedComposable(Routes.NewMatch, animationType = AnimationType.FADE){
                val newMatch by newMatchViewModel.match.collectAsState()
                NewMatchView(
                    match = newMatch,
                    onSendMessage = {
                        newMatchViewModel.sendMessage(it)
                        navController.popBackStack()
                    },
                    onGifSelected = {
                        newMatchViewModel.onGifSelected(it)
                        navController.popBackStack()
                    },
                    onCloseClicked = navController::popBackStack
                )
            }

            animatedComposable(Routes.EditProfile){
                val uiState by editProfileViewModel.uiState.collectAsState()
                EditProfileView(
                    uiState = uiState,
                    addPicture = {
                        navController.navigate(Routes.getAddPictureRoute(Routes.EditProfile))
                    },
                    onProfileEdited = navController::popBackStack,
                    onSignedOut = {
                        navController.navigate(Routes.Login){
                            popUpTo(Routes.Home){
                                inclusive = true
                            }
                        }
                    },
                    removePictureAt = editProfileViewModel::removePictureAt,
                    signOut = {editProfileViewModel.signOut(signInClient)},
                    updateProfile = editProfileViewModel::updateProfile,
                    action = editProfileViewModel.action
                )
            }

            animatedComposable(Routes.DiscoverySettings){
                val viewModel: DiscoverySettingsViewModel = koinViewModel()
                val uiState by viewModel.uiState.collectAsState()

                var discoverySettings by remember { mutableStateOf<CurrentDiscoverySettings?>(null) }

                LaunchedEffect(Unit) {
                    if (discoverySettings == null) {
                        discoverySettings = viewModel.getDiscoverySettings()
                        viewModel.setCurrentDiscoverySettings(discoverySettings!!)
                    }
                }

                discoverySettings?.let {
                    DiscoverySettingsView(
                        uiState = uiState,
                        navigateToHomeView = {
                            navController.navigate(Routes.Home)
                        },
                        updateDiscoverySettings = viewModel::updateDiscoverySettings,
                        action = viewModel.action
                    )
                } ?: run {
                    if (discoverySettings == null) LoadingView()
                    else {
                        Text(
                            modifier = Modifier.fillMaxSize(),
                            textAlign = TextAlign.Center,
                            text = "Invalid discovery settings"
                        )
                    }
                }
            }

            animatedComposable(Routes.MatchList){
                val viewModel: MatchListViewModel = koinViewModel()
                val uiState by viewModel.uiState.collectAsState()

                MatchListView(
                    uiState = uiState,
                    navigateToMatch = { match ->
                        val matchId = match.id
                        navController.navigate("${Routes.Chat}/$matchId")
                    },
                    onArrowBackPressed =  navController::popBackStack,
                    fetchMatches = viewModel::fetchMatches
                )
            }

            animatedComposable("${Routes.Chat}/{matchId}") { backStackEntry ->
                val viewModel: ChatViewModel = koinViewModel()
                val uiState by viewModel.uiState.collectAsState()
                val matchId = backStackEntry.arguments?.getString("matchId")
                var chatMatch by remember { mutableStateOf<Match?>(null) }

                LaunchedEffect(matchId) {
                    matchId?.let { id ->
                        chatMatch = viewModel.getMatchById(id)
                    }
                }

                chatMatch?.let { match ->
                    viewModel.setMatch(match)
                    val messages by viewModel.getMessages(match.id).collectAsState(initial = listOf())
                    Log.d(TAG, "messages: $messages")
                    ChatView(
                        uiState = uiState,
                        match = match,
                        messages = messages,
                        onArrowBackPressed = navController::popBackStack,
                        sendMessage = viewModel::sendMessage,
                        likeMessage = viewModel::likeMessage,
                        unLikeMessage = viewModel::unLikeMessage,
                        onGifSelected = viewModel::onGifSelected
                    )
                } ?: run {
                    if (matchId != null) LoadingView()
                    else {
                        Text(
                            modifier = Modifier.fillMaxSize(),
                            textAlign = TextAlign.Center,
                            text = "Invalid matchId"
                        )
                    }
                }
            }
        }
    }
}

@ExperimentalAnimationApi
fun NavGraphBuilder.animatedComposable(
    route: String,
    animationType: AnimationType = AnimationType.SLIDE,
    arguments: List<NamedNavArgument> = emptyList(),
    deepLinks: List<NavDeepLink> = emptyList(),
    content: @Composable AnimatedVisibilityScope.(NavBackStackEntry) -> Unit
) {
    composable(route, arguments, deepLinks,
        enterTransition = animationType.enterTransition,
        exitTransition = animationType.exitTransition,
        popEnterTransition = animationType.popEnterTransition,
        popExitTransition = animationType.popExitTransition,
        content = content
    )
}
