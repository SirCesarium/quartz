package net.sircesarium.qtz.api.particle.type

import java.util.function.Supplier
import net.minecraft.core.particles.ParticleOptions
import net.minecraft.core.particles.ParticleType
import net.minecraft.core.particles.SimpleParticleType
import net.neoforged.neoforge.registries.DeferredHolder
import net.sircesarium.qtz.api.particle.ParticleRegistry
import net.sircesarium.qtz.utils.toSnakeCase

/**
 * Registers a sprite-based particle with no extra data, named after the
 * property (converted to snake_case).
 *
 * | Property | Example |
 * |---|---|
 * | Property name → registry name | `by particle()` → `"sparkle"` |
 *
 * ```
 * class ModParticles : ParticleRegistry("modid") {
 *     val sparkle by particle()
 * }
 * ```
 *
 * The property resolves to a [DeferredHolder]; call `get()` for the
 * [SimpleParticleType].
 *
 * The particle provider is registered separately on the client, using the
 * holder, on the `RegisterParticleProvidersEvent`.
 */
fun ParticleRegistry.particle() = bindName { rawName ->
    val holder = particles.register(rawName.toSnakeCase(), Supplier { SimpleParticleType(false) })

    holder
}

/**
 * Registers a particle with custom data, named after the property (converted
 * to snake_case).
 *
 * | Property | Example |
 * |---|---|
 * | Property name → registry name | `by particle(::...)` → `"shock_wave"` |
 * | Particle type constructor | `factory` | `::ShockWaveParticleType` |
 *
 * ```
 * class ShockWaveParticleType : ParticleType<ShockWaveParticleOptions>(
 *     false, ShockWaveParticleOptions.CODEC, ShockWaveParticleOptions.STREAM_CODEC,
 * )
 *
 * class ModParticles : ParticleRegistry("modid") {
 *     val shockWave by particle(::ShockWaveParticleType)
 * }
 * ```
 *
 * The property resolves to a [DeferredHolder]; call `get()` for the
 * [ParticleType].
 *
 * The particle provider is registered separately on the client, using the
 * holder, on the `RegisterParticleProvidersEvent`.
 */
fun <T : ParticleOptions> ParticleRegistry.particle(
    factory: () -> ParticleType<T>,
) = bindName { rawName ->
    val holder = particles.register(rawName.toSnakeCase(), Supplier { factory() })

    holder
}