package dev.datlag.aniflow.ui.navigation.screen.home.dialog.settings.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Savings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import com.maxkeppeker.sheets.core.models.base.Header
import com.maxkeppeker.sheets.core.models.base.IconSource
import com.maxkeppeker.sheets.core.models.base.rememberUseCaseState
import com.maxkeppeler.sheets.option.OptionDialog
import com.maxkeppeler.sheets.option.models.DisplayMode
import com.maxkeppeler.sheets.option.models.Option
import com.maxkeppeler.sheets.option.models.OptionConfig
import com.maxkeppeler.sheets.option.models.OptionSelection
import dev.datlag.aniflow.SharedRes
import dev.datlag.aniflow.other.Constants
import dev.datlag.tooling.compose.onClick
import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.compose.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SponsorSection(
    modifier: Modifier = Modifier,
) {
    val sponsorDialog = rememberUseCaseState()
    val uriHandler = LocalUriHandler.current

    OptionDialog(
        state = sponsorDialog,
        config = OptionConfig(
            mode = DisplayMode.LIST
        ),
        selection = OptionSelection.Single(
            options = listOf(
                Option(
                    icon = IconSource(painter = painterResource(SharedRes.images.github)),
                    titleText = stringResource(SharedRes.strings.github)
                ),
                Option(
                    icon = IconSource(painter = painterResource(SharedRes.images.polar)),
                    titleText = stringResource(SharedRes.strings.polar)
                ),
                Option(
                    icon = IconSource(painter = painterResource(SharedRes.images.patreon)),
                    titleText = stringResource(SharedRes.strings.patreon)
                )
            ),
            onSelectOption = { option, _ ->
                when (option) {
                    0 -> uriHandler.openUri(Constants.Sponsor.GITHUB)
                    1 -> uriHandler.openUri(Constants.Sponsor.POLAR)
                    2 -> uriHandler.openUri(Constants.Sponsor.PATREON)
                }
            }
        ),
        header = Header.Default(
            title = stringResource(SharedRes.strings.sponsor),
            icon = IconSource(imageVector = Icons.Rounded.Savings)
        )
    )

    Row(
        modifier = modifier
            .defaultMinSize(minHeight = ButtonDefaults.MinHeight)
            .clip(MaterialTheme.shapes.medium)
            .onClick {
                sponsorDialog.show()
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = Icons.Rounded.Savings,
            contentDescription = null,
        )
        Text(text = stringResource(SharedRes.strings.sponsor))
    }
}