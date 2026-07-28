package net.sircesarium.qtz.api.block.type

import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.BlockTags
import net.minecraft.tags.ItemTags
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.FenceBlock
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
 * Datagen plan for a [FenceBlock].
 *
 * Generates post, side, and inventory models, a 4-way blockstate via
 * [BlockStateProvider.fourWayBlock], a self-drop loot table, and registers
 * [BlockTags.FENCES] and [ItemTags.FENCES].
 */
class FencePlan internal constructor(
    private val id: String,
    val deferredBlock: DeferredBlock<FenceBlock>,
    val item: DeferredItem<BlockItem>,
    private val texture: ResourceLocation,
) : DatagenPlan {
    override val block: Block get() = deferredBlock.get()
    var blockState: Boolean = true
    var lootTable: Boolean = true
    var renderType: String? = null

    override fun emitBlockState(provider: BlockStateProvider) {
        if (!blockState) return
        val post = provider.models().fencePost("${id}_post", texture)
        val side = provider.models().fenceSide("${id}_side", texture)
        val inventory = provider.models().fenceInventory("${id}_inventory", texture)
        renderType?.let {
            post.renderType(it)
            side.renderType(it)
            inventory.renderType(it)
        }
        provider.fourWayBlock(deferredBlock.get() as FenceBlock, post, side)
        provider.simpleBlockItem(deferredBlock.get(), inventory)
    }

    override fun emitLootTable(context: LootTableContext) {
        if (!lootTable) return
        context.dropSelf(deferredBlock.get())
    }

    override fun emitTags(context: TagContext) {
        context.add(BlockTags.FENCES, deferredBlock.get())
        context.add(ItemTags.FENCES, deferredBlock.get())
    }
}

/**
 * Scope for configuring a [FencePlan] inside [BlockRegistry.fence].
 */
class FenceScope(private val plan: FencePlan, private val builder: BlockBuilder<FenceBlock>) {
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
 * Creates a fence variant of the given [baseBlock] base material.
 *
 * Inherits the sound type from the base block.
 * Textures are resolved from the base block's registry ID (any namespace).
 * Auto-registers [BlockTags.FENCES] and [ItemTags.FENCES] for connectivity/tool compatibility.
 *
 * | Property | Config block | Example |
 * |---|---|---|
 * | Name | `by fence(base)` → `"stone_fence"` |
 * | Block class | auto-set to `FenceBlock` |
 * | Block properties | `configureBlock { }` | `strength(2.0f)`, `noCollision()` |
 * | Item properties | `configureItem { }` | `stacksTo(16)` |
 * | Block state + model | `blockState = true/false` | disable to provide custom |
 * | Loot table | `lootTable = true/false` | disable to provide custom |
 * | Render type | `renderType = "cutout"` | for transparent blocks |
 *
 * Datagen is auto-generated — each fence owns its plan.
 * Flags are per-fence. Disabling one does not affect others.
 *
 * ```
 * class ModBlocks : BlockRegistry("modid") {
 *     val stoneFence by fence(Blocks.STONE)
 *
 *     val glassFence by fence(Blocks.GLASS) {
 *         renderType = "cutout"
 *         configureBlock { noOcclusion() }
 *     }
 * }
 * ```
 */
fun BlockRegistry.fence(
    baseBlock: Block,
    config: FenceScope.() -> Unit = {},
) = bindName { rawName ->
    val name = rawName.toSnakeCase()
    val builder = BlockBuilder<FenceBlock>()
    val baseId = BuiltInRegistries.BLOCK.getKey(baseBlock)
    val texture = ResourceLocation.fromNamespaceAndPath(baseId.namespace, "block/${baseId.path}")

    val blockHolder = blocks.registerBlock(name) {
        val p = BlockBehaviour.Properties.of()
            .sound(baseBlock.soundType)
        builder.blockCustomizer?.invoke(p)
        FenceBlock(p)
    }

    val itemHolder = items.registerItem(name) {
        val p = Item.Properties()
        builder.itemCustomizer?.invoke(p)
        BlockItem(blockHolder.get(), p)
    }

    val plan = FencePlan(name, blockHolder, itemHolder, texture)
    FenceScope(plan, builder).apply(config)
    plans.add(plan)
    BlockWithItem(blockHolder, itemHolder)
}
