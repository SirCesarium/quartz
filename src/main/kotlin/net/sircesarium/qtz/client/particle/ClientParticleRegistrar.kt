package net.sircesarium.qtz.client.particle

import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent
import kotlin.properties.PropertyDelegateProvider
import kotlin.properties.ReadOnlyProperty

/**
 * Registers particle providers on the client.
 *
 * Client-only: this class references `net.minecraft.client.*` classes, so it
 * must never be loaded on a dedicated server. Register it from an `@Mod` class
 * annotated with `dist = [Dist.CLIENT]`; the server never loads that class:
 *
 * ```
 * object ModParticlesClient : ClientParticleRegistrar {
 *     val sparkle by spriteSet(ModParticles.sparkle) { sprites -> SparkleParticle.Factory(sprites) }
 * }
 *
 * @Mod(value = "modid", dist = [Dist.CLIENT])
 * class ModClient(modEventBus: IEventBus) {
 *     init {
 *         ModParticlesClient.register(modEventBus)
 *     }
 * }
 * ```
 *
 * Providers are declared with [spriteSet] and [special].
 */
open class ClientParticleRegistrar {
    @PublishedApi internal val registrations = mutableListOf<(RegisterParticleProvidersEvent) -> Unit>()

    fun register(bus: IEventBus) {
        bus.addListener(::onRegisterProviders)
    }

    private fun onRegisterProviders(event: RegisterParticleProvidersEvent) {
        for (registration in registrations) {
            registration(event)
        }
    }

    fun <T> bindName(factory: (String) -> T) = PropertyDelegateProvider<Any?, ReadOnlyProperty<Any?, T>> { _, property ->
        val instance = factory(property.name)
        ReadOnlyProperty { _, _ -> instance }
    }
}