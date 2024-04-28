package dev.datlag.aniflow.settings

import androidx.datastore.core.okio.OkioSerializer
import dev.datlag.aniflow.settings.model.AppSettings
import dev.datlag.tooling.async.suspendCatching
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.protobuf.ProtoBuf
import okio.BufferedSink
import okio.BufferedSource

data object AppSettingsSerializer : OkioSerializer<AppSettings> {
    override val defaultValue: AppSettings = AppSettings(
        color = null
    )

    @OptIn(ExperimentalSerializationApi::class)
    private val protobuf = ProtoBuf {
        encodeDefaults = true
    }

    @OptIn(ExperimentalSerializationApi::class)
    override suspend fun readFrom(source: BufferedSource): AppSettings {
        return suspendCatching {
            protobuf.decodeFromByteArray<AppSettings>(source.readByteArray())
        }.getOrNull() ?: defaultValue
    }

    @OptIn(ExperimentalSerializationApi::class)
    override suspend fun writeTo(t: AppSettings, sink: BufferedSink) {
        val newSink = suspendCatching {
            sink.write(protobuf.encodeToByteArray(t))
        }.getOrNull() ?: sink

        suspendCatching {
            newSink.emit()
        }.getOrNull()
    }
}