package dev.datlag.aniflow.ui.navigation.screen.medium.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.maxkeppeker.sheets.core.models.base.Header
import com.maxkeppeker.sheets.core.models.base.IconSource
import com.maxkeppeker.sheets.core.models.base.UseCaseState
import com.maxkeppeker.sheets.core.models.base.rememberUseCaseState
import com.maxkeppeler.sheets.option.OptionDialog
import com.maxkeppeler.sheets.option.models.*
import com.maxkeppeler.sheets.option.models.DisplayMode
import dev.datlag.aniflow.SharedRes
import dev.datlag.aniflow.other.Series
import dev.datlag.tooling.decompose.lifecycle.collectAsStateWithLifecycle
import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.collections.immutable.ImmutableCollection
import kotlinx.coroutines.flow.StateFlow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BSDialog(
    state: UseCaseState,
    bsVersionCode: Int,
    bsVersionName: String?,
    bsOptions: ImmutableCollection<Series>,
    onSearch: suspend (String) -> Unit,
    onSelect: (Series) -> Unit
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
                onSelect(bsOptions.toList()[option])
            }
        ),
        header = Header.Default(
            icon = IconSource(
                painter = painterResource(SharedRes.images.bs),
                tint = LocalContentColor.current
            ),
            title = stringResource(SharedRes.strings.bs)
        ),
        body = OptionBody.Custom {
            var value by remember { mutableStateOf("") }

            LaunchedEffect(value) {
                onSearch(value)
            }

            Column(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = value,
                    onValueChange = { value = it },
                    placeholder = {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = stringResource(SharedRes.strings.search),
                            textAlign = TextAlign.Center
                        )
                    },
                    shape = MaterialTheme.shapes.medium,
                    textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center)
                )
                if (bsVersionCode < 600) {
                    Text(text = stringResource(SharedRes.strings.bs_version_requirement))
                }
            }
        }
    )
}