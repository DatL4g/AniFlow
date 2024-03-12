package dev.datlag.aniflow.ui.navigation.screen.initial.home.component

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import net.pearx.kasechange.toTitleCase

@Composable
fun GenreChip(
    label: String,
    modifier: Modifier = Modifier,
    labelColor: Color = MaterialTheme.colorScheme.onPrimaryContainer,
    containerColor: Color = MaterialTheme.colorScheme.primaryContainer
) {
    SuggestionChip(
        onClick = { },
        modifier = modifier.wrapContentHeight().height(32.dp),
        border = null,
        colors = SuggestionChipDefaults.suggestionChipColors(
            labelColor = labelColor,
            containerColor = containerColor
        ),
        label = {
            Text(
                text = label.toTitleCase(),
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Clip,
                softWrap = true
            )
        }
    )
}