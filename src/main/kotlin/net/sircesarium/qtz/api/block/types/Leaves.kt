package net.sircesarium.qtz.api.block.types

import net.minecraft.world.item.Item
import net.minecraft.world.level.block.LeavesBlock
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.state.BlockBehaviour
import net.sircesarium.qtz.api.block.provider.BlockProvider
import net.sircesarium.qtz.api.block.BlockRegistry
import net.sircesarium.qtz.api.datagen.BlockShape

fun BlockRegistry.leaves(
    name: String? = null,
    datagen: Boolean = true,
    configure: BlockBehaviour.Properties.() -> Unit = {},
    itemConfigure: Item.Properties.() -> Unit = {},
): BlockProvider<LeavesBlock> = BlockProvider(
    registry = this, name,
    factory = ::LeavesBlock,
    configure = {
        ignitedByLava()
        noOcclusion()
        randomTicks()
        sound(SoundType.GRASS)
        configure()
    },
    itemConfigure, datagen, BlockShape.CubeAll
)
