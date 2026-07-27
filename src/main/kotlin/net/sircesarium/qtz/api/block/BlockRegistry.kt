package net.sircesarium.qtz.api.block

import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredRegister
import net.sircesarium.qtz.api.IRegistry
import kotlin.properties.PropertyDelegateProvider
import kotlin.properties.ReadOnlyProperty

open class BlockRegistry(modId: String) : IRegistry {
    internal val blocks = DeferredRegister.createBlocks(modId)
    internal val items = DeferredRegister.createItems(modId)

    override fun register(bus: IEventBus) {
        blocks.register(bus)
        items.register(bus)
    }

    fun <T> bindName(factory: (String) -> T) = PropertyDelegateProvider<Any?, ReadOnlyProperty<Any?, T>> { _, property ->
        val instance = factory(property.name)
        ReadOnlyProperty { _, _ -> instance }
    }
}