package dev.datlag.aniflow.ui.custom.speeddial

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp

@Composable
fun <T: FABItem> SubSpeedDialFABs(
    state: SpeedDialFABState,
    items: List<T>,
    showLabels: Boolean = true,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    labelContent: @Composable (T) -> Unit = {
        val backgroundColor = FloatingActionButtonDefaults.containerColor

        Surface(
            color = backgroundColor,
            shape = MaterialTheme.shapes.small,
            shadowElevation = 2.dp,
            onClick = { it.onClick() }
        ) {
            Text(
                text = it.label,
                color = contentColorFor(backgroundColor),
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
            )
        }
    },
    fabContent: @Composable (T) -> Unit = {
        SmallFloatingActionButton(
            modifier = Modifier.padding(4.dp),
            onClick = { it.onClick() },
            elevation = FloatingActionButtonDefaults.elevation(
                defaultElevation = 4.dp,
                hoveredElevation = 4.dp
            )
        ) {
            if (it.icon != null) {
                Icon(
                    modifier = it.modifier,
                    imageVector = it.icon,
                    contentDescription = it.label
                )
            } else if (it.painter != null) {
                Image(
                    modifier = it.modifier,
                    painter = it.painter,
                    contentDescription = it.label,
                    colorFilter = if (it.tint) ColorFilter.tint(LocalContentColor.current) else null
                )
            }
        }
    }
) {
    Column(
        horizontalAlignment = Alignment.End,
        verticalArrangement = verticalArrangement,
    ) {
        items.forEach { item ->
            AnimatedSmallFABWithLabel(
                state = state,
                showLabel = showLabels,
                labelContent = { labelContent(item) },
                fabContent = { fabContent(item) }
            )
        }
    }
}