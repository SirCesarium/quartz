package net.sircesarium.qtz.api.block.type

import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.RotatedPillarBlock
import net.minecraft.world.level.block.state.BlockBehaviour
import net.neoforged.neoforge.client.model.generators.BlockStateProvider
import net.sircesarium.qtz.api.DatagenPlan
import net.sircesarium.qtz.api.LootTableContext
import net.sircesarium.qtz.api.block.BlockBuilder
import net.sircesarium.qtz.api.block.BlockRegistry
import net.sircesarium.qtz.api.block.BlockWithItem
import net.sircesarium.qtz.utils.toSnakeCase
import net.neoforged.neoforge.registries.DeferredBlock
import net.neoforged.neoforge.registries.DeferredItem

/**
 * Datagen plan for a [RotatedPillarBlock].
 *
 * Generates a vertical cube-column model, a horizontal cube-column model,
 * and an axis-based blockstate via [BlockStateProvider.axisBlock].
 * Self-drop loot table.
 */
class PillarPlan internal constructor(
    private val id: String,
    val deferredBlock: DeferredBlock<RotatedPillarBlock>,
    val item: DeferredItem<BlockItem>,
    var textureSide: ResourceLocation,
    var textureEnd: ResourceLocation,
) : DatagenPlan {
    override val block: Block get() = deferredBlock.get()
    var blockState: Boolean = true
    var lootTable: Boolean = true

    override fun emitBlockState(provider: BlockStateProvider) {
        if (!blockState) return
        val vertical = provider.models().cubeColumn(id, textureSide, textureEnd)
        val horizontal = provider.models().cubeColumnHorizontal("${id}_horizontal", textureSide, textureEnd)
        provider.axisBlock(deferredBlock.get() as RotatedPillarBlock, vertical, horizontal)
        provider.simpleBlockItem(deferredBlock.get(), vertical)
    }

    override fun emitLootTable(context: LootTableContext) {
        if (!lootTable) return
        context.dropSelf(deferredBlock.get())
    }
}

/**
 * Scope for configuring a [PillarPlan] inside [BlockRegistry.pillar].
 */
class PillarScope(private val plan: PillarPlan, private val builder: BlockBuilder<RotatedPillarBlock>) {
    var blockState by plan::blockState
    var lootTable by plan::lootTable

    fun configureBlock(action: BlockBehaviour.Properties.() -> Unit) {
        builder.configureBlock(action)
    }

    fun configureItem(action: Item.Properties.() -> Unit) {
        builder.configureItem(action)
    }

    fun textureSide(path: String) {
        plan.textureSide = ResourceLocation.parse(path)
    }

    fun textureEnd(path: String) {
        plan.textureEnd = ResourceLocation.parse(path)
    }
}

/**
 * Register a new [RotatedPillarBlock] that can face any axis (X, Y, Z).
 *
 * **Texture requirements:**
 * - [textureSide]: side texture (bark / column face), e.g. `minecraft:block/oak_log`.
 * - [textureEnd]: top end texture (end grain), e.g. `minecraft:block/oak_log_top`.
 *
 * Both textures MUST exist. The blockstate uses [BlockStateProvider.axisBlock] with
 * a vertical model (Y-axis) and a rotated horizontal model (X/Z axes).
 *
 * | Property | Config block | Example |
 * |---|---|---|
 * | Name | `by pillar` → `"my_pillar"` |
 * | Block class | auto-set to [RotatedPillarBlock] |
 * | Block properties | `configureBlock { }` | `strength(2.0f)` |
 * | Item properties | `configureItem { }` | `stacksTo(64)` |
 * | Blockstate gen | `blockState = false` | disable |
 * | Loot table gen | `lootTable = false` | disable |
 * | Side texture | `textureSide("path")` | `textureSide("minecraft:block/oak_log")` |
 * | End texture | `textureEnd("path")` | `textureEnd("minecraft:block/oak_log_top")` |
 *
 * ```
 * class ModBlocks : BlockRegistry("modid") {
 *     val pillarWithoutBase by pillar {
 *         textureSide("minecraft:block/oak_log")
 *         textureEnd("minecraft:block/oak_log_top")
 *     }
 *
 *     val pillarWithBase by pillar(Blocks.STONE, "minecraft:block/side", "minecraft:block/end")
 * }
 * ```
 */
fun BlockRegistry.pillar(
    config: PillarScope.() -> Unit = {},
) = bindName { rawName ->
    val name = rawName.toSnakeCase()
    val builder = BlockBuilder<RotatedPillarBlock>()
    val textureSide = ResourceLocation.fromNamespaceAndPath(modId, "block/${name}_side")
    val textureEnd = ResourceLocation.fromNamespaceAndPath(modId, "block/${name}_end")

    val blockHolder = blocks.registerBlock(name) {
        val p = BlockBehaviour.Properties.of()
        builder.blockCustomizer?.invoke(p)
        RotatedPillarBlock(p)
    }

    val itemHolder = items.registerItem(name) {
        val p = Item.Properties()
        builder.itemCustomizer?.invoke(p)
        BlockItem(blockHolder.get(), p)
    }

    val plan = PillarPlan(name, blockHolder, itemHolder, textureSide, textureEnd)
    PillarScope(plan, builder).apply(config)
    plans.add(plan)
    BlockWithItem(blockHolder, itemHolder)
}

fun BlockRegistry.pillar(
    baseBlock: Block,
    textureSide: String,
    textureEnd: String,
    config: PillarScope.() -> Unit = {},
) = pillar(baseBlock, ResourceLocation.parse(textureSide), ResourceLocation.parse(textureEnd), config)

fun BlockRegistry.pillar(
    baseBlock: Block,
    textureSide: ResourceLocation,
    textureEnd: ResourceLocation,
    config: PillarScope.() -> Unit = {},
) = bindName { rawName ->
    val name = rawName.toSnakeCase()
    val builder = BlockBuilder<RotatedPillarBlock>()

    val blockHolder = blocks.registerBlock(name) {
        val p = BlockBehaviour.Properties.ofFullCopy(baseBlock)
        builder.blockCustomizer?.invoke(p)
        RotatedPillarBlock(p)
    }

    val itemHolder = items.registerItem(name) {
        val p = Item.Properties()
        builder.itemCustomizer?.invoke(p)
        BlockItem(blockHolder.get(), p)
    }

    val plan = PillarPlan(name, blockHolder, itemHolder, textureSide, textureEnd)
    PillarScope(plan, builder).apply(config)
    plans.add(plan)
    BlockWithItem(blockHolder, itemHolder)
}
