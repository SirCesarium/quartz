package net.sircesarium.qtz.api.block.type

import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.FenceGateBlock
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.block.state.properties.WoodType
import net.neoforged.neoforge.client.model.generators.BlockStateProvider
import net.sircesarium.qtz.api.DatagenPlan
import net.sircesarium.qtz.api.LootTableContext
import net.sircesarium.qtz.api.TagContext
import net.sircesarium.qtz.api.block.BlockBuilder
import net.sircesarium.qtz.api.block.BlockRegistry
import net.sircesarium.qtz.api.block.BlockWithItem
import net.sircesarium.qtz.utils.toSnakeCase
import net.neoforged.neoforge.registries.DeferredBlock
import net.neoforged.neoforge.registries.DeferredItem
import net.minecraft.tags.BlockTags

/**
 * Datagen plan for a [FenceGateBlock].
 *
 * Generates four models (gate, gate_open, gate_wall, gate_wall_open), a fence gate blockstate
 * via [BlockStateProvider.fenceGateBlock], a self-drop loot table, and registers
 * [BlockTags.FENCE_GATES] for connectivity.
 */
class FenceGatePlan internal constructor(
    private val id: String,
    val deferredBlock: DeferredBlock<FenceGateBlock>,
    val item: DeferredItem<BlockItem>,
    private val texture: ResourceLocation,
) : DatagenPlan {
    override val block: Block get() = deferredBlock.get()
    var blockState: Boolean = true
    var lootTable: Boolean = true

    override fun emitBlockState(provider: BlockStateProvider) {
        if (!blockState) return
        val gate = provider.models().fenceGate(id, texture)
        val gateOpen = provider.models().fenceGateOpen("${id}_open", texture)
        val gateWall = provider.models().fenceGateWall("${id}_wall", texture)
        val gateWallOpen = provider.models().fenceGateWallOpen("${id}_wall_open", texture)
        provider.fenceGateBlock(deferredBlock.get() as FenceGateBlock, gate, gateOpen, gateWall, gateWallOpen)
        provider.simpleBlockItem(deferredBlock.get(), gate)
    }

    override fun emitLootTable(context: LootTableContext) {
        if (!lootTable) return
        context.dropSelf(deferredBlock.get())
    }

    override fun emitTags(context: TagContext) {
        context.add(BlockTags.FENCE_GATES, deferredBlock.get())
    }
}

/**
 * Scope for configuring a [FenceGatePlan] inside [BlockRegistry.fenceGate].
 */
class FenceGateScope(private val plan: FenceGatePlan, private val builder: BlockBuilder<FenceGateBlock>) {
    var blockState by plan::blockState
    var lootTable by plan::lootTable

    fun configureBlock(action: BlockBehaviour.Properties.() -> Unit) {
        builder.configureBlock(action)
    }

    fun configureItem(action: Item.Properties.() -> Unit) {
        builder.configureItem(action)
    }
}

/**
 * Register a new [FenceGateBlock] using the given [baseBlock] for texture.
 *
 * The [woodType] controls the sound and appearance; defaults to [WoodType.OAK].
 *
 * | Property | Config block | Example |
 * |---|---|---|
 * | Name | `by fenceGate(base)` → `"oak_fence_gate"` |
 * | Block class | auto-set to [FenceGateBlock] |
 * | Block properties | `configureBlock { }` | `strength(2.0f)` |
 * | Item properties | `configureItem { }` | `stacksTo(64)` |
 * | Blockstate gen | `blockState = false` | disable |
 * | Loot table gen | `lootTable = false` | disable |
 *
 * ```
 * class ModBlocks : BlockRegistry("modid") {
 *     val oakGate by fenceGate(Blocks.OAK_PLANKS, WoodType.OAK)
 *     val spruceGate by fenceGate(Blocks.SPRUCE_PLANKS, WoodType.SPRUCE)
 * }
 * ```
 */
fun BlockRegistry.fenceGate(
    baseBlock: Block,
    woodType: WoodType = WoodType.OAK,
    config: FenceGateScope.() -> Unit = {},
) = bindName { rawName ->
    val name = rawName.toSnakeCase()
    val builder = BlockBuilder<FenceGateBlock>()
    val baseId = BuiltInRegistries.BLOCK.getKey(baseBlock)
    val texture = ResourceLocation.fromNamespaceAndPath(baseId.namespace, "block/${baseId.path}")

    val blockHolder = blocks.registerBlock(name) {
        val p = BlockBehaviour.Properties.ofFullCopy(baseBlock)
        builder.blockCustomizer?.invoke(p)
        FenceGateBlock(woodType, p)
    }

    val itemHolder = items.registerItem(name) {
        val p = Item.Properties()
        builder.itemCustomizer?.invoke(p)
        BlockItem(blockHolder.get(), p)
    }

    val plan = FenceGatePlan(name, blockHolder, itemHolder, texture)
    FenceGateScope(plan, builder).apply(config)
    plans.add(plan)
    BlockWithItem(blockHolder, itemHolder)
}
