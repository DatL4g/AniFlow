package dev.datlag.aniflow.ui.navigation.screen.home.component.airing

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Slideshow
import androidx.compose.material.icons.rounded.Slideshow
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.datlag.aniflow.SharedRes
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun Episode(value: Int, color: Color = LocalContentColor.current) {
    Row(
        modifier = Modifier.fillMaxWidth()
            .height(IntrinsicSize.Min),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Rounded.Slideshow,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = color
        )
        Text(
            text = stringResource(SharedRes.strings.episode_number, value),
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
            color = color
        )
    }
}