package net.sircesarium.qtz.api.datacomponent.type

import java.util.function.Supplier
import net.minecraft.core.component.DataComponentType
import net.neoforged.neoforge.registries.DeferredHolder
import net.sircesarium.qtz.api.datacomponent.DataComponentBuilder
import net.sircesarium.qtz.api.datacomponent.DataComponentRegistry
import net.sircesarium.qtz.utils.toSnakeCase

/**
 * Registers a data component type named after the property (converted to
 * snake_case).
 *
 * | Property | Config block | Example |
 * |---|---|---|
 * | Property name → registry name | `by dataComponent<...>()` → `"energy"` |
 * | Codec | `codec` | `codec(Energy.CODEC)` |
 * | Stream codec | `streamCodec` | `streamCodec(Energy.STREAM_CODEC)` |
 * | Cache encoding | `cacheEncoding` | `cacheEncoding()` |
 *
 * ```
 * class ModDataComponents : DataComponentRegistry("modid") {
 *     val energy by dataComponent<Energy> {
 *         codec(Energy.CODEC)
 *         streamCodec(Energy.STREAM_CODEC)
 *     }
 * }
 * ```
 *
 * The property resolves to a [DeferredHolder]; call `get()` for the
 * [DataComponentType]. The stream codec is optional; when omitted it is
 * derived from the codec.
 */
fun <T> DataComponentRegistry.dataComponent(
    config: DataComponentBuilder<T>.() -> Unit = {},
) = bindName { rawName ->
    val name = rawName.toSnakeCase()
    val builder = DataComponentBuilder<T>().apply(config)

    val holder = dataComponents.register(name, Supplier { builder.build() })

    holder
}