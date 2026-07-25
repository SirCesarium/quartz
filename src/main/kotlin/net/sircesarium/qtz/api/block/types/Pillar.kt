package net.sircesarium.qtz.api.block.types

import net.minecraft.world.item.Item
import net.minecraft.world.level.block.RotatedPillarBlock
import net.minecraft.world.level.block.state.BlockBehaviour
import net.sircesarium.qtz.api.block.BlockProvider
import net.sircesarium.qtz.api.block.BlockRegistry
import net.sircesarium.qtz.api.datagen.BlockShape

fun BlockRegistry.pillar(
    name: String? = null,
    withItem: Boolean = true,
    datagen: Boolean = true,
    configure: BlockBehaviour.Properties.() -> Unit = {},
    itemConfigure: Item.Properties.() -> Unit = {},
) = BlockProvider(
    registry = this, name,
    factory = ::RotatedPillarBlock,
    configure, withItem, itemConfigure, datagen, BlockShape.CubeColumn()
)
