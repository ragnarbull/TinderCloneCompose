package com.apiguave.tinderclonecompose.ui.editprofile

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.apiguave.tinderclonecompose.R
import com.apiguave.tinderclonecompose.domain.profilecard.entity.CurrentProfile
import com.apiguave.tinderclonecompose.domain.profile.entity.UserPicture
import com.apiguave.tinderclonecompose.ui.components.*
import kotlinx.coroutines.flow.SharedFlow

@SuppressLint("MutableCollectionMutableState")
@Composable
fun EditProfileView(
    uiState: EditProfileUiState,
    signOut: () -> Unit,
    addPicture: () -> Unit,
    onSignedOut: () -> Unit,
    onProfileEdited: () -> Unit,
    removePictureAt: (Int) -> Unit,
    updateProfile:
        (   currentProfile: CurrentProfile,
            bio: String,
            genderIndex: Int,
            orientationIndex: Int,
            height: String,
            jobTitle: String,
            languages: String,
            zodiacSign: String,
            education: String,
            interests: String,
            pictures: List<UserPicture>
        ) -> Unit,
    action: SharedFlow<EditProfileAction>,
) {
    val TAG = "EditProfileView"
    var showErrorDialog by remember { mutableStateOf(false) }
    var showDeleteConfirmationDialog by remember { mutableStateOf(false) }
    var deleteConfirmationPictureIndex by remember { mutableIntStateOf(-1) }

    // Bio control
    var bioText by rememberSaveable(stateSaver = TextFieldValue.Saver) { mutableStateOf(TextFieldValue(uiState.currentProfile.bio)) }

    var selectedGenderIndex by rememberSaveable { mutableIntStateOf(uiState.currentProfile.genderIndex) }
    var selectedOrientationIndex by rememberSaveable { mutableIntStateOf(uiState.currentProfile.orientationIndex) }

    // Height control
    var heightText by rememberSaveable(stateSaver = TextFieldValue.Saver) { mutableStateOf(TextFieldValue(uiState.currentProfile.height)) }
    val savedHeightInt = heightText.text.toIntOrNull() // Convert to Int or null if it's not a valid number
    val savedHeightInFeet = (savedHeightInt?.div(100) ?: 0)
    val savedHeightInInches = (savedHeightInt?.rem(100) ?: 0)
    var displayedHeightStr = ""
    if (savedHeightInFeet != 0) {
        displayedHeightStr = "${savedHeightInFeet}ft ${savedHeightInInches}in"
    }
    var showHeightDialog by remember { mutableStateOf(false) }

    // Job title control
    var jobTitleText by rememberSaveable(stateSaver = TextFieldValue.Saver) { mutableStateOf(TextFieldValue(uiState.currentProfile.jobTitle)) }

    // Languages control
    var languagesText by rememberSaveable(stateSaver = TextFieldValue.Saver) { mutableStateOf(TextFieldValue(uiState.currentProfile.languages)) }
    var selectedLanguages by remember {
        mutableStateOf(
            if (uiState.currentProfile.languages.isNotEmpty()) {
                uiState.currentProfile.languages.split(", ").toMutableList()
            } else {
                mutableListOf()
            }
        )
    }
    var showLanguagesDialog by remember { mutableStateOf(false) }
    val allSelectedLanguages by remember { mutableStateOf(mutableSetOf<String>()) }
    if (selectedLanguages.isNotEmpty()) {
        allSelectedLanguages.addAll(selectedLanguages)
    }

    // Zodiac sign control
    var zodiacSignText by rememberSaveable(stateSaver = TextFieldValue.Saver) { mutableStateOf(TextFieldValue(uiState.currentProfile.zodiacSign)) }
    var showZodiacSignDialog by remember { mutableStateOf(false) }

    // Education level control
    var educationLevelText by rememberSaveable(stateSaver = TextFieldValue.Saver) { mutableStateOf(TextFieldValue(uiState.currentProfile.education)) }
    var showEducationLevelDialog by remember { mutableStateOf(false) }

    // Interests control
    var interestsText by rememberSaveable(stateSaver = TextFieldValue.Saver) { mutableStateOf(TextFieldValue(uiState.currentProfile.interests)) }
    var selectedInterests by remember {
        mutableStateOf(
            if (uiState.currentProfile.interests.isNotEmpty()) {
                uiState.currentProfile.interests.split(", ").toMutableList()
            } else {
                mutableListOf()
            }
        )
    }
    var showInterestsDialog by remember { mutableStateOf(false) }
    val allSelectedInterests by remember { mutableStateOf(mutableSetOf<String>()) }
    if (selectedInterests.isNotEmpty()) {
        allSelectedInterests.addAll(selectedInterests)
    }

    LaunchedEffect(key1 = Unit, block = {
        action.collect {
            when(it){
                EditProfileAction.ON_SIGNED_OUT -> onSignedOut()
                EditProfileAction.ON_PROFILE_EDITED -> onProfileEdited()
            }
        }
    })

    LaunchedEffect(key1 = uiState.errorMessage, block = {
        if(uiState.errorMessage != null){
            showErrorDialog = true
        }
    })

    if (showDeleteConfirmationDialog) {
        DeleteConfirmationDialog(
            onDismissRequest = { showDeleteConfirmationDialog = false },
            onConfirm = {
                showDeleteConfirmationDialog = false
                removePictureAt(deleteConfirmationPictureIndex)
            },
            onDismiss = { showDeleteConfirmationDialog = false })
    }

    if(showErrorDialog){
        ErrorDialog(
            errorDescription = uiState.errorMessage,
            onDismissRequest = { showErrorDialog = false },
            onConfirm = { showErrorDialog = false}
        )
    }

    Scaffold(
        topBar = {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically){
                Text(
                    text = stringResource(id = R.string.edit_profile),
                    color = MaterialTheme.colors.onSurface,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(16.dp),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.weight(1f))
                TextButton(onClick = {
                    updateProfile(
                        uiState.currentProfile,
                        bioText.text,
                        selectedGenderIndex,
                        selectedOrientationIndex,
                        heightText.text,
                        jobTitleText.text,
                        languagesText.text,
                        zodiacSignText.text,
                        educationLevelText.text,
                        interestsText.text,
                        uiState.pictures
                    )
                }) {
                    Text(text = stringResource(id = R.string.save))
                }
            }
        }){ padding ->
        Column(modifier = Modifier
            .padding(padding)
            .verticalScroll(rememberScrollState())) {
            repeat(RowCount){rowIndex ->
                PictureGridRow(
                    rowIndex = rowIndex,
                    pictures = uiState.pictures,
                    onAddPicture = addPicture,
                    onAddedPictureClicked = {
                        showDeleteConfirmationDialog = true
                        deleteConfirmationPictureIndex = it
                    }
                )
            }

            Spacer(Modifier.height(32.dp))
            Column(Modifier.fillMaxWidth()) {
                SectionTitle(title = stringResource(id = R.string.profile_about_me))
                CustomFormTextField(
                    value = bioText,
                    placeholder = stringResource(id = R.string.write_something_interesting),
                    maxCharacters = 500
                ) {
                    bioText = it
                }

                SectionTitle(title = stringResource(id = R.string.gender))
                HorizontalPicker(
                    id = R.array.genders,
                    selectedIndex = selectedGenderIndex,
                    onOptionClick = {
                        selectedGenderIndex = it

                    })

                SectionTitle(title = stringResource(id = R.string.i_am_interested_in))

                HorizontalPicker(
                    id = R.array.interests,
                    selectedIndex = selectedOrientationIndex,
                    onOptionClick = {
                        selectedOrientationIndex = it
                    })

                // Personal Information (Display only)
                SectionTitle(title = stringResource(id = R.string.personal_information))
                FormDivider()
                TextRow(title = stringResource(id = R.string.name), text = uiState.currentProfile.name)
                FormDivider()
                TextRow(title = stringResource(id = R.string.birth_date), text = uiState.currentProfile.birthDate)
                FormDivider()

                // Essentials
                SectionTitle(title = stringResource(id = R.string.profile_essentials))
                FormDivider()

                // Height
                SectionTitle(title = stringResource(id = R.string.profile_height))
                ClickableTextRow(
                    imageVector = Icons.Filled.Star, // TODO: replace with more appropriate icon
                    text = displayedHeightStr,
                    onClick = {
                        showHeightDialog = true
                    },
                )
                // Dialog for selecting height
                if (showHeightDialog) {
                    EditHeightSelectorDialog(
                        savedHeight = heightText.text,
                        onDismiss = { showHeightDialog = false },
                        onSave = { selectedHeight ->
                            heightText = TextFieldValue(selectedHeight)
                            showHeightDialog = false
                        }
                    )
                }
                FormDivider()

                SectionTitle(title = stringResource(id = R.string.profile_job_title))
                FormTextField(
                    value = jobTitleText,
                    placeholder = "Enter your job title"
                ) {
                    jobTitleText = it
                }
                FormDivider()

                // Languages
                SectionTitle(title = stringResource(id = R.string.profile_languages))
                ClickableTextRow(
                    imageVector = Icons.Filled.Face, // TODO: replace with more appropriate icon
                    text = languagesText.text,
                    onClick = {
                        showLanguagesDialog = true
                    },
                )

                // Dialog for selecting languages
                if (showLanguagesDialog) {
                    EditLanguagesSelectorDialog(
                        arrId = R.array.languages,
                        allSelectedLanguages = allSelectedLanguages,
                        onDismiss = { showLanguagesDialog = false },
                        onSave = { selectedLanguagesList ->
                            languagesText = TextFieldValue(
                                if (selectedLanguagesList.isNotEmpty()) {
                                    selectedLanguagesList.joinToString(", ")
                                } else {
                                    ""
                                }
                            )
                            selectedLanguages = selectedLanguagesList.toMutableList()
                            showLanguagesDialog = false
                        }
                    )
                }
                FormDivider()

                // Basics
                SectionTitle(title = stringResource(id = R.string.profile_basics))
                FormDivider()
                // Zodiac sign
                SectionTitle(title = stringResource(id = R.string.profile_zodiac_sign))
                ClickableTextRow(
                    imageVector = Icons.Filled.Star, // TODO: replace with more appropriate icon
                    text = zodiacSignText.text,
                    onClick = {
                        showZodiacSignDialog = true
                    },
                )
                // Dialog for selecting the zodiac sign
                if (showZodiacSignDialog) {
                    EditZodiacSignSelectorDialog(
                        arrId = R.array.zodiac_signs,
                        savedZodiacSign = zodiacSignText.text,
                        onDismiss = { showZodiacSignDialog = false },
                        onSave = { selectedZodiacSign ->
                            zodiacSignText = TextFieldValue(selectedZodiacSign)
                            showZodiacSignDialog = false
                        }
                    )
                }
                FormDivider()

                SectionTitle(title = stringResource(id = R.string.profile_education))
                ClickableTextRow(
                    imageVector = Icons.Filled.Build, // TODO: replace with more appropriate icon
                    text = educationLevelText.text,
                    onClick = {
                        showEducationLevelDialog = true
                    },
                )
                // Dialog for selecting the education level
                if (showEducationLevelDialog) {
                    EditEducationLevelSelectorDialog(
                        arrId = R.array.education_levels,
                        savedEducationLevel = educationLevelText.text,
                        onDismiss = { showEducationLevelDialog = false },
                        onSave = { selectedEducationLevel ->
                            educationLevelText = TextFieldValue(selectedEducationLevel)
                            showEducationLevelDialog = false
                        }
                    )
                }
                FormDivider()

                // Interests
                SectionTitle(title = stringResource(id = R.string.profile_interests))
                ClickableTextRow(
                    imageVector = Icons.Filled.Face, // TODO: replace with more appropriate icon
                    text = interestsText.text,
                    onClick = {
                        showInterestsDialog = true
                    },
                )

                // Dialog for selecting interests
                if (showInterestsDialog) {
                    EditInterestsSelectorDialog(
                        arrId = R.array.user_interests,
                        allSelectedInterests = allSelectedInterests,
                        onDismiss = { showInterestsDialog = false },
                        onSave = { selectedInterestsList ->
                            interestsText = TextFieldValue(
                                if (selectedInterestsList.isNotEmpty()) {
                                    selectedInterestsList.joinToString(", ")
                                } else {
                                    ""
                                }
                            )
                            selectedInterests = selectedInterestsList.toMutableList()
                            showInterestsDialog = false
                        }
                    )
                }
                FormDivider()

                Spacer(modifier = Modifier.height(32.dp))
                OutlinedButton(modifier = Modifier.fillMaxWidth(), onClick = signOut) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(all = 8.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(stringResource(id = R.string.sign_out), fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }

    if(uiState.isLoading){
        LoadingView()
    }
}
