package net.sircesarium.qtz.api.particle

import net.minecraft.core.registries.Registries
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredRegister
import net.sircesarium.qtz.api.IRegistry
import kotlin.properties.PropertyDelegateProvider
import kotlin.properties.ReadOnlyProperty

open class ParticleRegistry(val modId: String) : IRegistry {
    @PublishedApi internal val particles =
        DeferredRegister.create(Registries.PARTICLE_TYPE, modId)

    companion object {
        val instances = mutableListOf<ParticleRegistry>()
    }

    init {
        instances.add(this)
    }

    override fun register(bus: IEventBus) {
        particles.register(bus)
    }

    fun <T> bindName(factory: (String) -> T) = PropertyDelegateProvider<Any?, ReadOnlyProperty<Any?, T>> { _, property ->
        val instance = factory(property.name)
        ReadOnlyProperty { _, _ -> instance }
    }
}