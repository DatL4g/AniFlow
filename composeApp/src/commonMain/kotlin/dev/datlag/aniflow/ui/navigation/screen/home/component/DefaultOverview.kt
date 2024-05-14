package dev.datlag.aniflow.ui.navigation.screen.home.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.datlag.aniflow.anilist.model.Medium
import dev.datlag.aniflow.anilist.state.CollectionState
import dev.datlag.aniflow.settings.model.TitleLanguage
import dev.datlag.aniflow.ui.navigation.screen.home.component.default.MediumCard
import dev.datlag.tooling.decompose.lifecycle.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.Flow

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DefaultOverview(
    title: String,
    flow: Flow<CollectionState>,
    titleLanguage: TitleLanguage?,
    onMediumClick: (Medium) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        val state by flow.collectAsStateWithLifecycle(CollectionState.None)

        Row(
            modifier = Modifier.padding(start = 16.dp, end = 4.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
        }

        when (val current = state) {
            is CollectionState.None -> {
                Loading()
            }
            is CollectionState.Success -> {
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp)
                ) {
                    items(current.collection.toList(), key = { it.id }) {
                        MediumCard(
                            medium = it,
                            titleLanguage = titleLanguage,
                            modifier = Modifier
                                .width(200.dp)
                                .height(280.dp),
                            onClick = onMediumClick,
                        )
                    }
                }
            }
            is CollectionState.Error -> {
                Text("Could not load $title")
            }
        }
    }
}

@Composable
private fun Loading() {
    Box(
        modifier = Modifier.fillMaxWidth().height(280.dp),
        contentAlignment = Alignment.Center
    ) {
        LinearProgressIndicator(
            modifier = Modifier.fillMaxWidth(fraction = 0.2F).clip(CircleShape)
        )
    }
}