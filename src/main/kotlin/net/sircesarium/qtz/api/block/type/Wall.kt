package net.sircesarium.qtz.api.block.type

import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.BlockTags
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.WallBlock
import net.minecraft.world.level.block.state.BlockBehaviour
import net.neoforged.neoforge.client.model.generators.BlockStateProvider
import net.sircesarium.qtz.api.DatagenPlan
import net.sircesarium.qtz.api.LootTableContext
import net.sircesarium.qtz.api.TagContext
import net.sircesarium.qtz.api.block.BlockBuilder
import net.sircesarium.qtz.api.block.BlockRegistry
import net.sircesarium.qtz.api.block.BlockWithItem
import net.sircesarium.qtz.utils.soundType
import net.sircesarium.qtz.utils.toSnakeCase
import net.neoforged.neoforge.registries.DeferredBlock
import net.neoforged.neoforge.registries.DeferredItem

/**
 * Datagen plan for a [WallBlock].
 *
 * Generates post, side, side-tall, and inventory models, a wall blockstate via
 * [BlockStateProvider.wallBlock], a self-drop loot table, and registers [BlockTags.WALLS].
 */
class WallPlan internal constructor(
    private val id: String,
    val deferredBlock: DeferredBlock<WallBlock>,
    val item: DeferredItem<BlockItem>,
    private val texture: ResourceLocation,
) : DatagenPlan {
    override val block: Block get() = deferredBlock.get()
    var blockState: Boolean = true
    var lootTable: Boolean = true
    var renderType: String? = null

    override fun emitBlockState(provider: BlockStateProvider) {
        if (!blockState) return
        val post = provider.models().wallPost("${id}_post", texture)
        val side = provider.models().wallSide("${id}_side", texture)
        val sideTall = provider.models().wallSideTall("${id}_side_tall", texture)
        val inventory = provider.models().wallInventory("${id}_inventory", texture)
        renderType?.let {
            post.renderType(it)
            side.renderType(it)
            sideTall.renderType(it)
            inventory.renderType(it)
        }
        provider.wallBlock(deferredBlock.get() as WallBlock, post, side, sideTall)
        provider.simpleBlockItem(deferredBlock.get(), inventory)
    }

    override fun emitLootTable(context: LootTableContext) {
        if (!lootTable) return
        context.dropSelf(deferredBlock.get())
    }

    override fun emitTags(context: TagContext) {
        context.add(BlockTags.WALLS, deferredBlock.get())
    }
}

/**
 * Scope for configuring a [WallPlan] inside [BlockRegistry.wall].
 */
class WallScope(private val plan: WallPlan, private val builder: BlockBuilder<WallBlock>) {
    var blockState by plan::blockState
    var lootTable by plan::lootTable
    var renderType by plan::renderType

    fun configureBlock(action: BlockBehaviour.Properties.() -> Unit) {
        builder.configureBlock(action)
    }

    fun configureItem(action: Item.Properties.() -> Unit) {
        builder.configureItem(action)
    }
}

/**
 * Creates a wall variant of the given [baseBlock] base material.
 *
 * Inherits the sound type from the base block.
 * Textures are resolved from the base block's registry ID (any namespace).
 * Auto-registers [BlockTags.WALLS] for wall connectivity.
 *
 * | Property | Config block | Example |
 * |---|---|---|
 * | Name | `by wall(base)` → `"stone_wall"` |
 * | Block class | auto-set to `WallBlock` |
 * | Block properties | `configureBlock { }` | `strength(2.0f)`, `noCollision()` |
 * | Item properties | `configureItem { }` | `stacksTo(16)` |
 * | Block state + model | `blockState = true/false` | disable to provide custom |
 * | Loot table | `lootTable = true/false` | disable to provide custom |
 * | Render type | `renderType = "cutout"` | for transparent blocks |
 *
 * Datagen is auto-generated — each wall owns its plan.
 * Flags are per-wall. Disabling one does not affect others.
 *
 * ```
 * class ModBlocks : BlockRegistry("modid") {
 *     val stoneWall by wall(Blocks.STONE)
 *
 *     val glassWall by wall(Blocks.GLASS) {
 *         renderType = "cutout"
 *         configureBlock { noOcclusion() }
 *     }
 * }
 * ```
 */
fun BlockRegistry.wall(
    baseBlock: Block,
    config: WallScope.() -> Unit = {},
) = bindName { rawName ->
    val name = rawName.toSnakeCase()
    val builder = BlockBuilder<WallBlock>()
    val baseId = BuiltInRegistries.BLOCK.getKey(baseBlock)
    val texture = ResourceLocation.fromNamespaceAndPath(baseId.namespace, "block/${baseId.path}")

    val blockHolder = blocks.registerBlock(name) {
        val p = BlockBehaviour.Properties.of()
            .sound(baseBlock.soundType)
        builder.blockCustomizer?.invoke(p)
        WallBlock(p)
    }

    val itemHolder = items.registerItem(name) {
        val p = Item.Properties()
        builder.itemCustomizer?.invoke(p)
        BlockItem(blockHolder.get(), p)
    }

    val plan = WallPlan(name, blockHolder, itemHolder, texture)
    WallScope(plan, builder).apply(config)
    plans.add(plan)
    BlockWithItem(blockHolder, itemHolder)
}
