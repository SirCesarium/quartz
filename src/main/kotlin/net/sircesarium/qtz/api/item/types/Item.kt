package net.sircesarium.qtz.api.item.types

import net.minecraft.world.item.Item
import net.sircesarium.qtz.api.item.ItemProvider
import net.sircesarium.qtz.api.item.ItemRegistry

fun ItemRegistry.item(
    name: String? = null,

    configure: Item.Properties.() -> Unit = {},
) = ItemProvider(
    registry = this,
    name,
    factory = ::Item,
    configure
)

fun <T : Item> ItemRegistry.item(
    factory: (Item.Properties) -> T,
    name: String? = null,

    configure: Item.Properties.() -> Unit = {},
) = ItemProvider(
    registry = this,
    name,
    factory,
    configure
)
