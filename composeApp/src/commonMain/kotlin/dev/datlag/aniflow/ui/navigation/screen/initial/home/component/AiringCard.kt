package dev.datlag.aniflow.ui.navigation.screen.initial.home.component

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.rememberAsyncImagePainter
import dev.datlag.aniflow.LocalDI
import dev.datlag.aniflow.anilist.AiringQuery
import dev.datlag.aniflow.anilist.model.Medium
import dev.datlag.aniflow.common.preferred
import dev.datlag.aniflow.settings.Settings
import dev.datlag.aniflow.settings.model.AppSettings
import dev.datlag.aniflow.ui.theme.SchemeTheme
import dev.datlag.aniflow.ui.theme.rememberSchemeThemeDominantColorState
import dev.datlag.tooling.decompose.lifecycle.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import org.kodein.di.instance
import org.kodein.di.instanceOrNull
import dev.datlag.aniflow.settings.model.TitleLanguage as SettingsTitle

@Composable
fun AiringCard(
    airing: AiringQuery.AiringSchedule,
    titleLanguage: SettingsTitle?,
    modifier: Modifier = Modifier,
    onClick: (Medium) -> Unit
) {
    airing.media?.let(::Medium)?.let { media ->
        val updater = SchemeTheme.create(media.id)

        Card(
            modifier = modifier,
            onClick = {
                onClick(media)
            }
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AsyncImage(
                    modifier = Modifier.widthIn(min = 100.dp, max = 120.dp).fillMaxHeight().clip(MaterialTheme.shapes.medium),
                    model = media.coverImage.extraLarge,
                    contentDescription = media.preferred(titleLanguage),
                    contentScale = ContentScale.Crop,
                    error = rememberAsyncImagePainter(
                        model = media.coverImage.large,
                        contentScale = ContentScale.Crop,
                        error = rememberAsyncImagePainter(
                            model = media.coverImage.medium,
                            contentScale = ContentScale.Crop,
                            onSuccess = { state ->
                                updater?.update(state.painter)
                            }
                        ),
                        onSuccess = { state ->
                            updater?.update(state.painter)
                        }
                    ),
                    onSuccess = { state ->
                        updater?.update(state.painter)
                    }
                )
                Column(
                    modifier = Modifier.weight(1F).padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = media.preferred(titleLanguage),
                        style = MaterialTheme.typography.titleLarge,
                        overflow = TextOverflow.Ellipsis,
                        softWrap = true,
                        modifier = Modifier.fillMaxWidth().weight(1F),
                        fontWeight = FontWeight.SemiBold
                    )
                    Episode(airing.episode)
                    Airing(airing.airingAt)
                }
            }
        }
    }
}