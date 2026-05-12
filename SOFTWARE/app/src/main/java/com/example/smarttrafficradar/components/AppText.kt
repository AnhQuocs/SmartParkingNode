package com.example.smarttrafficradar.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import com.example.smarttrafficradar.utils.s14
import com.example.smarttrafficradar.utils.s18

@Composable
fun AppHeader(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.s18,
    color: Color = MaterialTheme.colorScheme.onBackground,
    maxLines: Int = 1,
    minLines: Int = 1
) {
    Text(
        text = text,
        style = style,
        color = color,
        modifier = modifier,
        maxLines = maxLines,
        minLines = minLines,
        overflow = TextOverflow.Ellipsis
    )
}

@Composable
fun AppSubText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.s14,
    color: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    maxLines: Int = 1
) {
    Text(
        text = text,
        style = style,
        color = color,
        modifier = modifier,
        maxLines = maxLines,
        overflow = TextOverflow.Ellipsis
    )
}