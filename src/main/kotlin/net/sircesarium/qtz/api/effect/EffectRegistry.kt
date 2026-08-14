package net.sircesarium.qtz.api.effect

import net.minecraft.core.registries.Registries
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredRegister
import net.sircesarium.qtz.api.IRegistry
import kotlin.properties.PropertyDelegateProvider
import kotlin.properties.ReadOnlyProperty

open class EffectRegistry(val modId: String) : IRegistry {
    @PublishedApi internal val effects =
        DeferredRegister.create(Registries.MOB_EFFECT, modId)

    companion object {
        val instances = mutableListOf<EffectRegistry>()
    }

    init {
        instances.add(this)
    }

    override fun register(bus: IEventBus) {
        effects.register(bus)
    }

    fun <T> bindName(factory: (String) -> T) = PropertyDelegateProvider<Any?, ReadOnlyProperty<Any?, T>> { _, property ->
        val instance = factory(property.name)
        ReadOnlyProperty { _, _ -> instance }
    }
}