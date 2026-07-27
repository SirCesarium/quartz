package net.sircesarium.qtz.api.item.provider

import net.minecraft.world.item.Item
import net.sircesarium.qtz.api.item.ItemDelegate
import net.sircesarium.qtz.api.item.ItemRegistry
import net.sircesarium.qtz.api.util.toSnakeCase
import kotlin.reflect.KProperty

open class ItemProvider<T : Item>(
    private val registry: ItemRegistry,
    private val name: String?,
    private val factory: (Item.Properties) -> T,
    private val configure: Item.Properties.() -> Unit,
    private val onRegister: ((String) -> Unit)? = null,
) {
    open operator fun provideDelegate(thisRef: Any?, prop: KProperty<*>): ItemDelegate<T> {
        val id = name ?: prop.name.toSnakeCase()

        val props = Item.Properties()
        configure(props)

        onRegister?.invoke(id)

        return ItemDelegate(registry.items.registerItem(id, factory, props))
    }
}
