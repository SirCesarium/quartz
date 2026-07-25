package net.sircesarium.qtz.api.block.types

import net.minecraft.world.level.block.RotatedPillarBlock
import net.minecraft.world.level.block.state.BlockBehaviour
import net.sircesarium.qtz.api.block.BlockProvider
import net.sircesarium.qtz.api.block.BlockRegistry

fun BlockRegistry.pillar(
    name: String? = null,
    withItem: Boolean = true,
    configure: BlockBehaviour.Properties.() -> Unit = {},
) = BlockProvider(
    registry = this, name,
    factory = ::RotatedPillarBlock,
    configure, withItem
)
