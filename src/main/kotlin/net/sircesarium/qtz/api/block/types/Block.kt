package net.sircesarium.qtz.api.block.types

import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockBehaviour
import net.sircesarium.qtz.api.block.BlockRegistry
import net.sircesarium.qtz.api.block.provider.BlockOnlyProvider
import net.sircesarium.qtz.api.block.provider.BlockProvider
import net.sircesarium.qtz.api.datagen.BlockShape

fun BlockRegistry.block(
    name: String? = null,
    datagen: Boolean = true,
    shape: BlockShape = BlockShape.CubeAll,
    configure: BlockBehaviour.Properties.() -> Unit = {},
    itemConfigure: Item.Properties.() -> Unit = {},
): BlockProvider<Block> = BlockProvider(
    registry = this, name, factory = ::Block, configure,
    itemConfigure, datagen, shape
)

fun <T : Block> BlockRegistry.block(
    factory: (BlockBehaviour.Properties) -> T,
    name: String? = null,
    datagen: Boolean = true,
    configure: BlockBehaviour.Properties.() -> Unit = {},
    itemConfigure: Item.Properties.() -> Unit = {},
): BlockProvider<T> = BlockProvider(
    registry = this, name, factory, configure,
    itemConfigure, datagen
)

fun BlockRegistry.blockOnly(
    name: String? = null,
    datagen: Boolean = true,
    configure: BlockBehaviour.Properties.() -> Unit = {},
): BlockOnlyProvider<Block> = BlockOnlyProvider(
    registry = this, name, factory = ::Block, configure,
    datagen
)

fun <T : Block> BlockRegistry.blockOnly(
    factory: (BlockBehaviour.Properties) -> T,
    name: String? = null,
    datagen: Boolean = true,
    configure: BlockBehaviour.Properties.() -> Unit = {},
): BlockOnlyProvider<T> = BlockOnlyProvider(
    registry = this, name, factory, configure,
    datagen
)
