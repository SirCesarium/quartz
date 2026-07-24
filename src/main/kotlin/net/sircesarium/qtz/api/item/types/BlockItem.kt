package net.sircesarium.qtz.api.item.types

import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.Item
import net.neoforged.neoforge.registries.DeferredItem
import net.sircesarium.qtz.api.block.BlockWithItem
import net.sircesarium.qtz.api.item.ItemRegistry

fun ItemRegistry.blockItem(
    block: BlockWithItem<*>,
    configure: Item.Properties.() -> Unit = {},
): DeferredItem<BlockItem> {
    return block.item ?: run {
        val props = Item.Properties().apply(configure)
        items.registerSimpleBlockItem(block.block, props)
    }
}
