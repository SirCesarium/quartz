package net.sircesarium.qtz.api.effect.type

import java.util.function.Supplier
import net.minecraft.world.effect.MobEffect
import net.neoforged.neoforge.registries.DeferredHolder
import net.sircesarium.qtz.api.effect.EffectRegistry
import net.sircesarium.qtz.utils.toSnakeCase

/**
 * Registers a mob effect named after the property (converted to snake_case).
 *
 * | Property | Example |
 * |---|---|
 * | Property name → registry name | `by effect(...)` → `"shocked"` |
 * | Effect constructor | `::ShockedEffect` |
 *
 * The effect class defines its own category and color via the constructor, as
 * vanilla does; the DSL only wires the registration.
 *
 * ```
 * class ShockedEffect : MobEffect(MobEffectCategory.HARMFUL, rgb(255, 200, 0)) {
 *     override fun applyEffectTick(entity: LivingEntity, amplifier: Int) {
 *         // ...
 *     }
 * }
 *
 * class ModEffects : EffectRegistry("modid") {
 *     val shocked by effect(::ShockedEffect)
 * }
 * ```
 *
 * The property resolves to a [DeferredHolder]; call `get()` for the [MobEffect].
 */
fun <E : MobEffect> EffectRegistry.effect(
    constructor: () -> E,
) = bindName { rawName ->
    val holder = effects.register(rawName.toSnakeCase(), Supplier { constructor() })

    holder
}