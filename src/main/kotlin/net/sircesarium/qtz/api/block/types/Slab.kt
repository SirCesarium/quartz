package net.sircesarium.qtz.api.block.types

import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.SlabBlock
import net.minecraft.world.level.block.state.BlockBehaviour
import net.sircesarium.qtz.api.block.BlockRegistry
import net.sircesarium.qtz.api.block.BlockWithItem
import net.sircesarium.qtz.api.block.provider.BlockProvider

fun BlockRegistry.slab(
    baseBlock: Block,
    name: String? = null,
    datagen: Boolean = true,
    configure: BlockBehaviour.Properties.() -> Unit = {},
    itemConfigure: Item.Properties.() -> Unit = {},
): BlockProvider<SlabBlock> {
    val baseName = BuiltInRegistries.BLOCK.getKey(baseBlock).toString()
    val userConfigure = configure
    val reg = this
    return BlockProvider(
        registry = reg, name = name,
        factory = { SlabBlock(it) },
        configure = {
            sound(BlockSound.get(baseBlock))
            userConfigure()
        },
        itemConfigure = itemConfigure, datagen = false,
        onRegister = if (datagen) {{ id, _ ->
            reg.slabBlocks.add(Triple(reg.modId, id, baseName))
        }} else null
    )
}

fun BlockRegistry.slab(
    baseBlock: BlockWithItem<*>,
    name: String? = null,
    datagen: Boolean = true,
    configure: BlockBehaviour.Properties.() -> Unit = {},
    itemConfigure: Item.Properties.() -> Unit = {},
): BlockProvider<SlabBlock> {
    val baseName = baseBlock.block.id.toString()
    val userConfigure = configure
    val reg = this
    return BlockProvider(
        registry = reg, name = name,
        factory = { SlabBlock(it) },
        configure = {
            sound(BlockSound.get(baseBlock.block.get()))
            userConfigure()
        },
        itemConfigure = itemConfigure, datagen = false,
        onRegister = if (datagen) {{ id, _ ->
            reg.slabBlocks.add(Triple(reg.modId, id, baseName))
        }} else null
    )
}
