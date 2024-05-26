package dev.datlag.aniflow.ui.navigation.screen.home.dialog.settings.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import dev.datlag.aniflow.SharedRes
import dev.datlag.aniflow.other.Constants
import dev.datlag.aniflow.other.LocalConsentInfo
import dev.icerock.moko.resources.compose.stringResource

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PrivacySection(
    modifier: Modifier = Modifier
) {
    FlowRow(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
    ) {
        val consentInfo = LocalConsentInfo.current
        val uriHandler = LocalUriHandler.current

        if (consentInfo.privacy) {
            TextButton(
                onClick = {
                    consentInfo.showPrivacyForm()
                }
            ) {
                Text(text = stringResource(SharedRes.strings.edit_consent))
            }
        }
        TextButton(
            onClick = {
                uriHandler.openUri(Constants.PRIVACY_POLICY)
            }
        ) {
            Text(text = stringResource(SharedRes.strings.policy))
        }
        TextButton(
            onClick = {
                uriHandler.openUri(Constants.TERMS_CONDITIONS)
            }
        ) {
            Text(text = stringResource(SharedRes.strings.terms_conditions))
        }
    }
}