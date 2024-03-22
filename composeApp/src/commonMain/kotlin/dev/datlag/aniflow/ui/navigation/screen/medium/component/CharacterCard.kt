package dev.datlag.aniflow.ui.navigation.screen.medium.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.rememberAsyncImagePainter
import dev.datlag.aniflow.anilist.model.Medium
import dev.datlag.aniflow.common.preferredName
import dev.datlag.aniflow.common.shimmerPainter

@Composable
fun CharacterCard(char: Medium.Character, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
    ) {
        AsyncImage(
            model = char.image.large,
            contentDescription = null,
            modifier = Modifier.fillMaxWidth().aspectRatio(0.7F).clip(MaterialTheme.shapes.medium),
            contentScale = ContentScale.Crop,
            placeholder = shimmerPainter(),
            error = rememberAsyncImagePainter(
                model = char.image.medium,
                contentScale = ContentScale.Crop,
                placeholder = shimmerPainter()
            )
        )

        Box(
            modifier = Modifier.weight(1F),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = char.preferredName(),
                style = MaterialTheme.typography.labelLarge,
                maxLines = 2,
                softWrap = true,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 16.dp,
                        vertical = 12.dp
                    )
            )
        }
    }
}