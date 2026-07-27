package net.sircesarium.qtz.api.block.types

import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.SnowLayerBlock
import net.minecraft.world.level.block.state.BlockBehaviour
import net.sircesarium.qtz.api.block.BlockRegistry
import net.sircesarium.qtz.api.block.BlockWithItem
import net.sircesarium.qtz.api.block.provider.BlockProvider
import net.sircesarium.qtz.api.datagen.DatagenRegistry
import net.neoforged.neoforge.registries.DeferredBlock

fun BlockRegistry.snowLayer(
    baseBlock: Block,
    name: String? = null,
    withItem: Boolean = true,
    datagen: Boolean = true,
    configure: BlockBehaviour.Properties.() -> Unit = {},
    itemConfigure: Item.Properties.() -> Unit = {},
): BlockProvider<SnowLayerBlock> {
    val key = BuiltInRegistries.BLOCK.getResourceKey(baseBlock).orElseThrow()
    return snowLayer(DeferredBlock.createBlock<Block>(key), name, withItem, datagen, configure, itemConfigure)
}

fun BlockRegistry.snowLayer(
    baseBlock: DeferredBlock<*>,
    name: String? = null,
    withItem: Boolean = true,
    datagen: Boolean = true,
    configure: BlockBehaviour.Properties.() -> Unit = {},
    itemConfigure: Item.Properties.() -> Unit = {},
): BlockProvider<SnowLayerBlock> {
    val userConfigure = configure
    return BlockProvider(
        registry = this, name = name,
        factory = { SnowLayerBlock(it) },
        configure = {
            sound(BlockSound.get(baseBlock.get()))
            userConfigure()
        },
        withItem = withItem,
        itemConfigure = itemConfigure, datagen = false,
        onRegister = if (datagen) {{ id, _ ->
            DatagenRegistry.snowLayerBlocks.add(Triple(modId, id, baseBlock.id.path))
        }} else null
    )
}

fun BlockRegistry.snowLayer(
    baseBlock: BlockWithItem<*>,
    name: String? = null,
    withItem: Boolean = true,
    datagen: Boolean = true,
    configure: BlockBehaviour.Properties.() -> Unit = {},
    itemConfigure: Item.Properties.() -> Unit = {},
): BlockProvider<SnowLayerBlock> = snowLayer(baseBlock.block, name, withItem, datagen, configure, itemConfigure)
