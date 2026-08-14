package net.sircesarium.qtz.api.sound.type

import java.util.function.Supplier
import net.minecraft.resources.ResourceLocation
import net.minecraft.sounds.SoundEvent
import net.neoforged.neoforge.registries.DeferredHolder
import net.sircesarium.qtz.api.sound.SoundDef
import net.sircesarium.qtz.api.sound.SoundRegistry
import net.sircesarium.qtz.utils.toSnakeCase

/**
 * Registers a variable-range sound event named after the property (converted
 * to snake_case).
 *
 * | Property | Example |
 * |---|---|
 * | Property name → registry name | `by sound()` → `"spark"` |
 *
 * ```
 * class ModSounds : SoundRegistry("modid") {
 *     val spark by sound()
 * }
 * ```
 *
 * The property resolves to a [DeferredHolder]; call `get()` for the
 * [SoundEvent].
 *
 * The `sounds.json` entry is generated automatically by datagen; only the
 * `assets/<modid>/sounds/<name>.ogg` audio file must exist, matching the
 * registry name.
 */
fun SoundRegistry.sound() = bindName { rawName ->
    val name = rawName.toSnakeCase()

    soundDefs.add(SoundDef(name))

    val holder = sounds.register(name, Supplier {
        SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(modId, name))
    })

    holder
}

/**
 * Registers a fixed-range sound event named after the property (converted to
 * snake_case).
 *
 * | Property | Example |
 * |---|---|
 * | Property name → registry name | `by sound(32f)` → `"boom"` |
 * | Range | `range` | `32f` |
 *
 * ```
 * class ModSounds : SoundRegistry("modid") {
 *     val boom by sound(32f)
 * }
 * ```
 *
 * The property resolves to a [DeferredHolder]; call `get()` for the
 * [SoundEvent].
 *
 * The `sounds.json` entry is generated automatically by datagen; only the
 * `assets/<modid>/sounds/<name>.ogg` audio file must exist, matching the
 * registry name.
 */
fun SoundRegistry.sound(
    range: Float,
) = bindName { rawName ->
    val name = rawName.toSnakeCase()

    soundDefs.add(SoundDef(name))

    val holder = sounds.register(name, Supplier {
        SoundEvent.createFixedRangeEvent(ResourceLocation.fromNamespaceAndPath(modId, name), range)
    })

    holder
}