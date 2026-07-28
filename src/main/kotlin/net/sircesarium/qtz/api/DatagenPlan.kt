package net.sircesarium.qtz.api

import net.minecraft.tags.TagKey
import net.minecraft.world.item.Item
import net.minecraft.world.level.ItemLike
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.properties.IntegerProperty
import net.minecraft.world.level.storage.loot.LootTable
import net.neoforged.neoforge.client.model.generators.BlockStateProvider

/**
 * A plan for generating datagen files (blockstate, loot table, tags) for a single block.
 *
 * Each block-type registration (slab, stair, wall, etc.) creates its own plan
 * and adds it to the registry's plan list. The datagen layer iterates over
 * all plans and calls the appropriate emit methods.
 */
interface DatagenPlan {
    val block: Block
    val knownBlocks: List<Block> get() = listOf(block)
    fun emitBlockState(provider: BlockStateProvider) {}
    fun emitLootTable(context: LootTableContext) {}
    fun emitTags(context: TagContext) {}
}

/**
 * Context passed to [DatagenPlan.emitLootTable].
 *
 * Provides helper methods for common loot-table patterns.
 */
interface LootTableContext {
    fun dropSelf(block: Block)
    fun dropSlab(block: Block)
    fun dropLayered(block: Block, layersProperty: IntegerProperty, perLayer: Int = 1)
    fun dropOther(block: Block, dropsAs: ItemLike)
    fun addLootTable(block: Block, builder: LootTable.Builder)
}

/**
 * Context passed to [DatagenPlan.emitTags].
 *
 * Provides helper methods for adding block and item tags.
 */
interface TagContext {
    fun add(tag: TagKey<Block>, block: Block)
    fun add(tag: TagKey<Item>, item: ItemLike)
}
