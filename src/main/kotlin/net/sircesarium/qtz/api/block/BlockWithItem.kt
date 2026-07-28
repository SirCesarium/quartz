package net.sircesarium.qtz.api.block

import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.neoforged.neoforge.registries.DeferredBlock
import net.neoforged.neoforge.registries.DeferredItem

data class BlockWithItem<B : Block, I : Item>(
    val blockHolder: DeferredBlock<B>,
    val itemHolder: DeferredItem<I>
) {
    val block: B get() = blockHolder.get()
    val item: I get() = itemHolder.get()
}
