package dev.datlag.aniflow.ui.navigation.screen.medium.component

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.datlag.aniflow.SharedRes
import dev.datlag.aniflow.anilist.model.Character
import dev.datlag.tooling.decompose.lifecycle.collectAsStateWithLifecycle
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.coroutines.flow.StateFlow

@Composable
fun CharacterSection(
    characterFlow: StateFlow<Collection<Character>>,
    modifier: Modifier = Modifier,
    onClick: (Character) -> Unit
) {
    val characters by characterFlow.collectAsStateWithLifecycle()

    if (characters.isNotEmpty()) {
        Column(
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                modifier = Modifier.padding(top = 16.dp).padding(horizontal = 16.dp),
                text = stringResource(SharedRes.strings.characters),
                style = MaterialTheme.typography.headlineSmall
            )
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(top = 8.dp, start = 16.dp, end = 16.dp, bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally)
            ) {
                items(characters.toList()) { char ->
                    CharacterCard(
                        char = char,
                        modifier = Modifier.width(96.dp).height(200.dp),
                        onClick = onClick
                    )
                }
            }
        }
    }
}