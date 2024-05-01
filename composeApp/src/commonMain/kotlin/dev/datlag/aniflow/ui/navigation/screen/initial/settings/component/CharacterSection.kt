package dev.datlag.aniflow.ui.navigation.screen.initial.settings.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.PersonPin
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.maxkeppeker.sheets.core.models.base.rememberUseCaseState
import com.maxkeppeler.sheets.option.OptionDialog
import com.maxkeppeler.sheets.option.models.DisplayMode
import com.maxkeppeler.sheets.option.models.Option
import com.maxkeppeler.sheets.option.models.OptionConfig
import com.maxkeppeler.sheets.option.models.OptionSelection
import dev.datlag.aniflow.common.toComposeString
import dev.datlag.tooling.decompose.lifecycle.collectAsStateWithLifecycle
import dev.datlag.aniflow.settings.model.CharLanguage as SettingsChar
import kotlinx.coroutines.flow.Flow
import dev.icerock.moko.resources.compose.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharacterSection(
    characterFlow: Flow<SettingsChar?>,
    modifier: Modifier = Modifier,
    onChanged: (SettingsChar?) -> Unit
) {
    val selectedChar by characterFlow.collectAsStateWithLifecycle(null)
    val useCase = rememberUseCaseState()
    val languages = remember { SettingsChar.all.toList() }

    OptionDialog(
        state = useCase,
        selection = OptionSelection.Single(
            options = languages.map {
                Option(
                    selected = it == selectedChar,
                    titleText = stringResource(it.toComposeString())
                )
            },
            onSelectOption = { option, _ ->
                onChanged(languages[option])
            }
        ),
        config = OptionConfig(
            mode = DisplayMode.LIST,
        )
    )

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = Icons.Filled.PersonPin,
            contentDescription = null
        )
        Text(
            text = "Character Language"
        )
        Spacer(modifier = Modifier.weight(1F))
        IconButton(
            onClick = { useCase.show() }
        ) {
            Icon(
                imageVector = Icons.Default.ExpandMore,
                contentDescription = null
            )
        }
    }
}