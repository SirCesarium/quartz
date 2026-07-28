package net.sircesarium.qtz.api.block

import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredRegister
import net.sircesarium.qtz.api.DatagenPlan
import net.sircesarium.qtz.api.IRegistry
import kotlin.properties.PropertyDelegateProvider
import kotlin.properties.ReadOnlyProperty

open class BlockRegistry(val modId: String) : IRegistry {
    @PublishedApi internal val blocks = DeferredRegister.createBlocks(modId)
    @PublishedApi internal val items = DeferredRegister.createItems(modId)

    internal val plans = mutableListOf<DatagenPlan>()

    companion object {
        val instances = mutableListOf<BlockRegistry>()
    }

    init {
        instances.add(this)
    }

    override fun register(bus: IEventBus) {
        blocks.register(bus)
        items.register(bus)
    }

    fun <T> bindName(factory: (String) -> T) = PropertyDelegateProvider<Any?, ReadOnlyProperty<Any?, T>> { _, property ->
        val instance = factory(property.name)
        ReadOnlyProperty { _, _ -> instance }
    }
}
