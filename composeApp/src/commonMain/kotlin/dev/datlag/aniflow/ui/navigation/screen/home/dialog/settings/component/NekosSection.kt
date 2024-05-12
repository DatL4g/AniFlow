package dev.datlag.aniflow.ui.navigation.screen.home.dialog.settings.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import dev.datlag.aniflow.SharedRes
import dev.datlag.tooling.compose.onClick
import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun NekosSection(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    var clicked by remember { mutableStateOf(0) }

    Row(
        modifier = modifier
            .defaultMinSize(minHeight = ButtonDefaults.MinHeight)
            .clip(MaterialTheme.shapes.medium)
            .onClick {
                if (clicked >= 99) {
                    onClick()
                } else {
                    clicked++
                }
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Image(
            modifier = Modifier.size(24.dp),
            painter = painterResource(SharedRes.images.cat_filled),
            contentDescription = null,
            colorFilter = ColorFilter.tint(LocalContentColor.current)
        )
        Text(text = stringResource(SharedRes.strings.nekos_api))
        AnimatedVisibility(
            visible = clicked >= 1,
        ) {
            Badge(
                containerColor = MaterialTheme.colorScheme.tertiaryContainer
            ) {
                Text(text = "$clicked")
            }
        }
    }
}