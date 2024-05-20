package dev.datlag.aniflow.ui.navigation.screen.discover.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.datlag.aniflow.ui.navigation.screen.discover.DiscoverType
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun TypeCard(
    type: DiscoverType,
    modifier: Modifier = Modifier,
    onClick: (DiscoverType) -> Unit
) {
    ElevatedCard(
        onClick = { onClick(type) },
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = type.icon,
                contentDescription = null
            )
            Text(text = stringResource(type.text))
        }
    }
}