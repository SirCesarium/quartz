package net.sircesarium.qtz.api.block.types

import net.minecraft.world.item.Item
import net.minecraft.world.level.block.StairBlock
import net.minecraft.world.level.block.state.BlockBehaviour
import net.sircesarium.qtz.api.block.provider.BlockProvider
import net.sircesarium.qtz.api.block.BlockRegistry
import net.sircesarium.qtz.api.block.BlockWithItem
import net.sircesarium.qtz.api.block.provider.StairProvider
import net.neoforged.neoforge.registries.DeferredBlock

fun BlockRegistry.stair(
    baseBlock: DeferredBlock<*>,
    name: String? = null,
    withItem: Boolean = true,
    datagen: Boolean = true,
    configure: BlockBehaviour.Properties.() -> Unit = {},
    itemConfigure: Item.Properties.() -> Unit = {},
): BlockProvider<StairBlock> = StairProvider(this, name, baseBlock, withItem, datagen, configure, itemConfigure)

fun BlockRegistry.stair(
    baseBlock: BlockWithItem<*>,
    name: String? = null,
    withItem: Boolean = true,
    datagen: Boolean = true,
    configure: BlockBehaviour.Properties.() -> Unit = {},
    itemConfigure: Item.Properties.() -> Unit = {},
): BlockProvider<StairBlock> = stair(baseBlock.block, name, withItem, datagen, configure, itemConfigure)
