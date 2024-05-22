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
import dev.datlag.aniflow.anilist.state.HomeDefaultState
import dev.datlag.aniflow.settings.model.TitleLanguage
import dev.datlag.aniflow.ui.custom.ErrorContent
import dev.datlag.aniflow.ui.navigation.screen.home.component.default.MediumCard
import dev.datlag.tooling.decompose.lifecycle.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

@Composable
fun DefaultOverview(
    title: String,
    flow: StateFlow<HomeDefaultState>,
    titleLanguage: TitleLanguage?,
    onMediumClick: (Medium) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        val state by flow.collectAsStateWithLifecycle()

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
            is HomeDefaultState.None, is HomeDefaultState.Loading -> {
                Loading()
            }
            is HomeDefaultState.Success -> {
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
            is HomeDefaultState.Error -> {
                ErrorContent(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    horizontal = true
                )
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