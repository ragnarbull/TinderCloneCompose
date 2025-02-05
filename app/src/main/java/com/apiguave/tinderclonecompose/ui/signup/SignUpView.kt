package com.apiguave.tinderclonecompose.ui.signup

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.apiguave.tinderclonecompose.R
import com.apiguave.tinderclonecompose.domain.profile.entity.CreateUserProfile
import com.apiguave.tinderclonecompose.domain.profile.entity.Orientation
import com.apiguave.tinderclonecompose.domain.profile.entity.UserLocation
import com.apiguave.tinderclonecompose.extensions.isValidUsername
import com.apiguave.tinderclonecompose.ui.components.*
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@Composable
fun SignUpView(
    uiState: SignUpUiState,
    signInClient: GoogleSignInClient,
    onAddPicture: () -> Unit,
    onNavigateToHome: () -> Unit,
    signUp: (data: Intent?, profile: CreateUserProfile) -> Unit,
    removePictureAt: (Int) -> Unit
) {

    var showDeleteConfirmationDialog by remember { mutableStateOf(false) }
    var showErrorDialog by remember { mutableStateOf(false) }

    var deleteConfirmationPictureIndex by remember { mutableStateOf(0) }
    var birthdate by rememberSaveable { mutableStateOf(eighteenYearsAgo) }
    val dateDialogState = rememberMaterialDialogState()
    var nameText by rememberSaveable(stateSaver = TextFieldValue.Saver) { mutableStateOf(TextFieldValue("")) }
    var bioText by rememberSaveable(stateSaver = TextFieldValue.Saver) { mutableStateOf(TextFieldValue("")) }

    var selectedGenderIndex by rememberSaveable { mutableStateOf(-1) }
    var selectedOrientationIndex by rememberSaveable { mutableStateOf(-1) }

    var heightText by rememberSaveable(stateSaver = TextFieldValue.Saver) { mutableStateOf(TextFieldValue("")) }
    var jobTitleText by rememberSaveable(stateSaver = TextFieldValue.Saver) { mutableStateOf(TextFieldValue("")) }
    var languagesText by rememberSaveable(stateSaver = TextFieldValue.Saver) { mutableStateOf(TextFieldValue("")) }
    var zodiacSignText by rememberSaveable(stateSaver = TextFieldValue.Saver) { mutableStateOf(TextFieldValue("")) }
    var educationText by rememberSaveable(stateSaver = TextFieldValue.Saver) { mutableStateOf(TextFieldValue("")) }
    var interestsText by rememberSaveable(stateSaver = TextFieldValue.Saver) { mutableStateOf(TextFieldValue("")) }

    val isSignUpEnabled = remember { derivedStateOf { nameText.text.isValidUsername() && uiState.pictures.size > 1 && selectedGenderIndex >= 0 && selectedOrientationIndex >= 0 } }
    val coroutineScope = rememberCoroutineScope()

    //Update UI state

    LaunchedEffect(key1 = uiState, block = {
        if(uiState.isUserSignedIn){
            onNavigateToHome()
        }
        if(uiState.errorMessage != null){
            showErrorDialog = true
        }
    })


    val startForResult = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = {activityResult ->
            //Transforms the Uris to Bitmaps
            val isMale = selectedGenderIndex == 0
            val location = UserLocation(44.6553, -110.6702) // Yellowstone
            val orientation = Orientation.entries[selectedOrientationIndex]
            val maxDistance = 5 // km
            val minAge = 18 //
            val maxAge = 35
            val profile = CreateUserProfile(
                nameText.text,
                birthdate,
                bioText.text,
                isMale,
                location,
                orientation,
                maxDistance,
                minAge,
                maxAge,
                heightText.text,
                jobTitleText.text,
                languagesText.text,
                zodiacSignText.text,
                educationText.text,
                interestsText.text,
                uiState.pictures.map { it.bitmap },
                )
            //Signs up with the information provided
            signUp(activityResult.data, profile)
        }
    )

    //Dialogs

    if (showDeleteConfirmationDialog) {
        DeleteConfirmationDialog(
            onDismissRequest = { showDeleteConfirmationDialog = false },
            onConfirm = {
                showDeleteConfirmationDialog = false
                removePictureAt(deleteConfirmationPictureIndex)},
            onDismiss = { showDeleteConfirmationDialog = false})
    }

    if(showErrorDialog){
        ErrorDialog(
            errorDescription = uiState.errorMessage,
            onDismissRequest = { showErrorDialog = false },
            onConfirm = { showErrorDialog = false}
        )
    }

    FormDatePickerDialog(dateDialogState, onDateChange = { birthdate = it })
    
    Surface {
        LazyColumn( modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        ) {

            item {
                Text(
                    text = stringResource(id = R.string.create_profile),
                    modifier = Modifier.padding(16.dp),
                    fontSize = 30.sp,
                    color = MaterialTheme.colors.onSurface,
                    fontWeight = FontWeight.Bold
                )
            }

            items(RowCount){ rowIndex ->
                PictureGridRow(
                    rowIndex = rowIndex,
                    pictures = uiState.pictures,
                    onAddPicture = onAddPicture,
                    onAddedPictureClicked = {
                        showDeleteConfirmationDialog = true
                        deleteConfirmationPictureIndex = it
                    }
                )
            }

            item {
                Spacer(
                    Modifier
                        .fillMaxWidth()
                        .height(32.dp))
                Column(Modifier.fillMaxWidth()) {
                    SectionTitle(title = stringResource(id = R.string.personal_information))
                    FormTextField(
                        value = nameText,
                        placeholder = stringResource(id = R.string.enter_your_name)
                    ) { newText ->
                        nameText = newText
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                BorderStroke(
                                    1.dp,
                                    if (isSystemInDarkTheme()) Color.DarkGray else Color.LightGray
                                )
                            ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(stringResource(id = R.string.birth_date), modifier = Modifier.padding(start = 8.dp), color = MaterialTheme.colors.onSurface)
                        Spacer(modifier = Modifier.weight(1.0f))
                        TextButton(
                            onClick = { dateDialogState.show() },
                            contentPadding = PaddingValues(
                                start = 20.dp,
                                top = 20.dp,
                                end = 20.dp,
                                bottom = 20.dp
                            )
                        ) {
                            Text(
                                birthdate.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG)),
                                color = MaterialTheme.colors.onSurface
                            )
                        }
                    }

                    SectionTitle(title = stringResource(id = R.string.profile_about_me) )
                    FormTextField(
                        modifier = Modifier.height(128.dp),
                        value = bioText,
                        placeholder = stringResource(id = R.string.write_something_interesting)
                    ) { bioText = it }

                    SectionTitle(title = stringResource(id = R.string.gender))
                    HorizontalPicker(
                        id = R.array.genders,
                        selectedIndex = selectedGenderIndex,
                        onOptionClick = { selectedGenderIndex = it })

                    SectionTitle(title = stringResource(id = R.string.i_am_interested_in))
                    HorizontalPicker(
                        id = R.array.interests,
                        selectedIndex = selectedOrientationIndex,
                        onOptionClick = { selectedOrientationIndex = it })

                    // Essentials
                    SectionTitle(title = stringResource(id = R.string.profile_essentials))
                    FormDivider()
                    SectionTitle(title = stringResource(id = R.string.profile_height))
                    FormTextField(
                        value = heightText,
                        placeholder = "Enter your height in ft"
                    ) {
                        heightText = it
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
                    SectionTitle(title = stringResource(id = R.string.profile_languages))
                    FormTextField(
                        value = languagesText,
                        placeholder = "Enter languages you know"
                    ) {
                        languagesText = it
                    }
                    FormDivider()

                    // Basics
                    SectionTitle(title = stringResource(id = R.string.profile_basics))
                    FormDivider()
                    SectionTitle(title = stringResource(id = R.string.profile_zodiac_sign))
                    FormTextField(
                        value = zodiacSignText,
                        placeholder = "Enter your Zodiac sign"
                    ) {
                        zodiacSignText = it
                    }
                    FormDivider()
                    SectionTitle(title = stringResource(id = R.string.profile_education))
                    FormTextField(
                        value = educationText,
                        placeholder = "Enter your highest education"
                    ) {
                        educationText = it
                    }
                    FormDivider()

                    // Interests
                    SectionTitle(title = stringResource(id = R.string.profile_interests))
                    FormDivider()
                    FormTextField(
                        value = interestsText,
                        placeholder = "Enter your interests"
                    ) {
                        interestsText = it
                    }
                    FormDivider()

                    Spacer(
                        Modifier
                            .fillMaxWidth()
                            .height(32.dp))

                    GradientGoogleButton(enabled = isSignUpEnabled.value) {
                        coroutineScope.launch {
                            startForResult.launch(signInClient.signInIntent)
                        }
                    }
                    Spacer(
                        Modifier
                            .fillMaxWidth()
                            .height(32.dp))
                }
            }
        }
    }

    if(uiState.isLoading){
        LoadingView()
    }
}

