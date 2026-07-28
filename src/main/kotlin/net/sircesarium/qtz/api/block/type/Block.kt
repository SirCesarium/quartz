package net.sircesarium.qtz.api.block.type

import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockBehaviour
import net.sircesarium.qtz.api.block.BlockBuilder
import net.sircesarium.qtz.api.block.BlockRegistry
import net.sircesarium.qtz.api.block.BlockWithItem
import net.sircesarium.qtz.utils.toSnakeCase
import kotlin.jvm.JvmName

/**
 * Registers a block named after the property (converted to snake_case) with
 * its corresponding [BlockItem].
 *
 * | Property | Config block | Example |
 * |---|---|---|
 * | Property name → registry name | `by block()` → `"some_block"` |
 * | Block properties | `configureBlock { }` | `noCollision()`, `strength(2.0f)`, `sound(SoundType.STONE)` |
 * | Item properties | `configureItem { }` | `stacksTo(4)`, `fireResistant()` |
 *
 * ```
 * class ModBlocks : BlockRegistry("modid") {
 *     val simplyNamedBlock by block()
 *
 *     val configuredBlock by block {
 *         configureBlock {
 *             noCollision()
 *             lightLevel { 15 }
 *             strength(0.3f)
 *             sound(SoundType.GLASS)
 *         }
 *
 *         configureItem {
 *             stacksTo(16)
 *             fireResistant()
 *         }
 *     }
 * }
 * ```
 */
@JvmName("blockSimple")
fun BlockRegistry.block(config: BlockBuilder<Block>.() -> Unit = {}) = block<Block>(config)

/**
 * Registers a block using a custom block class.
 *
 * All config options from [block] apply, plus:
 *
 * | Property | Config block | Example |
 * |---|---|---|
 * | Custom block class | `customBlock(::MyBlock)` | `customBlock(::MyCustomBlock)` |
 *
 * ```
 * class MyBlock(properties: Properties) : Block(properties)
 *
 * class ModBlocks : BlockRegistry("modid") {
 *     val myBlock: BlockWithItem<MyBlock, BlockItem> by block {
 *         customBlock(::MyBlock)
 *
 *         configureBlock {
 *             strength(2.0f)
 *             noCollision()
 *         }
 *
 *         configureItem {
 *             stacksTo(1)
 *         }
 *     }
 * }
 * ```
 */
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
