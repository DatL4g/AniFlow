package dev.datlag.aniflow.ui.navigation.screen.settings.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.rememberAsyncImagePainter
import com.mikepenz.markdown.m3.Markdown
import dev.datlag.aniflow.LocalDI
import dev.datlag.aniflow.SharedRes
import dev.datlag.aniflow.anilist.model.User
import dev.datlag.aniflow.other.UserHelper
import dev.datlag.tooling.compose.onClick
import dev.datlag.tooling.decompose.lifecycle.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.Flow
import dev.icerock.moko.resources.compose.stringResource
import dev.icerock.moko.resources.compose.painterResource
import org.kodein.di.instance

@Composable
fun UserSection(
    userFlow: Flow<User?>,
    loginUri: String,
    modifier: Modifier = Modifier,
) {
    val user by userFlow.collectAsStateWithLifecycle(null)

    Column(
        modifier = modifier.padding(bottom = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val uriHandler = LocalUriHandler.current

        AsyncImage(
            modifier = Modifier.size(96.dp).clip(CircleShape).onClick(
                enabled = user == null,
            ) {
                uriHandler.openUri(loginUri)
            },
            model = user?.avatar?.large,
            contentDescription = null,
            error = rememberAsyncImagePainter(
                model = user?.avatar?.medium,
                contentScale = ContentScale.Crop,
                error = painterResource(SharedRes.images.anilist)
            ),
            contentScale = ContentScale.Crop,
            alignment = Alignment.Center
        )
        Text(
            text = user?.name ?: stringResource(SharedRes.strings.settings),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Markdown(
            modifier = Modifier.padding(bottom = 16.dp),
            content = user?.description ?: "Login with [AniList](${loginUri})"
        )
    }
}