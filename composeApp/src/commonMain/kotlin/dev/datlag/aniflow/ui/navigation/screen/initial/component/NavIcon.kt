package dev.datlag.aniflow.ui.navigation.screen.initial.component

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.datlag.aniflow.ui.navigation.screen.initial.InitialComponent
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun NavIcon(item: InitialComponent.PagerItem) {
    Icon(
        imageVector = item.icon,
        contentDescription = stringResource(item.label),
        modifier = Modifier.size(24.dp),
        tint = LocalContentColor.current
    )
}