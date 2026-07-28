package net.sircesarium.qtz.api.block.type

import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.TrapDoorBlock
import net.minecraft.core.Direction
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.block.state.properties.BlockSetType
import net.minecraft.world.level.block.state.properties.Half
import net.neoforged.neoforge.client.model.generators.BlockStateProvider
import net.neoforged.neoforge.client.model.generators.ConfiguredModel
import net.sircesarium.qtz.api.DatagenPlan
import net.sircesarium.qtz.api.LootTableContext
import net.sircesarium.qtz.api.block.BlockBuilder
import net.sircesarium.qtz.api.block.BlockRegistry
import net.sircesarium.qtz.api.block.BlockWithItem
import net.sircesarium.qtz.api.block.createItem
import net.sircesarium.qtz.utils.toSnakeCase
import net.neoforged.neoforge.registries.DeferredBlock
import net.neoforged.neoforge.registries.DeferredItem

class TrapdoorPlan internal constructor(
    private val id: String,
    val deferredBlock: DeferredBlock<TrapDoorBlock>,
    val item: DeferredItem<Item>,
    private val texture: ResourceLocation,
) : DatagenPlan {
    override val block: Block get() = deferredBlock.get()
    var blockState: Boolean = true
    var lootTable: Boolean = true

    override fun emitBlockState(provider: BlockStateProvider) {
        if (!blockState) return
        val bottom = provider.models().singleTexture(id + "_bottom", provider.mcLoc("block/template_trapdoor_bottom"), "texture", texture).renderType("cutout")
        val top = provider.models().singleTexture(id + "_top", provider.mcLoc("block/template_trapdoor_top"), "texture", texture).renderType("cutout")
        val open = provider.models().singleTexture(id + "_open", provider.mcLoc("block/template_trapdoor_open"), "texture", texture).renderType("cutout")
        val block = deferredBlock.get() as TrapDoorBlock
        provider.getVariantBuilder(block).forAllStates { state ->
            val facing = state.getValue(TrapDoorBlock.FACING)
            val half = state.getValue(TrapDoorBlock.HALF)
            val isOpen = state.getValue(TrapDoorBlock.OPEN)
            val model = when {
                isOpen -> open
                half == Half.BOTTOM -> bottom
                else -> top
            }
            val y = when (facing) {
                Direction.EAST -> 90
                Direction.SOUTH -> 180
                Direction.WEST -> 270
                else -> 0
            }
            arrayOf(ConfiguredModel.builder().modelFile(model).rotationY(y).buildLast())
        }
        provider.simpleBlockItem(block, bottom)
    }

    override fun emitLootTable(context: LootTableContext) {
        if (!lootTable) return
        context.dropSelf(deferredBlock.get())
    }
}

class TrapdoorScope(private val plan: TrapdoorPlan, private val builder: BlockBuilder<TrapDoorBlock>) {
    var blockState by plan::blockState
    var lootTable by plan::lootTable

    fun configureBlock(action: BlockBehaviour.Properties.() -> Unit) {
        builder.configureBlock(action)
    }

    fun configureItem(action: Item.Properties.() -> Unit) {
        builder.configureItem(action)
    }

    fun customItem(factory: (Block, Item.Properties) -> Item) {
        builder.customItem(factory)
    }
}

fun BlockRegistry.trapdoor(
    blockSetType: BlockSetType = BlockSetType.OAK,
    config: TrapdoorScope.() -> Unit = {},
) = bindName { rawName ->
    val name = rawName.toSnakeCase()
    val builder = BlockBuilder<TrapDoorBlock>()
    val texture = ResourceLocation.fromNamespaceAndPath(modId, "block/${name}")

    val blockHolder = blocks.registerBlock(name) {
        val p = BlockBehaviour.Properties.of()
            .noOcclusion()
            .sound(blockSetType.soundType())
        builder.blockCustomizer?.invoke(p)
        TrapDoorBlock(blockSetType, p)
    }

    val itemHolder = items.registerItem(name) { properties ->
        builder.itemCustomizer?.invoke(properties)
        createItem(builder, blockHolder, properties)
    }

    val plan = TrapdoorPlan(name, blockHolder, itemHolder, texture)
    TrapdoorScope(plan, builder).apply(config)
    plans.add(plan)
    BlockWithItem(blockHolder, itemHolder)
}
