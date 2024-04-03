package dev.datlag.aniflow.settings

import androidx.datastore.core.okio.OkioSerializer
import dev.datlag.tooling.async.suspendCatching
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.protobuf.ProtoBuf
import okio.BufferedSink
import okio.BufferedSource

data object UserSettingsSerializer : OkioSerializer<UserSettings> {
    override val defaultValue: UserSettings = UserSettings()

    @OptIn(ExperimentalSerializationApi::class)
    private val protobuf = ProtoBuf {
        encodeDefaults = true
    }

    @OptIn(ExperimentalSerializationApi::class)
    override suspend fun readFrom(source: BufferedSource): UserSettings {
        return suspendCatching {
            protobuf.decodeFromByteArray<UserSettings>(source.readByteArray())
        }.getOrNull() ?: defaultValue
    }

    @OptIn(ExperimentalSerializationApi::class)
    override suspend fun writeTo(t: UserSettings, sink: BufferedSink) {
        val newSink = suspendCatching {
            sink.write(protobuf.encodeToByteArray(t))
        }.getOrNull() ?: sink

        suspendCatching {
            newSink.emit()
        }.getOrNull()
    }
}