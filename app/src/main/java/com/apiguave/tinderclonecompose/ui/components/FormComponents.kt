package com.apiguave.tinderclonecompose.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.apiguave.tinderclonecompose.extensions.withLinearGradient
import com.apiguave.tinderclonecompose.ui.theme.Nero
import com.apiguave.tinderclonecompose.ui.theme.SystemGray4
import kotlinx.coroutines.launch

@Composable
fun SectionTitle(title: String) {
    Text(
        title.uppercase(),
        modifier = Modifier.padding(all = 8.dp),
        color = if (isSystemInDarkTheme()) Color.LightGray else Color.DarkGray,
        fontWeight = FontWeight.Bold
    )
}

@Composable
fun FormDivider() {
    Spacer(
        Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(if (isSystemInDarkTheme()) Color.DarkGray else SystemGray4)
    )
}

@Composable
fun TextRow(title: String, text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(if (isSystemInDarkTheme()) Nero else Color.White)
            .padding(horizontal = 12.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, fontWeight = FontWeight.Bold, color = MaterialTheme.colors.onSurface)
        Spacer(modifier = Modifier.weight(1.0f))
        Text(
            text = text,
            color = MaterialTheme.colors.onSurface
        )
    }
}

@Composable
fun FormIconButton(imageVector: ImageVector, color1: Color, color2: Color){
    FormIconButton(painter = rememberVectorPainter(image = imageVector), color1 = color1, color2 = color2, )
}

@Composable
fun FormIconButton(painter: Painter, color1: Color, color2: Color){
    IconButton(onClick = {}, enabled = false) {
        Icon(
            painter = painter,
            modifier = Modifier
                .border(
                    2.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            color1,
                            color2
                        )
                    ), shape = CircleShape
                )
                .padding(2.dp)
                .size(16.dp)
                .withLinearGradient(color1, color2)

            , contentDescription = null
        )
    }
}

@Composable
fun ClickableTextRow(imageVector: ImageVector, text: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(if (isSystemInDarkTheme()) Nero else Color.White)
            .padding(horizontal = 12.dp, vertical = 16.dp)
            .clickable { onClick.invoke() },

    verticalAlignment = Alignment.CenterVertically
    ) {
        FormIconButton(
            imageVector = imageVector, color1 = Color.Blue, color2 = Color.Blue
        )
        Spacer(modifier = Modifier.weight(1.0f))
        Text(
            text = text,
            color = MaterialTheme.colors.onSurface
        )
    }
}

@Composable
fun FormTextField(
    modifier: Modifier = Modifier,
    value: TextFieldValue,
    placeholder: String,
    maxCharacters: Int = 500, // Default to 500 characters if not provided
    onValueChange: (TextFieldValue) -> Unit,
) {
    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .then(modifier),
        value = value,
        placeholder = { Text(placeholder) },
        onValueChange = {
            if (it.text.length <= maxCharacters) {
                onValueChange(it)
            }
        }
    )
}

@Composable
fun CustomFormTextField(
    value: TextFieldValue,
    placeholder: String,
    maxCharacters: Int = 500, // Default to 500 characters if not provided
    onValueChange: (TextFieldValue) -> Unit
    ) {
    var remainingCharacters = maxCharacters - value.text.length
    OutlinedTextField(
        value = value,
        onValueChange = {
            if (it.text.length <= maxCharacters) {
                onValueChange(it)
                remainingCharacters = maxCharacters - it.text.length
            }
        },
        placeholder = { Text(placeholder) },
        trailingIcon = {
            Text(
                text = "$remainingCharacters",
                style = MaterialTheme.typography.caption,
                color = if (remainingCharacters < 0) Color.Red else Color.Gray
            )
        },
        isError = remainingCharacters < 0
    )
}

@Composable
fun OptionButton(text: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    TextButton(
        modifier = modifier,
        onClick = onClick,
        contentPadding = PaddingValues(
            start = 16.dp,
            top = 16.dp,
            end = 16.dp,
            bottom = 16.dp
        )
    ) {
        Text(text, color = MaterialTheme.colors.onSurface.copy(alpha = .2f))
    }
}


