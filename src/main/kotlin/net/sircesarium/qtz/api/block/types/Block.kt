package net.sircesarium.qtz.api.block.types

import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockBehaviour
import net.sircesarium.qtz.api.block.BlockProvider
import net.sircesarium.qtz.api.block.BlockRegistry

fun BlockRegistry.block(
    name: String? = null,
    withItem: Boolean = true,

    configure: BlockBehaviour.Properties.() -> Unit = {},
) = BlockProvider(
    registry = this,
    name,
    factory = ::Block,
    configure,
    withItem
)

fun <T : Block> BlockRegistry.block(
    factory: (BlockBehaviour.Properties) -> T,
    name: String? = null,
    withItem: Boolean = true,

    configure: BlockBehaviour.Properties.() -> Unit = {},
) = BlockProvider(
    registry = this,
    name,
    factory,
    configure,
    withItem
)
