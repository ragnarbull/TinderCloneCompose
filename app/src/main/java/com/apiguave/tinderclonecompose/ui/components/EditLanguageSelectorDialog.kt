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
fun EditLanguagesSelectorDialog(
    @ArrayRes arrId: Int,
    allSelectedLanguages: MutableSet<String>,
    onDismiss: () -> Unit,
    onSave: (MutableSet<String>) -> Unit
) {
    Log.d("LanguagesSelector", "allSelectedLanguages: $allSelectedLanguages")

    val selectedLanguagesTemp by remember { mutableStateOf(mutableListOf<String>()) }
    Log.d("LanguagesSelector", "selectedLanguagesTemp: $selectedLanguagesTemp")
    if (selectedLanguagesTemp.isNotEmpty()) {
        allSelectedLanguages.addAll(selectedLanguagesTemp)
    }
    Log.d("LanguagesSelector", "allSelectedLanguages: $allSelectedLanguages")

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
                                text = stringResource(id = R.string.profile_languages_i_know),
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                            IconButton(onClick = onDismiss) {
                                Icon(Icons.Filled.Close, contentDescription = "Close")
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))

                        // Display selectable languages in two columns
                        LanguagesSelector(
                            arrId = arrId,
                            allSelectedLanguages = allSelectedLanguages,
                            onLanguageSelected = { language ->
                                if (allSelectedLanguages.contains(language)) {
                                    Log.d("LanguagesSelector", "Removing: $language")
                                    allSelectedLanguages.remove(language)
                                    selectedLanguagesTemp.remove(language)
                                } else {
                                    Log.d("LanguagesSelector", "Adding: $language")
                                    allSelectedLanguages.add(language)
                                    selectedLanguagesTemp.add(language)
                                }
                            }
                        )

                        Spacer(modifier = Modifier.height(16.dp))
                        // Save button
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(onClick = { onSave(allSelectedLanguages) }) {
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
fun LanguagesSelector(
    @ArrayRes arrId: Int,
    allSelectedLanguages: MutableSet<String>,
    onLanguageSelected: (String) -> Unit
) {
    Log.d("LanguagesSelector", "Composable recomposed")

    val languageOpts = stringArrayResource(id = arrId)
    val list = languageOpts.asList()
    val languagesFirstColumn = list.subList(0, list.size / 2)
    val languagesSecondColumn = list.subList(list.size / 2, list.size)

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            languagesFirstColumn.forEach { language ->
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val isChecked = allSelectedLanguages.contains(language)
                    Checkbox(
                        checked = isChecked,
                        onCheckedChange = {
                            if (isChecked) {
                                onLanguageSelected(language)
                            } else {
                                onLanguageSelected(language)
                            }
                        }
                    )
                    Text(
                        text = language,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        }

        Column {
            languagesSecondColumn.forEach { language ->
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val isChecked = allSelectedLanguages.contains(language)
                    Checkbox(
                        checked = isChecked,
                        onCheckedChange = {
                            if (isChecked) {
                                onLanguageSelected(language)
                            } else {
                                onLanguageSelected(language)
                            }
                        }
                    )
                    Text(
                        text = language,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        }
    }
}
