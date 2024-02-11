package com.apiguave.tinderclonecompose.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.apiguave.tinderclonecompose.R
import com.apiguave.tinderclonecompose.domain.profilecard.entity.Profile

@Composable
fun FullProfileView(
    profile: Profile,
    modifier: Modifier = Modifier,
    contentModifier: Modifier = Modifier,
){
    var currentIndex by remember{ mutableIntStateOf(0) }

    val gradient = Brush.verticalGradient(
        colorStops = arrayOf(
            .68f to Color.Transparent,
            .92f to Color.Black
        )
    )
    Column {
        Card(modifier = modifier
            .fillMaxWidth()
            .aspectRatio(.6f),
            shape = RoundedCornerShape(8.dp)
        ) {
            Box(Modifier.fillMaxSize()) {
                //Gradient
                //Picture
                AsyncImage(
                    modifier = Modifier.fillMaxSize(),
                    model = profile.pictures[currentIndex],
                    contentScale = ContentScale.Crop,
                    contentDescription = null)
                //Gradient
                Spacer(Modifier.fillMaxSize().background(gradient))
                Box(contentModifier.fillMaxSize()){
                    //Upper picture index indicator
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(6.dp)) {
                        repeat(profile.pictures.size){ index ->
                            Box(
                                Modifier
                                    .weight(1f)
                                    .height(3.dp)
                                    .padding(horizontal = 4.dp)
                                    .alpha(if (index == currentIndex) 1f else .5f)
                                    .background(if (index == currentIndex) Color.White else Color.LightGray))
                        }
                    }
                    //Clickable
                    Row(Modifier.fillMaxSize()) {
                        Box(modifier = Modifier
                            .fillMaxHeight()
                            .weight(1f)
                            .clickable { if (currentIndex > 0) currentIndex-- })
                        Box(modifier = Modifier
                            .fillMaxHeight()
                            .weight(1f)
                            .clickable { if (currentIndex < profile.pictures.size - 1) currentIndex++ }
                        )
                    }
                }
            }
        }
        Spacer(Modifier.fillMaxSize().background(gradient))
        Box(Modifier.fillMaxSize()) {
            Box(contentModifier.fillMaxSize()) {
                //Information
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SectionTitle(title = stringResource(id = R.string.about_me))
                }
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = profile.bio)
                }
            }
        }
    }
}
