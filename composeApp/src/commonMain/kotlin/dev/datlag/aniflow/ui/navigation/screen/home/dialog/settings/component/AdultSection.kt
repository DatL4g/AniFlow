package dev.datlag.aniflow.ui.navigation.screen.home.dialog.settings.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.NoAdultContent
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.datlag.aniflow.SharedRes
import dev.datlag.tooling.decompose.lifecycle.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.Flow
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun AdultSection(
    adultFlow: Flow<Boolean>,
    modifier: Modifier = Modifier,
    onChange: (Boolean) -> Unit
) {
    val adultContent by adultFlow.collectAsStateWithLifecycle(false)

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = Icons.Default.NoAdultContent,
            contentDescription = null,
        )
        Text(
            text = stringResource(SharedRes.strings.adult_content_setting)
        )
        Spacer(modifier = Modifier.weight(1F))
        Switch(
            checked = adultContent,
            onCheckedChange = onChange,
            thumbContent = {
                if (adultContent) {
                    Icon(
                        modifier = Modifier.size(SwitchDefaults.IconSize),
                        imageVector = Icons.Default.Check,
                        contentDescription = null
                    )
                }
            }
        )
    }
}