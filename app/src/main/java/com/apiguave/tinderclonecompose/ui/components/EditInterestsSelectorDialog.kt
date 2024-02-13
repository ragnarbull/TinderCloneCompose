package com.apiguave.tinderclonecompose.ui.components

import android.annotation.SuppressLint
import android.util.Log
import androidx.annotation.ArrayRes
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
fun EditInterestsSelectorDialog(
    @ArrayRes arrId: Int,
    allSelectedInterests: MutableSet<String>,
    onDismiss: () -> Unit,
    onSave: (MutableSet<String>) -> Unit
) {
    Log.d("InterestsSelector", "allSelectedInterests: $allSelectedInterests")

    val selectedInterestsTemp by remember { mutableStateOf(mutableListOf<String>()) }
    Log.d("InterestsSelector", "selectedInterestsTemp: $selectedInterestsTemp")
    if (selectedInterestsTemp.isNotEmpty()) {
        allSelectedInterests.addAll(selectedInterestsTemp)
    }
    Log.d("InterestsSelector", "allSelectedInterests: $allSelectedInterests")

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 600.dp),
            elevation = 8.dp
        ) {
            LazyColumn  {
                item {
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
                                text = stringResource(id = R.string.profile_interests),
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                            IconButton(onClick = onDismiss) {
                                Icon(Icons.Filled.Close, contentDescription = "Close")
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))

                        // Display selectable interests in two columns
                        InterestsSelector(
                            arrId = arrId,
                            allSelectedInterests = allSelectedInterests,
                            onInterestSelected = { interest ->
                                if (allSelectedInterests.contains(interest)) {
                                    Log.d("InterestsSelector", "Removing: $interest")
                                    allSelectedInterests.remove(interest)
                                    allSelectedInterests.remove(interest)
                                } else {
                                    Log.d("InterestsSelector", "Adding: $interest")
                                    allSelectedInterests.add(interest)
                                    selectedInterestsTemp.add(interest)
                                }
                            }
                        )

                        Spacer(modifier = Modifier.height(16.dp))
                        // Save button
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(onClick = { onSave(allSelectedInterests) }) {
                                Text("Save")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun InterestsSelector(
    @ArrayRes arrId: Int,
    allSelectedInterests: MutableSet<String>,
    onInterestSelected: (String) -> Unit
) {
    Log.d("InterestsSelector", "Composable recomposed")

    val interestsOpts = stringArrayResource(id = arrId)
    val list = interestsOpts.asList()
    val interestsFirstColumn = list.subList(0, list.size / 2)
    val interestsSecondColumn = list.subList(list.size / 2, list.size)

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            interestsFirstColumn.forEach { interest ->
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val isChecked = allSelectedInterests.contains(interest)
                    Checkbox(
                        checked = isChecked,
                        onCheckedChange = {
                            if (isChecked) {
                                onInterestSelected(interest)
                            } else {
                                onInterestSelected(interest)
                            }
                        }
                    )
                    Text(
                        text = interest,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        }

        Column {
            interestsSecondColumn.forEach { interest ->
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val isChecked = allSelectedInterests.contains(interest)
                    Checkbox(
                        checked = isChecked,
                        onCheckedChange = {
                            if (isChecked) {
                                onInterestSelected(interest)
                            } else {
                                onInterestSelected(interest)
                            }
                        }
                    )
                    Text(
                        text = interest,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        }
    }
}
