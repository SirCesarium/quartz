package net.sircesarium.qtz.api.block.type

import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockBehaviour
import net.sircesarium.qtz.api.block.BlockBuilder
import net.sircesarium.qtz.api.block.BlockRegistry
import net.sircesarium.qtz.api.block.BlockWithItem
import net.sircesarium.qtz.utils.toSnakeCase

inline fun <reified B : Block> BlockRegistry.block(
    noinline config: BlockBuilder<B>.() -> Unit = {},
) = bindName { rawName ->
    val name = rawName.toSnakeCase()
    val builder = BlockBuilder<B>().apply(config)

    @Suppress("UNCHECKED_CAST")
    val factory = builder.blockFactory ?: { Block(it) as B }

    val blockHolder = blocks.registerBlock(name) {
        val properties = BlockBehaviour.Properties.of()
        builder.blockCustomizer?.invoke(properties)

        factory(properties)
    }

    val itemHolder = items.registerItem(name) {
        val properties = Item.Properties()
        builder.itemCustomizer?.invoke(properties)

        BlockItem(blockHolder.get(), properties)
    }

    BlockWithItem(blockHolder, itemHolder)
}
