package net.sircesarium.qtz.api.item

import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredRegister
import net.sircesarium.qtz.api.IRegistry
import kotlin.properties.PropertyDelegateProvider
import kotlin.properties.ReadOnlyProperty

open class ItemRegistry(val modId: String) : IRegistry {
    @PublishedApi internal val items = DeferredRegister.createItems(modId)
    @PublishedApi internal val plans = mutableListOf<IItemPlan>()

    companion object {
        val instances = mutableListOf<ItemRegistry>()
    }

    init {
        instances.add(this)
    }

    override fun register(bus: IEventBus) {
        items.register(bus)
    }

    fun <T> bindName(factory: (String) -> T) = PropertyDelegateProvider<Any?, ReadOnlyProperty<Any?, T>> { _, property ->
        val instance = factory(property.name)
        ReadOnlyProperty { _, _ -> instance }
    }
}
