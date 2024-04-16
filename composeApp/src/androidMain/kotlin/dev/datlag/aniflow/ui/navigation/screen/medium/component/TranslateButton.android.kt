package dev.datlag.aniflow.ui.navigation.screen.medium.component

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import dev.datlag.aniflow.SharedRes
import dev.icerock.moko.resources.compose.stringResource
import io.github.aakira.napier.Napier
import java.util.Locale

@Composable
actual fun TranslateButton(
    text: String,
    onTranslation: (String?) -> Unit,
) {
    val locale = remember { Locale.getDefault() }
    if (locale.language.equals(Locale.forLanguageTag("en").language, ignoreCase = true)) {
        Napier.e("Language is english")
        return
    }
    if (locale.toLanguageTag().equals("en", ignoreCase = true)) {
        Napier.e("LanguageTag is english")
        return
    }
    if (locale.isO3Language.equals("ENG", ignoreCase = true)) {
        Napier.e("Language ISO is english")
        return
    }

    val targetLanguage = remember(locale) {
        TranslateLanguage.fromLanguageTag(locale.toLanguageTag())
            ?: TranslateLanguage.fromLanguageTag(locale.language)
            ?: TranslateLanguage.fromLanguageTag(locale.isO3Language)
    }

    if (targetLanguage == null || targetLanguage == TranslateLanguage.ENGLISH) {
        Napier.e("TargetLanguage is: $targetLanguage, ${locale.toLanguageTag()}")
        return
    }

    val options = remember(targetLanguage) {
        TranslatorOptions.Builder()
            .setSourceLanguage(TranslateLanguage.ENGLISH)
            .setTargetLanguage(targetLanguage)
            .build()
    }
    val englishLocaleTranslator = remember(options) { Translation.getClient(options) }
    val downloadConditions = remember {
        DownloadConditions.Builder()
            .requireWifi()
            .build()
    }
    var enabled by remember { mutableStateOf(true) }
    var translated by remember { mutableStateOf(false) }
    var progress by remember { mutableStateOf(false) }

    TextButton(
        onClick = {
            if (translated) {
                translated = false
                onTranslation(null)
            } else {
                progress = true
                enabled = false

                englishLocaleTranslator
                    .downloadModelIfNeeded(downloadConditions)
                    .addOnFailureListener {
                        progress = false
                        enabled = false
                    }.addOnSuccessListener {
                        englishLocaleTranslator
                            .translate(text)
                            .addOnFailureListener {
                                progress = false
                                enabled = true

                                translated = false
                                onTranslation(null)
                            }.addOnSuccessListener {
                                progress = false
                                enabled = true

                                translated = true
                                onTranslation(it)
                            }
                    }
            }
        },
        enabled = enabled
    ) {
        if (progress) {
            CircularProgressIndicator(
                modifier = Modifier.size(ButtonDefaults.IconSize),
                strokeWidth = 2.dp,
                color = LocalContentColor.current
            )
        } else {
            Icon(
                modifier = Modifier.size(ButtonDefaults.IconSize),
                imageVector = Icons.Default.Translate,
                contentDescription = null
            )
        }
        Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
        Text(stringResource(SharedRes.strings.translate))
    }

    DisposableEffect(englishLocaleTranslator) {
        onDispose {
            englishLocaleTranslator.close()
        }
    }
}