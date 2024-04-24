package dev.datlag.aniflow

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.CompositionLocalProvider
import androidx.core.view.WindowCompat
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.backhandler.backHandler
import com.arkivanov.essenty.lifecycle.Lifecycle
import com.arkivanov.essenty.lifecycle.LifecycleOwner
import com.arkivanov.essenty.lifecycle.essentyLifecycle
import dev.datlag.aniflow.ui.navigation.RootComponent
import dev.datlag.tooling.decompose.lifecycle.LocalLifecycleOwner
import dev.datlag.tooling.safeCast
import org.kodein.di.DIAware
import org.kodein.di.instance
import org.publicvalue.multiplatform.oidc.appsupport.AndroidCodeAuthFlowFactory

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        enableEdgeToEdge()

        val di = applicationContext.safeCast<DIAware>()?.di ?: (application as DIAware).di
        val lifecycleOwner = object : LifecycleOwner {
            override val lifecycle: Lifecycle = essentyLifecycle()
        }
        val factory by di.instance<AndroidCodeAuthFlowFactory>()
        factory.registerActivity(this)

        val root = RootComponent(
            componentContext = DefaultComponentContext(
                lifecycle = lifecycleOwner.lifecycle,
                backHandler = backHandler()
            ),
            di = di
        )

        setContent {
            CompositionLocalProvider(
                LocalLifecycleOwner provides lifecycleOwner,
                LocalEdgeToEdge provides true
            ) {
                App(
                    di = di
                ) {
                    root.render()
                }
            }
        }
    }
}