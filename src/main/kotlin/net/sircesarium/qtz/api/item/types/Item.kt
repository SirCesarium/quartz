package net.sircesarium.qtz.api.item.types

import net.minecraft.world.item.Item
import net.sircesarium.qtz.api.item.provider.ItemProvider
import net.sircesarium.qtz.api.item.ItemRegistry

fun ItemRegistry.item(
    name: String? = null,
    datagen: Boolean = true,
    configure: Item.Properties.() -> Unit = {},
) = ItemProvider(
    registry = this,
    name,
    factory = ::Item,
    configure,
    onRegister = if (datagen) { itemName -> this.itemModels.add(modId to itemName) } else null,
)

fun <T : Item> ItemRegistry.item(
    factory: (Item.Properties) -> T,
    name: String? = null,
    datagen: Boolean = true,
    configure: Item.Properties.() -> Unit = {},
) = ItemProvider(
    registry = this,
    name,
    factory,
    configure,
    onRegister = if (datagen) { itemName -> this.itemModels.add(modId to itemName) } else null,
)
