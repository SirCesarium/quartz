package net.sircesarium.qtz.api.block.types

import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockBehaviour
import net.sircesarium.qtz.api.block.provider.BlockProvider
import net.sircesarium.qtz.api.block.BlockRegistry
import net.sircesarium.qtz.api.datagen.BlockShape

fun BlockRegistry.block(
    name: String? = null,
    withItem: Boolean = true,
    datagen: Boolean = true,
    shape: BlockShape = BlockShape.CubeAll,
    configure: BlockBehaviour.Properties.() -> Unit = {},
    itemConfigure: Item.Properties.() -> Unit = {},
) = BlockProvider(
    registry = this, name, factory = ::Block, configure,
    withItem, itemConfigure, datagen, shape
)

fun <T : Block> BlockRegistry.block(
    factory: (BlockBehaviour.Properties) -> T,
    name: String? = null,
    withItem: Boolean = true,
    datagen: Boolean = true,
    configure: BlockBehaviour.Properties.() -> Unit = {},
    itemConfigure: Item.Properties.() -> Unit = {},
) = BlockProvider(
    registry = this, name, factory, configure,
    withItem, itemConfigure, datagen
)
