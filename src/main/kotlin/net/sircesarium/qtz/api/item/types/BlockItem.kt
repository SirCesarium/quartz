package net.sircesarium.qtz.api.item.types

import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.Item
import net.sircesarium.qtz.api.block.BlockWithItem
import net.sircesarium.qtz.api.item.ItemDelegate
import net.sircesarium.qtz.api.item.ItemRegistry

fun ItemRegistry.blockItem(
    block: BlockWithItem<*>,
    configure: Item.Properties.() -> Unit = {},
): ItemDelegate<BlockItem> {
    val holder = block.item ?: run {
        val props = Item.Properties().apply(configure)
        items.registerSimpleBlockItem(block.block, props)
    }
    return ItemDelegate(holder)
}
