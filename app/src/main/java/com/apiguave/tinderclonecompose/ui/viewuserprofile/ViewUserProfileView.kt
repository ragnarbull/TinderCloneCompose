package com.apiguave.tinderclonecompose.ui.viewuserprofile

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.apiguave.tinderclonecompose.R
import com.apiguave.tinderclonecompose.domain.profilecard.entity.Profile
import com.apiguave.tinderclonecompose.extensions.withLinearGradient
import com.apiguave.tinderclonecompose.ui.components.*
import com.apiguave.tinderclonecompose.ui.theme.Orange
import com.apiguave.tinderclonecompose.ui.theme.Pink

@SuppressLint("ResourceType")
@Composable
fun ViewUserProfileView(
    userProfile: Profile,
    onArrowBackPressed: () -> Unit,
) {

    Scaffold(
        topBar = {
            ProfileTopBar(userProfile = userProfile, onArrowBackPressed = onArrowBackPressed)
            },
        ){ padding ->
        Column(modifier = Modifier
            .padding(padding)
            .verticalScroll(rememberScrollState())) {
            Spacer(Modifier.weight(1f))
            Box(Modifier.padding(horizontal = 12.dp)) {
                val buttonRowHeightDp by remember { mutableStateOf(0.dp) }
                FullProfileView(userProfile,
                    contentModifier = Modifier.padding(bottom = buttonRowHeightDp.plus(8.dp))
                )
            }
        }
    }
}

@Composable
fun ProfileTopBar(userProfile: Profile, onArrowBackPressed: () -> Unit){
    Surface(elevation = AppBarDefaults.TopAppBarElevation){
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically){
            Column(Modifier.padding(vertical = 8.dp), horizontalAlignment = Alignment.CenterHorizontally){
                Text(text = userProfile.name, fontSize = 13.sp,fontWeight = FontWeight.Light, color = Color.Gray,textAlign = TextAlign.Center)
                Text(text = userProfile.age.toString(), fontSize = 13.sp,fontWeight = FontWeight.Light, color = Color.Gray,textAlign = TextAlign.Center)
            }
            Spacer(Modifier.weight(1f))
            Box(Modifier.weight(1f)){
                IconButton(modifier = Modifier.height(IntrinsicSize.Max),onClick = onArrowBackPressed) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_chevron_left_24),
                        contentDescription = null,
                        modifier = Modifier
                            .size(40.dp)
                            .withLinearGradient(Pink, Orange)
                            .align(Alignment.TopCenter)
                    )
                }
            }
        }
    }
}

