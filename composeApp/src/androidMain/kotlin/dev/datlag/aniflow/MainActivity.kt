package dev.datlag.aniflow

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.CompositionLocalProvider
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.defaultComponentContext
import com.arkivanov.decompose.handleDeepLink
import com.arkivanov.essenty.backhandler.backHandler
import com.arkivanov.essenty.lifecycle.Lifecycle
import com.arkivanov.essenty.lifecycle.LifecycleOwner
import com.arkivanov.essenty.lifecycle.essentyLifecycle
import com.arkivanov.essenty.statekeeper.stateKeeper
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.ktx.requestAppUpdateInfo
import dev.datlag.aniflow.other.BurningSeriesResolver
import dev.datlag.aniflow.other.ConsentInfo
import dev.datlag.aniflow.other.DomainVerifier
import dev.datlag.aniflow.other.LocalConsentInfo
import dev.datlag.aniflow.other.UpdateManager
import dev.datlag.aniflow.other.UserHelper
import dev.datlag.aniflow.ui.navigation.RootComponent
import dev.datlag.tooling.compose.launchIO
import dev.datlag.tooling.decompose.lifecycle.LocalLifecycleOwner
import dev.datlag.tooling.safeCast
import io.github.aakira.napier.Napier
import org.kodein.di.DIAware
import org.kodein.di.instance
import org.kodein.di.instanceOrNull

class MainActivity : AppCompatActivity() {

    private lateinit var root: RootComponent

    @OptIn(ExperimentalDecomposeApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()

        WindowCompat.setDecorFitsSystemWindows(window, false)
        enableEdgeToEdge()

        val di = applicationContext.safeCast<DIAware>()?.di ?: (application as DIAware).di
        val lifecycleOwner = object : LifecycleOwner {
            override val lifecycle: Lifecycle = essentyLifecycle()
        }

        root = RootComponent(
            componentContext = DefaultComponentContext(
                lifecycle = lifecycleOwner.lifecycle,
                backHandler = backHandler()
            ),
            di = di
        )

        DomainVerifier.verify(this)
        UpdateManager.checkForUpdates(this) { manager, info, type ->
            manager.startUpdateFlow(info, this, AppUpdateOptions.defaultOptions(type))
        }

        val consentInfo = ConsentInfo(this)

        setContent {
            CompositionLocalProvider(
                LocalLifecycleOwner provides lifecycleOwner,
                LocalEdgeToEdge provides true,
                LocalConsentInfo provides consentInfo
            ) {
                App(
                    di = di
                ) {
                    root.render()
                }
            }
        }
    }

    @SuppressLint("MissingSuperCall")
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        val uri = intent.data ?: return
        val itemId = uri.pathSegments?.firstNotNullOfOrNull { it.trim().toIntOrNull() }
        if (itemId != null && ::root.isInitialized) {
            root.onDeepLink(itemId)
            return
        }

        val accessToken = uri.getFragmentOrQueryParameter("access_token")
        if (accessToken.isNullOrBlank()) {
            return
        }

        root.onLogin(
            accessToken = accessToken,
            expiresIn = uri.getFragmentOrQueryParameter("expires_in")?.toIntOrNull()
        )
    }

    override fun onDestroy() {
        super.onDestroy()

        val di = applicationContext.safeCast<DIAware>()?.di ?: (application as DIAware).di
        val resolver by di.instanceOrNull<BurningSeriesResolver>()

        DomainVerifier.verify(this)
        resolver?.close()
    }

    override fun onStart() {
        super.onStart()

        DomainVerifier.verify(this)
    }

    override fun onResume() {
        super.onResume()

        DomainVerifier.verify(this)
        UpdateManager.checkResume(this) { manager, info ->
            manager.startUpdateFlow(info, this, AppUpdateOptions.defaultOptions(AppUpdateType.IMMEDIATE))
        }
    }

    override fun onPause() {
        super.onPause()

        DomainVerifier.verify(this)
    }

    override fun onStop() {
        super.onStop()

        DomainVerifier.verify(this)
    }

    override fun onRestart() {
        super.onRestart()

        DomainVerifier.verify(this)
    }

    private fun Uri.getFragmentOrQueryParameter(param: String): String? {
        return this.fragment.getFragmentParameter(param) ?: getQueryParameter(param)?.ifBlank { null }
    }

    private fun String?.getFragmentParameter(param: String): String? {
        val keys = this?.split("&").orEmpty()
        keys.forEach { key ->
            val values = key.split("=")
            if (values[0] == param) {
                return values.getOrNull(1)?.ifBlank { null }
            }
        }
        return null
    }
}