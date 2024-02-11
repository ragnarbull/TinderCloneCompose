package com.apiguave.tinderclonecompose.ui.di

import com.apiguave.tinderclonecompose.ui.chat.ChatViewModel
import com.apiguave.tinderclonecompose.ui.discoverysettings.DiscoverySettingsViewModel
import com.apiguave.tinderclonecompose.ui.editprofile.EditProfileViewModel
import com.apiguave.tinderclonecompose.ui.home.HomeViewModel
import com.apiguave.tinderclonecompose.ui.login.LoginViewModel
import com.apiguave.tinderclonecompose.ui.matchlist.MatchListViewModel
import com.apiguave.tinderclonecompose.ui.newmatch.NewMatchViewModel
import com.apiguave.tinderclonecompose.ui.signup.SignUpViewModel
import com.apiguave.tinderclonecompose.ui.viewuserprofile.ViewUserProfileViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val presentationModule = module {

    //View models
    viewModel { ChatViewModel(get()) }
    viewModel { ViewUserProfileViewModel() }
    viewModel { NewMatchViewModel(get()) }
    viewModel { EditProfileViewModel(get(), get()) }
    viewModel { SignUpViewModel(get(), get()) }
    viewModel { LoginViewModel(get()) }
    viewModel { HomeViewModel(androidApplication(), get(), get()) }
    viewModel { MatchListViewModel(get()) }
    viewModel { DiscoverySettingsViewModel(get()) }
}