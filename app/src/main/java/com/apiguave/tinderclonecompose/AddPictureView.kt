package com.apiguave.tinderclonecompose

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.apiguave.tinderclonecompose.fileprovider.ComposeFileProvider
import com.apiguave.tinderclonecompose.ui.theme.LightPurple
import com.apiguave.tinderclonecompose.ui.theme.Orange
import com.apiguave.tinderclonecompose.ui.theme.Pink
import com.apiguave.tinderclonecompose.ui.theme.Purple

@Composable
fun AddPictureView(onCloseClicked: () -> Unit){
    val galleryLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            // process eith the received image uri
        }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->

        }
    )

    var imageUri by remember {
        mutableStateOf<Uri?>(null)
    }
    val context = LocalContext.current

    Column(
        Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 16.dp)) {


        Icon(
            Icons.Filled.Close,
            modifier = Modifier
                .size(48.dp)
                .align(Alignment.Start)
                .clickable(onClick = onCloseClicked),
            tint = Color.Gray,
            contentDescription = null)

        Spacer(Modifier.height(28.dp))
        Text("Crear nuevo", fontSize = 30.sp, fontWeight = FontWeight.Bold)
        Text("Selecciona el tipo de contenido")

        Spacer(Modifier.weight(1f))
        SourceTypeCard(
            text1 = "Subir",
            text2 = "Foto",
            color1 = Pink,
            color2 = Orange,
            iconRes = R.drawable.ic_baseline_image_90,
            onClick = {galleryLauncher.launch("image/*")}
        )

        Spacer(Modifier.height(16.dp))
        SourceTypeCard(
            text1 = "Capturar desde",
            text2 = "Cámara",
            color1 = Purple,
            color2 = LightPurple,
            iconRes = R.drawable.ic_baseline_photo_camera_90,
            onClick = {
                val uri = ComposeFileProvider.getImageUri(context)
                imageUri = uri
                cameraLauncher.launch(uri)}
        )
        Spacer(Modifier.weight(1f))
    }
}

@Composable
fun SourceTypeCard(text1: String,
                   text2: String,
                   color1: Color,
                   color2: Color,
                   @DrawableRes iconRes: Int,
                   onClick: () -> Unit){
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp)
    ) {
        Box(modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        color1,
                        color2,
                    )
                )
            )){
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(20.dp)) {
                Text(text1,
                    color = Color.White,
                    fontWeight = FontWeight.Light)
                Text(text2,
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold)
            }

            Icon(
                tint = Color.White,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(90.dp)
                    .alpha(.8f)
                    .offset(x = 30.dp, y = 16.dp)
                    .rotate(-20f)
                ,
                painter = painterResource(id = iconRes),
                contentDescription = null,
            )
        }
    }
}