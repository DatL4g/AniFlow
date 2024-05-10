package dev.datlag.aniflow.ui.navigation.screen.home.dialog.settings.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.maxkeppeker.sheets.core.models.base.IconSource
import com.maxkeppeker.sheets.core.models.base.rememberUseCaseState
import com.maxkeppeler.sheets.option.OptionDialog
import com.maxkeppeler.sheets.option.models.DisplayMode
import com.maxkeppeler.sheets.option.models.Option
import com.maxkeppeler.sheets.option.models.OptionConfig
import com.maxkeppeler.sheets.option.models.OptionSelection
import dev.datlag.aniflow.common.toComposeColor
import dev.datlag.aniflow.common.toComposeString
import dev.datlag.aniflow.other.StateSaver
import dev.datlag.tooling.decompose.lifecycle.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.Flow
import dev.datlag.aniflow.settings.model.Color as SettingsColor
import dev.icerock.moko.resources.compose.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ColorSection(
    selectedColorFlow: Flow<SettingsColor?>,
    modifier: Modifier = Modifier,
    onChange: (SettingsColor?) -> Unit,
) {
    val selectedColor by selectedColorFlow.collectAsStateWithLifecycle(null)
    val temporaryColor by StateSaver.temporaryColor.collectAsStateWithLifecycle()
    val useCase = rememberUseCaseState(
        onFinishedRequest = {
            StateSaver.updateTemporaryColor(null)
        },
        onCloseRequest = {
            StateSaver.updateTemporaryColor(null)
        },
        onDismissRequest = {
            StateSaver.updateTemporaryColor(null)
        }
    )
    val colors = remember { SettingsColor.all.toList() }

    OptionDialog(
        state = useCase,
        selection = OptionSelection.Single(
            options = colors.map {
                Option(
                    icon = IconSource(
                        imageVector = Icons.Filled.Circle,
                        tint = it.toComposeColor()
                    ),
                    selected = if (temporaryColor != null) {
                        it == temporaryColor
                    } else {
                        it == selectedColor
                    },
                    titleText = stringResource(it.toComposeString()),
                    onClick = {
                        StateSaver.updateTemporaryColor(it)
                    }
                )
            },
            onSelectOption = { option, _ ->
                onChange(colors[option])
            }
        ),
        config = OptionConfig(
            mode = DisplayMode.GRID_VERTICAL,
            gridColumns = 4
        )
    )

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Palette,
            contentDescription = null,
        )
        Text(
            text = "Profile Color"
        )
        Spacer(modifier = Modifier.weight(1F))
        IconButton(
            onClick = { useCase.show() }
        ) {
            Icon(
                imageVector = Icons.Filled.Circle,
                contentDescription = null,
                tint = selectedColor?.toComposeColor() ?: LocalContentColor.current
            )
        }
    }
}