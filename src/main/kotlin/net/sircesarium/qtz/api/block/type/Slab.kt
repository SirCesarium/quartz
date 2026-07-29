package net.sircesarium.qtz.api.block.type

import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.SlabBlock
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
 * Datagen plan for a [SlabBlock].
 *
 * Generates bottom, top, and double models, plus a slab blockstate with
 * [BlockStateProvider.slabBlock] and the appropriate slab loot table.
 */
class SlabPlan internal constructor(
    private val id: String,
    val deferredBlock: DeferredBlock<SlabBlock>,
    val item: DeferredItem<BlockItem>,
    var texture: ResourceLocation,
) : DatagenPlan {
    override val block: Block get() = deferredBlock.get()
    var blockState: Boolean = true
    var lootTable: Boolean = true
    var renderType: String? = null

    override fun emitBlockState(provider: BlockStateProvider) {
        if (!blockState) return
        val bottomModel = provider.models().slab(id, texture, texture, texture)
        val topModel = provider.models().slabTop("${id}_top", texture, texture, texture)
        val doubleModel = provider.models().cubeAll("${id}_double", texture)
        renderType?.let {
            bottomModel.renderType(it)
            topModel.renderType(it)
            doubleModel.renderType(it)
        }
        provider.slabBlock(deferredBlock.get() as SlabBlock, bottomModel, topModel, doubleModel)
        provider.simpleBlockItem(deferredBlock.get(), bottomModel)
    }

    override fun emitLootTable(context: LootTableContext) {
        if (!lootTable) return
        context.dropSlab(deferredBlock.get())
    }
}

/**
 * Scope for configuring a [SlabPlan] inside [BlockRegistry.slab].
 */
class SlabScope(private val plan: SlabPlan, private val builder: BlockBuilder<SlabBlock>) {
    var blockState by plan::blockState
    var lootTable by plan::lootTable
    var renderType by plan::renderType

    fun configureBlock(action: BlockBehaviour.Properties.() -> Unit) {
        builder.configureBlock(action)
    }

    fun configureItem(action: Item.Properties.() -> Unit) {
        builder.configureItem(action)
    }

    fun texture(path: String) {
        plan.texture = ResourceLocation.parse(path)
    }
}

/**
 * Creates a slab variant of the given [baseBlock] base material.
 *
 * Inherits the sound type from the base block.
 * Textures are resolved from the base block's registry ID (any namespace).
 *
 * | Property | Config block | Example |
 * |---|---|---|
 * | Name | `by slab(base)` → `"stone_slab"` |
 * | Block class | auto-set to `SlabBlock` |
 * | Block properties | `configureBlock { }` | `strength(2.0f)`, `noCollision()` |
 * | Item properties | `configureItem { }` | `stacksTo(16)` |
 * | Texture override | `texture("path")` | `texture("minecraft:block/stone")` |
 * | Block state + model | `blockState = true/false` | disable to provide custom |
 * | Loot table | `lootTable = true/false` | disable to provide custom |
 *
 * Datagen is auto-generated — each slab owns its plan.
 * Flags are per-slab. Disabling one does not affect others.
 *
 * ```
 * class ModBlocks : BlockRegistry("modid") {
 *     val stoneSlab by slab(Blocks.STONE)
 *
 *     val customSlab by slab(Blocks.STONE) {
 *         texture("minecraft:block/polished_andesite")
 *         configureBlock { noCollision(); lightLevel { 8 } }
 *     }
 * }
 * ```
 */
fun BlockRegistry.slab(
    config: SlabScope.() -> Unit = {},
) = bindName { rawName ->
    val name = rawName.toSnakeCase()
    val builder = BlockBuilder<SlabBlock>()
    val textureName = name.removeSuffix("_slab")
    val texture = ResourceLocation.fromNamespaceAndPath(modId, "block/${textureName}")

    val blockHolder = blocks.registerBlock(name) {
        val p = BlockBehaviour.Properties.of()
        builder.blockCustomizer?.invoke(p)
        SlabBlock(p)
    }

    val itemHolder = items.registerItem(name) {
        val p = Item.Properties()
        builder.itemCustomizer?.invoke(p)
        BlockItem(blockHolder.get(), p)
    }

    val plan = SlabPlan(name, blockHolder, itemHolder, texture)
    SlabScope(plan, builder).apply(config)
    plans.add(plan)
    BlockWithItem(blockHolder, itemHolder)
}

fun BlockRegistry.slab(
    baseBlock: Block,
    config: SlabScope.() -> Unit = {},
) = bindName { rawName ->
    val name = rawName.toSnakeCase()
    val builder = BlockBuilder<SlabBlock>()
    val baseId = BuiltInRegistries.BLOCK.getKey(baseBlock)
    val texture = ResourceLocation.fromNamespaceAndPath(baseId.namespace, "block/${baseId.path}")

    val blockHolder = blocks.registerBlock(name) {
        val p = BlockBehaviour.Properties.of()
            .sound(baseBlock.soundType)
        builder.blockCustomizer?.invoke(p)
        SlabBlock(p)
    }

    val itemHolder = items.registerItem(name) {
        val p = Item.Properties()
        builder.itemCustomizer?.invoke(p)
        BlockItem(blockHolder.get(), p)
    }

    val plan = SlabPlan(name, blockHolder, itemHolder, texture)
    SlabScope(plan, builder).apply(config)
    plans.add(plan)
    BlockWithItem(blockHolder, itemHolder)
}
