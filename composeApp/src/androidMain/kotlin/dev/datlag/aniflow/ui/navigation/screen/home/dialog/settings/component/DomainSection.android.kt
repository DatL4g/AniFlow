package dev.datlag.aniflow.ui.navigation.screen.home.dialog.settings.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import dev.datlag.aniflow.SharedRes
import dev.datlag.aniflow.other.DomainVerifier
import dev.datlag.tooling.decompose.lifecycle.collectAsStateWithLifecycle
import dev.icerock.moko.resources.compose.stringResource

@Composable
actual fun DomainSection(modifier: Modifier) {
    if (DomainVerifier.supported) {
        val context = LocalContext.current
        val verified by DomainVerifier.verified.collectAsStateWithLifecycle()

        SideEffect {
            DomainVerifier.verify(context)
        }

        Row(
            modifier = modifier,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.Share,
                contentDescription = null,
            )
            Text(
                text = stringResource(SharedRes.strings.open_links)
            )
            Spacer(modifier = Modifier.weight(1F))
            Switch(
                checked = verified,
                onCheckedChange = {
                    DomainVerifier.enable(context)
                },
                enabled = !verified,
                thumbContent = {
                    if (verified) {
                        Icon(
                            modifier = Modifier.size(SwitchDefaults.IconSize),
                            imageVector = Icons.Rounded.Check,
                            contentDescription = null
                        )
                    }
                }
            )
        }
    }
}