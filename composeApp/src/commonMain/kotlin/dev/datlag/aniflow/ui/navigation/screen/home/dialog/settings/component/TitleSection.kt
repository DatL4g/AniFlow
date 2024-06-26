package dev.datlag.aniflow.ui.navigation.screen.home.dialog.settings.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material.icons.rounded.Title
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
import com.maxkeppeker.sheets.core.models.base.Header
import com.maxkeppeker.sheets.core.models.base.IconSource
import com.maxkeppeker.sheets.core.models.base.rememberUseCaseState
import com.maxkeppeler.sheets.option.OptionDialog
import com.maxkeppeler.sheets.option.models.DisplayMode
import com.maxkeppeler.sheets.option.models.Option
import com.maxkeppeler.sheets.option.models.OptionConfig
import com.maxkeppeler.sheets.option.models.OptionSelection
import dev.datlag.aniflow.SharedRes
import dev.datlag.aniflow.common.toComposeString
import dev.datlag.aniflow.settings.model.TitleLanguage as SettingsTitle
import dev.datlag.tooling.decompose.lifecycle.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.Flow
import dev.icerock.moko.resources.compose.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TitleSection(
    titleFlow: Flow<SettingsTitle?>,
    modifier: Modifier = Modifier,
    onChange: (SettingsTitle?) -> Unit,
) {
    val selectedTitle by titleFlow.collectAsStateWithLifecycle(null)
    val useCase = rememberUseCaseState()
    val languages = remember { SettingsTitle.all.toList() }

    OptionDialog(
        state = useCase,
        selection = OptionSelection.Single(
            options = languages.map {
                Option(
                    selected = it == selectedTitle,
                    titleText = stringResource(it.toComposeString())
                )
            },
            onSelectOption = { option, _ ->
                onChange(languages[option])
            }
        ),
        config = OptionConfig(
            mode = DisplayMode.LIST,
        ),
        header = Header.Default(
            icon = IconSource(imageVector = Icons.Rounded.Title),
            title = stringResource(SharedRes.strings.title_language),
        )
    )

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = Icons.Rounded.Title,
            contentDescription = null
        )
        Text(
            text = stringResource(SharedRes.strings.title_language)
        )
        Spacer(modifier = Modifier.weight(1F))
        IconButton(
            onClick = { useCase.show() }
        ) {
            Icon(
                imageVector = Icons.Rounded.ExpandMore,
                contentDescription = null
            )
        }
    }
}