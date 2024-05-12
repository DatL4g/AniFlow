package dev.datlag.aniflow.ui.navigation.screen.medium.component

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import com.maxkeppeker.sheets.core.models.base.Header
import com.maxkeppeker.sheets.core.models.base.IconSource
import com.maxkeppeker.sheets.core.models.base.UseCaseState
import com.maxkeppeker.sheets.core.models.base.rememberUseCaseState
import com.maxkeppeler.sheets.option.OptionDialog
import com.maxkeppeler.sheets.option.models.DisplayMode
import com.maxkeppeler.sheets.option.models.Option
import com.maxkeppeler.sheets.option.models.OptionConfig
import com.maxkeppeler.sheets.option.models.OptionSelection
import dev.datlag.aniflow.SharedRes
import dev.datlag.aniflow.other.Series
import dev.datlag.tooling.decompose.lifecycle.collectAsStateWithLifecycle
import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.coroutines.flow.StateFlow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BSDialog(
    state: UseCaseState,
    bsOptions: Collection<Series>
) {
    OptionDialog(
        state = state,
        config = OptionConfig(
            mode = DisplayMode.LIST
        ),
        selection = OptionSelection.Single(
            options = bsOptions.map {
                Option(
                    titleText = it.title
                )
            },
            onSelectOption = { option, _ ->

            }
        ),
        header = Header.Default(
            icon = IconSource(
                painter = painterResource(SharedRes.images.bs),
                tint = LocalContentColor.current
            ),
            title = stringResource(SharedRes.strings.bs)
        )
    )
}