package dev.datlag.aniflow.ui.navigation.screen.medium.component

import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.rounded.ExpandLess
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.datlag.aniflow.SharedRes
import dev.datlag.aniflow.anilist.model.Medium
import dev.datlag.aniflow.common.htmlToAnnotatedString
import dev.datlag.tooling.compose.onClick
import dev.datlag.tooling.decompose.lifecycle.collectAsStateWithLifecycle
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlin.math.max

@Composable
fun DescriptionSection(
    initialMedium: Medium,
    descriptionFlow: Flow<String?>,
    translatedDescriptionFlow: StateFlow<String?>,
    modifier: Modifier = Modifier,
    onTranslation: (String?) -> Unit
) {
    val description by descriptionFlow.collectAsStateWithLifecycle(initialMedium.description)

    if (!description.isNullOrBlank()) {
        Column(
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val translatedDescription by translatedDescriptionFlow.collectAsStateWithLifecycle()
            var descriptionExpandable by remember(description) { mutableStateOf(false) }
            var descriptionExpanded by remember(description) { mutableStateOf(false) }

            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp).padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    modifier = Modifier.weight(1F),
                    text = stringResource(SharedRes.strings.description),
                    style = MaterialTheme.typography.headlineSmall
                )
                TranslateButton(description ?: "") { text ->
                    onTranslation(text)
                }
            }

            val animatedLines by animateIntAsState(
                targetValue = if (descriptionExpanded) {
                    Int.MAX_VALUE
                } else {
                    3
                },
                animationSpec = tween()
            )

            SelectionContainer {
                Text(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    text = (translatedDescription ?: description)!!.htmlToAnnotatedString(),
                    maxLines = max(animatedLines, 1),
                    softWrap = true,
                    overflow = TextOverflow.Ellipsis,
                    onTextLayout = { result ->
                        if (!descriptionExpanded) {
                            descriptionExpandable = result.hasVisualOverflow
                        }
                    }
                )
            }
            if (descriptionExpandable) {
                IconButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        descriptionExpanded = !descriptionExpanded
                    }
                ) {
                    val icon = if (descriptionExpanded) {
                        Icons.Rounded.ExpandLess
                    } else {
                        Icons.Rounded.ExpandMore
                    }

                    Icon(
                        imageVector = icon,
                        contentDescription = null
                    )
                }
            }
        }
    }
}