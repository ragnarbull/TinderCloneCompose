package com.apiguave.tinderclonecompose.ui.components

import android.annotation.SuppressLint
import androidx.annotation.ArrayRes
import androidx.compose.foundation.layout.*
import androidx.compose.material.Checkbox
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.apiguave.tinderclonecompose.R

@SuppressLint("MutableCollectionMutableState")
@Composable
fun EditEducationLevelSelectorDialog(
    @ArrayRes arrId: Int,
    savedEducationLevel: String,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var selectedEducationLevelTemp by remember { mutableStateOf(savedEducationLevel) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 500.dp),
            elevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(id = R.string.profile_what_is_your_education_level),
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Filled.Close, contentDescription = "Close")
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))

                // Display selectable education levels in two columns
                EducationLevelSelector(
                    arrId = arrId,
                    selectedEducationLevel = selectedEducationLevelTemp,
                    onEducationLevelSelected = { selectedEducationLevelTemp = it }
                )

                Spacer(modifier = Modifier.height(16.dp))
                // Save button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = { onSave(selectedEducationLevelTemp) }) {
                        Text("Save")
                    }
                }
            }
        }
    }
}

@Composable
fun EducationLevelSelector(
    @ArrayRes arrId: Int,
    selectedEducationLevel: String,
    onEducationLevelSelected: (String) -> Unit
) {
    val educationLevelsOpts = stringArrayResource(id = arrId)
    val list = educationLevelsOpts.asList()
    val educationLevelsFirstColumn = list.subList(0, list.size / 2)
    val educationLevelsSecondColumn = list.subList(list.size / 2, list.size)

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            educationLevelsFirstColumn.forEach { educationLevel ->
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val isChecked = selectedEducationLevel == educationLevel
                    Checkbox(
                        checked = isChecked,
                        onCheckedChange = {
                            if (isChecked) {
                                onEducationLevelSelected("")
                            } else {
                                onEducationLevelSelected(educationLevel)
                            }
                        }
                    )
                    Text(
                        text = educationLevel,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        }

        Column {
            educationLevelsSecondColumn.forEach { educationLevel ->
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val isChecked = selectedEducationLevel == educationLevel
                    Checkbox(
                        checked = isChecked,
                        onCheckedChange = {
                            if (isChecked) {
                                onEducationLevelSelected("")
                            } else {
                                onEducationLevelSelected(educationLevel)
                            }
                        }
                    )
                    Text(
                        text = educationLevel,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        }
    }
}
