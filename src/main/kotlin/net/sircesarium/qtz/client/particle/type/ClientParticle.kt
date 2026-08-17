package net.sircesarium.qtz.client.particle.type

import java.util.function.Supplier
import net.minecraft.client.particle.ParticleEngine
import net.minecraft.client.particle.ParticleProvider
import net.minecraft.core.particles.ParticleOptions
import net.minecraft.core.particles.ParticleType
import net.sircesarium.qtz.client.particle.ClientParticleRegistrar

/**
 * Registers a sprite-set particle provider for the given particle type.
 *
 * | Property | Example |
 * |---|---|
 * | Particle type | `ModParticles.sparkle` |
 * | Provider factory | `{ sprites -> SparkleParticle.Factory(sprites) }` |
 *
 * The factory receives the particle's [SpriteSet][net.minecraft.client.particle.SpriteSet]
 * and returns the [ParticleProvider]:
 *
 * ```
 * object ModParticlesClient : ClientParticleRegistrar {
 *     val sparkle by spriteSet(ModParticles.sparkle) { sprites -> SparkleParticle.Factory(sprites) }
 * }
 * ```
 *
 * The property resolves to the [ParticleType] the provider is bound to.
 */
fun <T : ParticleOptions> ClientParticleRegistrar.spriteSet(
    type: Supplier<out ParticleType<T>>,
    registration: ParticleEngine.SpriteParticleRegistration<T>,
) = bindName { _ ->
    registrations.add { event ->
        event.registerSpriteSet(type.get(), registration)
    }

    type.get()
}

/**
 * Registers a special particle provider (no sprite) for the given particle
 * type.
 *
 * | Property | Example |
 * |---|---|
 * | Particle type | `ModParticles.shockWave` |
 * | Provider | `::ShockWaveParticle.Provider` |
 *
 * ```
 * object ModParticlesClient : ClientParticleRegistrar {
 *     val shockWave by special(ModParticles.shockWave, ::ShockWaveParticle.Provider)
 * }
 * ```
 *
 * The property resolves to the [ParticleType] the provider is bound to.
 */
fun <T : ParticleOptions> ClientParticleRegistrar.special(
    type: Supplier<out ParticleType<T>>,
    provider: ParticleProvider<T>,
) = bindName { _ ->
    registrations.add { event ->
        event.registerSpecial(type.get(), provider)
    }

    type.get()
}