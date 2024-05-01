package dev.datlag.aniflow.ui.navigation.screen.medium.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.rememberAsyncImagePainter
import dev.datlag.aniflow.anilist.model.Character
import dev.datlag.aniflow.anilist.model.Medium
import dev.datlag.aniflow.common.preferredName
import dev.datlag.aniflow.settings.model.CharLanguage
import dev.datlag.tooling.compose.ifTrue
import dev.datlag.tooling.decompose.lifecycle.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.Flow

@Composable
fun CharacterCard(
    char: Character,
    languageFlow: Flow<CharLanguage?>,
    modifier: Modifier = Modifier,
    onClick: (Character) -> Unit
) {
    Card(
        modifier = modifier,
        onClick = {
            onClick(char)
        }
    ) {
        var imageShadow by remember(char.image) { mutableStateOf(false) }

        AsyncImage(
            model = char.image.large,
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(0.7F)
                .ifTrue(imageShadow) {
                    shadow(
                        elevation = 8.dp,
                        shape = MaterialTheme.shapes.medium,
                        spotColor = MaterialTheme.colorScheme.primary
                    )
                },
            contentScale = ContentScale.Crop,
            error = rememberAsyncImagePainter(
                model = char.image.medium,
                contentScale = ContentScale.Crop,
                onSuccess = {
                    imageShadow = true
                }
            ),
            onSuccess = {
                imageShadow = true
            }
        )

        Box(
            modifier = Modifier.weight(1F),
            contentAlignment = Alignment.Center
        ) {
            val charLanguage by languageFlow.collectAsStateWithLifecycle(null)

            Text(
                text = char.preferredName(charLanguage),
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