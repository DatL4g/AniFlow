package dev.datlag.aniflow.ui.navigation.screen.medium.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import dev.datlag.aniflow.SharedRes
import dev.datlag.aniflow.anilist.model.Medium
import dev.datlag.aniflow.common.popular
import dev.datlag.aniflow.common.rated
import dev.datlag.aniflow.model.asNullIf
import dev.datlag.aniflow.model.ifValue
import dev.datlag.aniflow.model.ifValueOrNull
import dev.datlag.tooling.decompose.lifecycle.collectAsStateWithLifecycle
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

@Composable
fun RatingSection(
    initialMedium: Medium,
    ratedFlow: Flow<Medium.Ranking?>,
    popularFlow: Flow<Medium.Ranking?>,
    scoreFlow: Flow<Int?>,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        val rated by ratedFlow.collectAsStateWithLifecycle(initialMedium.rated())
        val popular by popularFlow.collectAsStateWithLifecycle(initialMedium.popular())
        val score by scoreFlow.collectAsStateWithLifecycle(initialMedium.averageScore.asNullIf(-1))

        rated?.let {
            Column(
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(SharedRes.strings.rated),
                    style = MaterialTheme.typography.labelSmall
                )
                Text(
                    text = "#${it.rank}",
                    style = MaterialTheme.typography.displaySmall
                )
            }
        }
        popular?.let {
            Column(
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(SharedRes.strings.popularity),
                    style = MaterialTheme.typography.labelSmall
                )
                Text(
                    text = "#${it.rank}",
                    style = MaterialTheme.typography.displaySmall
                )
            }
        }
        score?.let {
            Column(
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(SharedRes.strings.score),
                    style = MaterialTheme.typography.labelSmall
                )
                Text(
                    text = "${it}%",
                    style = MaterialTheme.typography.displaySmall
                )
            }
        }
    }
}