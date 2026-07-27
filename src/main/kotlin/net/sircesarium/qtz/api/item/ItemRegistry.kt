package net.sircesarium.qtz.api.item

import net.minecraft.world.item.Item
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredItem
import net.neoforged.neoforge.registries.DeferredRegister
import kotlin.reflect.KProperty

abstract class ItemRegistry(val modId: String) {
    val items: DeferredRegister.Items = DeferredRegister.createItems(modId)

    internal val itemModels = mutableListOf<Pair<String, String>>()
    internal val handheldModels = mutableListOf<Pair<String, String>>()

    companion object {
        internal val instances = mutableListOf<ItemRegistry>()
    }

    init {
        instances.add(this)
    }

    fun register(bus: IEventBus) {
        items.register(bus)
    }
}

class ItemDelegate<T : Item>(val holder: DeferredItem<T>) {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): DeferredItem<T> = holder
}
