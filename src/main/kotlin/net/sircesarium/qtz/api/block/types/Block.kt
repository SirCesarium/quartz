package net.sircesarium.qtz.api.block.types

import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockBehaviour
import net.sircesarium.qtz.api.block.BlockProvider
import net.sircesarium.qtz.api.block.BlockRegistry
import net.sircesarium.qtz.api.block.QtzBlock

fun BlockRegistry.block(
    name: String? = null,
    opts: QtzBlock = QtzBlock(),

    configure: BlockBehaviour.Properties.() -> Unit = {},
) = BlockProvider(
    registry = this,
    name,
    factory = ::Block,
    configure,
    opts
)

fun <T : Block> BlockRegistry.block(
    factory: (BlockBehaviour.Properties) -> T,
    name: String? = null,
    opts: QtzBlock = QtzBlock(),
    configure: BlockBehaviour.Properties.() -> Unit = {},
) = BlockProvider(
    registry = this,
    name,
    factory,
    configure,
    opts
)
