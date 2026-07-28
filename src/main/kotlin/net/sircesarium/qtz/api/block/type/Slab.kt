package net.sircesarium.qtz.api.block.type

import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.SlabBlock
import net.sircesarium.qtz.api.block.BlockBuilder
import net.sircesarium.qtz.api.block.BlockRegistry
import net.sircesarium.qtz.utils.soundType

/**
 * Creates a slab variant of the given [block] base material.
 *
 * Inherits the sound type from the base block.
 *
 * | Property | Config block | Example |
 * |---|---|---|
 * | Name | `by slab(base)` → `"stone_slab"` |
 * | Block class | auto-set to `SlabBlock` |
 * | Block properties | `configureBlock { }` | `strength(2.0f)`, `noCollision()` |
 * | Item properties | `configureItem { }` | `stacksTo(16)` |
 *
 * ```
 * class ModBlocks : BlockRegistry("modid") {
 *     val stoneSlab by slab(Blocks.STONE)
 *
 *     val softSlab by slab(Blocks.STONE) {
 *         configureBlock {
 *             noCollision()
 *             lightLevel { 8 }
 *         }
 *
 *         configureItem {
 *             stacksTo(4)
 *         }
 *     }
 * }
 * ```
 */
fun BlockRegistry.slab(block: Block, config: BlockBuilder<SlabBlock>.() -> Unit = {}) = block<SlabBlock> {
    customBlock(::SlabBlock)
    configureBlock {
        sound(block.soundType)
    }
    config()
}
