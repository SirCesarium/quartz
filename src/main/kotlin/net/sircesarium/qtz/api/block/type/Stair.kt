package net.sircesarium.qtz.api.block.type

import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.StairBlock
import net.minecraft.world.level.block.state.BlockBehaviour
import net.neoforged.neoforge.client.model.generators.BlockStateProvider
import net.sircesarium.qtz.api.DatagenPlan
import net.sircesarium.qtz.api.LootTableContext
import net.sircesarium.qtz.api.block.BlockBuilder
import net.sircesarium.qtz.api.block.BlockRegistry
import net.sircesarium.qtz.api.block.BlockWithItem
import net.sircesarium.qtz.utils.soundType
import net.sircesarium.qtz.utils.toSnakeCase
import net.neoforged.neoforge.registries.DeferredBlock
import net.neoforged.neoforge.registries.DeferredItem

/**
 * Datagen plan for a [StairBlock].
 *
 * Generates inner, outer, and straight stair models, a stair blockstate via
 * [BlockStateProvider.stairsBlock], and a self-drop loot table.
 */
class StairPlan internal constructor(
    private val id: String,
    val deferredBlock: DeferredBlock<StairBlock>,
    val item: DeferredItem<BlockItem>,
    private val texture: ResourceLocation,
) : DatagenPlan {
    override val block: Block get() = deferredBlock.get()
    var blockState: Boolean = true
    var lootTable: Boolean = true
    var renderType: String? = null

    override fun emitBlockState(provider: BlockStateProvider) {
        if (!blockState) return
        val stairsModel = provider.models().stairs(id, texture, texture, texture)
        val stairsInner = provider.models().stairsInner("${id}_inner", texture, texture, texture)
        val stairsOuter = provider.models().stairsOuter("${id}_outer", texture, texture, texture)
        renderType?.let {
            stairsModel.renderType(it)
            stairsInner.renderType(it)
            stairsOuter.renderType(it)
        }
        provider.stairsBlock(deferredBlock.get() as StairBlock, stairsModel, stairsInner, stairsOuter)
        provider.simpleBlockItem(deferredBlock.get(), stairsModel)
    }

    override fun emitLootTable(context: LootTableContext) {
        if (!lootTable) return
        context.dropSelf(deferredBlock.get())
    }
}

/**
 * Scope for configuring a [StairPlan] inside [BlockRegistry.stair].
 */
class StairScope(private val plan: StairPlan, private val builder: BlockBuilder<StairBlock>) {
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
 * Creates a stair variant of the given [baseBlock] base material.
 *
 * Inherits the sound type from the base block.
 * Textures are resolved from the base block's registry ID (any namespace).
 *
 * | Property | Config block | Example |
 * |---|---|---|
 * | Name | `by stair(base)` → `"stone_stair"` |
 * | Block class | auto-set to `StairBlock` (uses base block's default state for shape) |
 * | Block properties | `configureBlock { }` | `strength(2.0f)`, `noCollision()` |
 * | Item properties | `configureItem { }` | `stacksTo(16)` |
 * | Block state + model | `blockState = true/false` | disable to provide custom |
 * | Loot table | `lootTable = true/false` | disable to provide custom |
 * | Render type | `renderType = "cutout"` | for transparent blocks (glass, ice) |
 *
 * Datagen is auto-generated — each stair owns its plan.
 * Flags are per-stair. Disabling one does not affect others.
 *
 * ```
 * class ModBlocks : BlockRegistry("modid") {
 *     val stoneStair by stair(Blocks.STONE)
 *
 *     val glassStair by stair(Blocks.GLASS) {
 *         renderType = "cutout"
 *         configureBlock { noOcclusion() }
 *     }
 * }
 * ```
 */
fun BlockRegistry.stair(
    baseBlock: Block,
    config: StairScope.() -> Unit = {},
) = bindName { rawName ->
    val name = rawName.toSnakeCase()
    val builder = BlockBuilder<StairBlock>()
    val baseId = BuiltInRegistries.BLOCK.getKey(baseBlock)
    val texture = ResourceLocation.fromNamespaceAndPath(baseId.namespace, "block/${baseId.path}")

    val blockHolder = blocks.registerBlock(name) {
        val p = BlockBehaviour.Properties.of()
            .sound(baseBlock.soundType)
        builder.blockCustomizer?.invoke(p)
        StairBlock(baseBlock.defaultBlockState(), p)
    }

    val itemHolder = items.registerItem(name) {
        val p = Item.Properties()
        builder.itemCustomizer?.invoke(p)
        BlockItem(blockHolder.get(), p)
    }

    val plan = StairPlan(name, blockHolder, itemHolder, texture)
    StairScope(plan, builder).apply(config)
    plans.add(plan)
    BlockWithItem(blockHolder, itemHolder)
}
