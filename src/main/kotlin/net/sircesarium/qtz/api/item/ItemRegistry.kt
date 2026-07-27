package net.sircesarium.qtz.api.item

import net.minecraft.world.item.Item
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredItem
import net.neoforged.neoforge.registries.DeferredRegister
import kotlin.reflect.KProperty

abstract class ItemRegistry(val modId: String) {
    val items: DeferredRegister.Items = DeferredRegister.createItems(modId)

    fun register(bus: IEventBus) {
        items.register(bus)
    }
}

class ItemDelegate<T : Item>(val holder: DeferredItem<T>) {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): DeferredItem<T> = holder
}
