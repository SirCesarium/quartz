package net.sircesarium.qtz.api.datacomponent

import com.mojang.serialization.Codec
import net.minecraft.core.component.DataComponentType
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec

class DataComponentBuilder<T> {
    @PublishedApi internal var codec: Codec<T>? = null
    @PublishedApi internal var streamCodec: StreamCodec<RegistryFriendlyByteBuf, T>? = null
    @PublishedApi internal var cacheEncoding: Boolean = false

    fun codec(codec: Codec<T>) {
        this.codec = codec
    }

    fun streamCodec(streamCodec: StreamCodec<RegistryFriendlyByteBuf, T>) {
        this.streamCodec = streamCodec
    }

    fun cacheEncoding() {
        cacheEncoding = true
    }

    fun build(): DataComponentType<T> {
        var builder = DataComponentType.builder<T>()
            .persistent(requireNotNull(codec) { "Missing Codec for data component" })

        val syncCodec = streamCodec
        if (syncCodec != null) {
            builder = builder.networkSynchronized(syncCodec)
        }
        if (cacheEncoding) {
            builder = builder.cacheEncoding()
        }

        return builder.build()
    }
}