package dev.datlag.aniflow.ui.navigation.screen.home.dialog.about

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.mikepenz.aboutlibraries.Libs
import dev.datlag.aniflow.LocalEdgeToEdge
import dev.datlag.aniflow.SharedRes
import dev.datlag.aniflow.common.merge
import dev.datlag.aniflow.ui.navigation.screen.home.dialog.about.component.LibraryCard
import dev.icerock.moko.resources.compose.readTextAsState
import dev.icerock.moko.resources.compose.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutDialog(component: AboutComponent) {
    val sheetState = rememberModalBottomSheetState()
    val (insets, bottomPadding) = if (LocalEdgeToEdge.current) {
        WindowInsets(
            left = 0,
            top = 0,
            right = 0,
            bottom = 0
        ) to BottomSheetDefaults.windowInsets.only(WindowInsetsSides.Bottom).asPaddingValues()
    } else {
        BottomSheetDefaults.windowInsets to PaddingValues()
    }

    ModalBottomSheet(
        onDismissRequest = component::dismiss,
        windowInsets = insets,
        sheetState = sheetState
    ) {
        val libsJson by SharedRes.assets.aboutlibraries_json.readTextAsState()
        val libs = remember(libsJson) {
            libsJson?.let { json ->
                Libs.Builder().withJson(json).build()
            }
        }
        val libsList = remember(libs) {
            libs?.libraries.orEmpty()
        }

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = bottomPadding.merge(PaddingValues(16.dp)),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Text(
                    modifier = Modifier.fillParentMaxWidth(),
                    text = stringResource(SharedRes.strings.open_source_licenses),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }
            item {
                Text(
                    modifier = Modifier.fillParentMaxWidth().padding(vertical = 16.dp),
                    text = stringResource(SharedRes.strings.open_source_licenses_text),
                    textAlign = TextAlign.Center
                )
            }
            items(libsList, key = { it.uniqueId }) {
                LibraryCard(it)
            }
        }
    }
}