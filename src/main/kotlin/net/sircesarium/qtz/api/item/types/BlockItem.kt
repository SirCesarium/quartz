package net.sircesarium.qtz.api.item.types

import net.minecraft.world.item.BlockItem
import net.sircesarium.qtz.api.block.BlockWithItem
import net.sircesarium.qtz.api.item.ItemDelegate
import net.sircesarium.qtz.api.item.ItemRegistry

fun ItemRegistry.blockItem(
    block: BlockWithItem<*>,
): ItemDelegate<BlockItem> = ItemDelegate(block.item)
