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
fun EditZodiacSignSelectorDialog(
    @ArrayRes arrId: Int,
    savedZodiacSign: String,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var selectedZodiacSignTemp by remember { mutableStateOf(savedZodiacSign) }

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
                        text = stringResource(id = R.string.profile_what_is_your_zodiac_sign),
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Filled.Close, contentDescription = "Close")
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))

                // Display selectable Zodiac signs in two columns
                ZodiacSignSelector(
                    arrId = arrId,
                    selectedZodiacSign = selectedZodiacSignTemp,
                    onZodiacSignSelected = { selectedZodiacSignTemp = it }
                )

                Spacer(modifier = Modifier.height(16.dp))
                // Save button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = { onSave(selectedZodiacSignTemp) }) {
                        Text("Save")
                    }
                }
            }
        }
    }
}

@Composable
fun ZodiacSignSelector(
    @ArrayRes arrId: Int,
    selectedZodiacSign: String,
    onZodiacSignSelected: (String) -> Unit
) {
    val zodiacOpts = stringArrayResource(id = arrId)
    val list = zodiacOpts.asList()
    val zodiacSignsFirstColumn = list.subList(0, list.size / 2)
    val zodiacSignsSecondColumn = list.subList(list.size / 2, list.size)

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            zodiacSignsFirstColumn.forEach { zodiacSign ->
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val isChecked = selectedZodiacSign == zodiacSign
                    Checkbox(
                        checked = isChecked,
                        onCheckedChange = {
                            if (isChecked) {
                                onZodiacSignSelected("")
                            } else {
                                onZodiacSignSelected(zodiacSign)
                            }
                        }
                    )
                    Text(
                        text = zodiacSign,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        }

        Column {
            zodiacSignsSecondColumn.forEach { zodiacSign ->
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val isChecked = selectedZodiacSign == zodiacSign
                    Checkbox(
                        checked = isChecked,
                        onCheckedChange = {
                            if (isChecked) {
                                onZodiacSignSelected("")
                            } else {
                                onZodiacSignSelected(zodiacSign)
                            }
                        }
                    )
                    Text(
                        text = zodiacSign,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        }
    }
}
