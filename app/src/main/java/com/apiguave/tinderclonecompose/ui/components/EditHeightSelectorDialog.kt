package com.apiguave.tinderclonecompose.ui.components

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.apiguave.tinderclonecompose.R

@SuppressLint("MutableCollectionMutableState")
@Composable
fun EditHeightSelectorDialog(
    savedHeight: String?,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    val TAG = "EditHeightSelectorDialog"
    // Height is saved in the format: 602 (6ft 2in), 511 (5ft 11in), 408 (4ft 8in)
    val savedHeightInt = savedHeight?.toIntOrNull() // Convert to Int or null if it's not a valid number
    var heightInFeetTemp by remember { mutableIntStateOf(savedHeightInt?.div(100) ?: 5) }
    var heightInInchesTemp by remember { mutableIntStateOf(savedHeightInt?.rem(100) ?: 6) }

    Log.d(TAG, "savedHeight: $savedHeight")
    Log.d(TAG, "heightInFeetTemp: $heightInFeetTemp")
    Log.d(TAG, "heightInInchesTemp: $heightInInchesTemp")

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 250.dp),
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
                        text = stringResource(id = R.string.profile_height),
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Filled.Close, contentDescription = "Close")
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))

                // Display selectable height
                HeightSelector(
                    heightInFeetTemp = heightInFeetTemp,
                    heightInInchesTemp = heightInInchesTemp,
                    onHeightInFeetSelected = { heightInFeetTemp = it },
                    onHeightInInchesSelected = { heightInInchesTemp = it }
                )

                Spacer(modifier = Modifier.height(16.dp))
                // Save button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = {
                        if (heightInFeetTemp in 4..7 && heightInInchesTemp in 0..11) {
                            val heightToSave = "${heightInFeetTemp.toString().padStart(1, '0')}${heightInInchesTemp.toString().padStart(2, '0')}"
                            Log.d(TAG, "heightToSave: $heightToSave")
                            onSave(heightToSave)
                        } else {
                            Log.d(TAG, "Invalid height")
                        }
                    }) {
                        Text("Save")
                    }
                }
            }
        }
    }
}

@Composable
fun HeightSelector(
    heightInFeetTemp: Int,
    heightInInchesTemp: Int,
    onHeightInFeetSelected: (Int) -> Unit,
    onHeightInInchesSelected: (Int) -> Unit
) {
    var feetValue by remember { mutableStateOf(heightInFeetTemp.toString()) }
    var inchesValue by remember { mutableStateOf(heightInInchesTemp.toString()) }
    var feetError by remember { mutableStateOf(false) }
    var inchesError by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Input for feet
        Column {
            Text(text = "Feet")
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = feetValue,
                onValueChange = { newValue ->
                    feetValue = newValue
                    val feet = newValue.toIntOrNull()
                    if (feet in 4..7) {
                        feetError = false
                        onHeightInFeetSelected(feet ?: heightInFeetTemp)
                    } else {
                        feetError = true
                    }
                },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            )
            if (feetError) {
                Text(
                    text = "Feet should be between 4 and 7",
                    color = Color.Red,
                    fontSize = 12.sp
                )
            }
        }

        // Input for inches
        Column {
            Text(text = "Inches")
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = inchesValue,
                onValueChange = { newValue ->
                    inchesValue = newValue
                    val inches = newValue.toIntOrNull()
                    if (inches in 0..11) {
                        inchesError = false
                        onHeightInInchesSelected(inches ?: heightInInchesTemp)
                    } else {
                        inchesError = true
                    }
                },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            )
            if (inchesError) {
                Text(
                    text = "Inches should be between 0 and 11",
                    color = Color.Red,
                    fontSize = 12.sp
                )
            }
        }
    }
}