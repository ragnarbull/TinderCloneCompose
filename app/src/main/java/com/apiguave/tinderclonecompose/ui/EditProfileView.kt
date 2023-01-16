package com.apiguave.tinderclonecompose.ui

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.apiguave.tinderclonecompose.R
import com.apiguave.tinderclonecompose.ui.shared.*

@Composable
fun EditProfileView(imageUris: SnapshotStateList<Uri>, onAddPicture: () -> Unit) {
    var deleteConfirmationDialog by remember { mutableStateOf(false) }
    var deleteConfirmationPictureIndex by remember { mutableStateOf(-1) }

    if (deleteConfirmationDialog) {
        DeleteConfirmationDialog(
            onDismissRequest = { deleteConfirmationDialog = false },
            onConfirm = {
                deleteConfirmationDialog = false
                imageUris.removeAt(deleteConfirmationPictureIndex) },
            onDismiss = { deleteConfirmationDialog = false})
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .background(MaterialTheme.colors.surface),
    ) {

        val rows = 1 + (GridItemCount -1) / ColumnCount
        item {
            Text(
                text = stringResource(id = R.string.edit_profile),
                color = MaterialTheme.colors.onSurface,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold)
        }

        items(rows){ rowIndex ->
            PictureGridRow(
                rowIndex = rowIndex,
                imageUris = imageUris,
                onAddPicture = onAddPicture,
                onAddedPictureClicked = {
                    deleteConfirmationDialog = true
                    deleteConfirmationPictureIndex = it
                }
            )
        }
        item{
            Spacer(modifier = Modifier
                .fillMaxWidth()
                .height(32.dp))
        }

        item {
            EditProfileFormView()
        }
    }
}

@Composable
fun EditProfileFormView(){
    var bioText by remember { mutableStateOf(TextFieldValue("")) }

    var selectedGenderIndex by remember { mutableStateOf(0) }
    var selectedOrientationIndex by remember { mutableStateOf(0) }

    Column(Modifier.fillMaxWidth()) {
        SectionTitle(title = stringResource(id = R.string.about_me))
        FormTextField(value = bioText, placeholder = stringResource(id = R.string.write_something_interesting), onValueChange = {
            bioText = it
        })

        SectionTitle(title = stringResource(id = R.string.gender))
        GenderOptions(
            selectedIndex = selectedGenderIndex,
            onOptionClick = { selectedGenderIndex = it })

        SectionTitle(title = stringResource(id = R.string.i_am_interested_in))

        OrientationOptions(
            selectedIndex = selectedOrientationIndex,
            onOptionClick = { selectedOrientationIndex = it })


        SectionTitle(title = stringResource(id = R.string.personal_information))
        FormDivider()
        TextRow(title = stringResource(id = R.string.name), text = "Alejandro")
        FormDivider()
        TextRow(title = stringResource(id = R.string.birth_date), text = "Ago 10 2001")
        FormDivider()

        Spacer(modifier = Modifier
            .fillMaxWidth()
            .height(32.dp))
        OutlinedButton(modifier = Modifier.fillMaxWidth(), onClick = {}){
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(all = 8.dp), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically){
                Text(stringResource(id = R.string.sign_out), fontWeight = FontWeight.Bold)
            }
        }
        Spacer(modifier = Modifier
            .fillMaxWidth()
            .height(32.dp))

    }
}

