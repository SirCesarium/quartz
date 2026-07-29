package net.sircesarium.qtz.api.item.type

import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item
import net.neoforged.neoforge.client.model.generators.ItemModelProvider
import net.neoforged.neoforge.registries.DeferredItem
import net.sircesarium.qtz.api.item.IItemPlan
import net.sircesarium.qtz.api.item.ItemBuilder
import net.sircesarium.qtz.api.item.ItemRegistry
import net.sircesarium.qtz.utils.toSnakeCase
import kotlin.jvm.JvmName

class ItemPlan @PublishedApi internal constructor(
    private val id: String,
    val deferredItem: DeferredItem<out Item>,
    private val defaultTexture: ResourceLocation,
) : IItemPlan {
    override val item: Item get() = deferredItem.get()
    var model: Boolean = true
    var customTexture: ResourceLocation? = null

    override fun emitItemModel(provider: ItemModelProvider) {
        if (!model) return
        val texture = customTexture ?: defaultTexture
        provider.singleTexture(id, provider.mcLoc("item/generated"), "layer0", texture)
    }
}

@JvmName("itemSimple")
fun ItemRegistry.item(
    config: ItemBuilder<Item>.() -> Unit = {},
) = item<Item>(config)

inline fun <reified I : Item> ItemRegistry.item(
    noinline config: ItemBuilder<I>.() -> Unit = {},
) = bindName { rawName ->
    val name = rawName.toSnakeCase()
    val builder = ItemBuilder<I>().apply(config)

    @Suppress("UNCHECKED_CAST")
    val factory = builder.itemFactory ?: { Item(it) as I }

    val deferred = items.registerItem(name) {
        val props = Item.Properties().apply { builder.propertiesCustomizer?.invoke(this) }
        factory(props)
    }

    val texture = ResourceLocation.fromNamespaceAndPath(modId, "item/${name}")
    val plan = ItemPlan(name, deferred, texture).apply {
        model = builder.model
        customTexture = builder.customTexture
    }
    plans.add(plan)
    deferred
}
