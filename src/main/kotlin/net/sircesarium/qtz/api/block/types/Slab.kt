package net.sircesarium.qtz.api.block.types

import net.minecraft.world.item.Item
import net.minecraft.world.level.block.SlabBlock
import net.minecraft.world.level.block.state.BlockBehaviour
import net.sircesarium.qtz.api.block.BlockRegistry
import net.sircesarium.qtz.api.block.BlockWithItem
import net.sircesarium.qtz.api.block.provider.BlockProvider
import net.sircesarium.qtz.api.datagen.DatagenRegistry
import net.neoforged.neoforge.registries.DeferredBlock

fun BlockRegistry.slab(
    baseBlock: DeferredBlock<*>,
    name: String? = null,
    withItem: Boolean = true,
    datagen: Boolean = true,
    configure: BlockBehaviour.Properties.() -> Unit = {},
    itemConfigure: Item.Properties.() -> Unit = {},
): BlockProvider<SlabBlock> {
    val userConfigure = configure
    return BlockProvider(
        registry = this, name = name,
        factory = { SlabBlock(it) },
        configure = {
            sound(BlockSound.get(baseBlock.get()))
            userConfigure()
        },
        withItem = withItem,
        itemConfigure = itemConfigure, datagen = false,
        onRegister = if (datagen) {{ id, _ ->
            DatagenRegistry.slabBlocks.add(Triple(modId, id, baseBlock.id.path))
        }} else null
    )
}

fun BlockRegistry.slab(
    baseBlock: BlockWithItem<*>,
    name: String? = null,
    withItem: Boolean = true,
    datagen: Boolean = true,
    configure: BlockBehaviour.Properties.() -> Unit = {},
    itemConfigure: Item.Properties.() -> Unit = {},
): BlockProvider<SlabBlock> = slab(baseBlock.block, name, withItem, datagen, configure, itemConfigure)
