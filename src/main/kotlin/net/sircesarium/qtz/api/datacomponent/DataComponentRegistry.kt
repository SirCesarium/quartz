package net.sircesarium.qtz.api.datacomponent

import net.minecraft.core.registries.Registries
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredRegister
import net.sircesarium.qtz.api.IRegistry
import kotlin.properties.PropertyDelegateProvider
import kotlin.properties.ReadOnlyProperty

open class DataComponentRegistry(val modId: String) : IRegistry {
    @PublishedApi internal val dataComponents =
        DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, modId)

    companion object {
        val instances = mutableListOf<DataComponentRegistry>()
    }

    init {
        instances.add(this)
    }

    override fun register(bus: IEventBus) {
        dataComponents.register(bus)
    }

    fun <T> bindName(factory: (String) -> T) = PropertyDelegateProvider<Any?, ReadOnlyProperty<Any?, T>> { _, property ->
        val instance = factory(property.name)
        ReadOnlyProperty { _, _ -> instance }
    }
}