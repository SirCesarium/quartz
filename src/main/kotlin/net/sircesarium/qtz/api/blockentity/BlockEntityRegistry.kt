package net.sircesarium.qtz.api.blockentity

import net.minecraft.core.registries.Registries
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredRegister
import net.sircesarium.qtz.api.IRegistry
import kotlin.properties.PropertyDelegateProvider
import kotlin.properties.ReadOnlyProperty

open class BlockEntityRegistry(val modId: String) : IRegistry {
    @PublishedApi internal val blockEntityTypes =
        DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, modId)

    companion object {
        val instances = mutableListOf<BlockEntityRegistry>()
    }

    init {
        instances.add(this)
    }

    override fun register(bus: IEventBus) {
        blockEntityTypes.register(bus)
    }

    fun <T> bindName(factory: (String) -> T) = PropertyDelegateProvider<Any?, ReadOnlyProperty<Any?, T>> { _, property ->
        val instance = factory(property.name)
        ReadOnlyProperty { _, _ -> instance }
    }
}