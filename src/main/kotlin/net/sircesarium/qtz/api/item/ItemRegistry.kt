package net.sircesarium.qtz.api.item

import net.minecraft.world.item.Item
import net.neoforged.bus.api.IEventBus

import net.neoforged.neoforge.registries.DeferredItem
import net.neoforged.neoforge.registries.DeferredRegister
import net.sircesarium.qtz.api.util.toSnakeCase
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

class ItemProvider<T : Item>(
    private val registry: ItemRegistry,
    private val name: String?,
    private val factory: (Item.Properties) -> T,
    private val configure: Item.Properties.() -> Unit,
    private val opts: QtzItem = QtzItem(),
) {
    operator fun provideDelegate(thisRef: Any?, prop: KProperty<*>): ItemDelegate<T> {
        val id = name ?: prop.name.toSnakeCase()
        val props = Item.Properties()
        opts.itemOps?.invoke(props)
        configure(props)
        return ItemDelegate(registry.items.registerItem(id, factory, props))
    }
}
