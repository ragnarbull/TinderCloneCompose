package com.apiguave.tinderclonecompose.ui.discoverysettings

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.apiguave.tinderclonecompose.R
import com.apiguave.tinderclonecompose.domain.discoverysettingscard.entity.CurrentDiscoverySettings
import com.apiguave.tinderclonecompose.ui.components.*
import kotlinx.coroutines.flow.SharedFlow

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DiscoverySettingsView(
    uiState: DiscoverySettingsUiState,
    navigateToHomeView: () -> Unit,
    updateDiscoverySettings: (CurrentDiscoverySettings, Int, Int, Int) -> Unit,
    action: SharedFlow<EditDiscoverySettingsAction>
) {
    val TAG = "DiscoverySettingsView"
    var showErrorDialog by remember { mutableStateOf(false) }

    // Initialization of values from currentDiscoverySettings
    var maxDistance by rememberSaveable { mutableIntStateOf(uiState.currentDiscoverySettings.maxDistance) }
    var minAge by rememberSaveable { mutableIntStateOf(uiState.currentDiscoverySettings.minAge) }
    var maxAge by rememberSaveable { mutableIntStateOf(uiState.currentDiscoverySettings.maxAge) }

    LaunchedEffect(key1 = Unit) {
        action.collect { event ->
            when (event) {
                EditDiscoverySettingsAction.ON_DISCOVERY_SETTINGS_EDITED -> {
                    try {
                        Log.d(TAG, "Navigating to Home View...")
                        navigateToHomeView()
                    } catch (e: Exception) {
                        Log.e(TAG, "Error fetching profiles: ${e.message}", e)
                    }
                }
            }
        }
    }

    LaunchedEffect(key1 = uiState.errorMessage, block = {
        if(uiState.errorMessage != null){
            showErrorDialog = true
        }
    })

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
                    text = stringResource(id = R.string.discovery_settings),
                    color = MaterialTheme.colors.onSurface,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(16.dp),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.weight(1f))
                TextButton(onClick = {
                    updateDiscoverySettings(uiState.currentDiscoverySettings, maxDistance, minAge, maxAge)
                }) {
                    Text(text = stringResource(id = R.string.save))
                }
            }
        }){ padding ->
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            FormDivider()
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                backgroundColor = MaterialTheme.colors.surface.copy(alpha = 0.1f)
            ) {
                Column {
                    // Max Distance Section
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        SectionTitle(title = stringResource(id = R.string.max_distance))
                        Text(
                            text = "$maxDistance km",
                            modifier = Modifier.padding(end = 16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Slider(
                        value = maxDistance.toFloat(),
                        onValueChange = { value ->
                            maxDistance = value.toInt()
                        },
                        valueRange = 2f..50f,
                        steps = 1,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
            FormDivider()

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                backgroundColor = MaterialTheme.colors.surface.copy(alpha = 0.1f)
            ) {
                Column {
                    // Age Range Section
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        SectionTitle(title = stringResource(id = R.string.age_range))
                        Text(
                            text = "$minAge - $maxAge",
                            modifier = Modifier.padding(end = 16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    RangeSlider(
                        value = minAge.toFloat()..maxAge.toFloat(),
                        onValueChange = { range ->
                            minAge = range.start.toInt()
                            maxAge = range.endInclusive.toInt()
                        },
                        valueRange = 18f..100f,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
            FormDivider()
        }
    }

    if(uiState.isLoading){
        LoadingView()
    }
}


