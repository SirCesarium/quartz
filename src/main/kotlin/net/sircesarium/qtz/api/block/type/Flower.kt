package net.sircesarium.qtz.api.block.type

import net.minecraft.resources.ResourceLocation
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.FlowerBlock
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.state.BlockBehaviour
import net.neoforged.neoforge.client.model.generators.BlockStateProvider
import net.neoforged.neoforge.registries.DeferredBlock
import net.neoforged.neoforge.registries.DeferredItem
import net.sircesarium.qtz.api.DatagenPlan
import net.sircesarium.qtz.api.LootTableContext
import net.sircesarium.qtz.api.block.BlockBuilder
import net.sircesarium.qtz.api.block.BlockRegistry
import net.sircesarium.qtz.api.block.BlockWithItem
import net.sircesarium.qtz.api.block.createItem
import net.sircesarium.qtz.utils.toSnakeCase

class FlowerPlan internal constructor(
    private val id: String,
    val deferredBlock: DeferredBlock<FlowerBlock>,
    val item: DeferredItem<Item>,
    var texture: ResourceLocation,
) : DatagenPlan {
    override val block: Block get() = deferredBlock.get()
    var blockState: Boolean = true
    var lootTable: Boolean = true

    override fun emitBlockState(provider: BlockStateProvider) {
        if (!blockState) return
        val model = provider.models().cross(id, texture).renderType("cutout")
        provider.simpleBlock(deferredBlock.get(), model)
        provider.itemModels().singleTexture(id, provider.mcLoc("item/generated"), "layer0", texture)
    }

    override fun emitLootTable(context: LootTableContext) {
        if (!lootTable) return
        context.dropSelf(deferredBlock.get())
    }
}

class FlowerScope(private val plan: FlowerPlan, private val builder: BlockBuilder<FlowerBlock>) {
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

    fun texture(path: String) {
        plan.texture = ResourceLocation.parse(path)
    }
}

fun BlockRegistry.flower(
    config: FlowerScope.() -> Unit = {},
) = bindName { rawName ->
    val name = rawName.toSnakeCase()
    val builder = BlockBuilder<FlowerBlock>()
    val texture = ResourceLocation.fromNamespaceAndPath(modId, "block/${name}")

    val blockHolder = blocks.registerBlock(name) {
        val p = BlockBehaviour.Properties.of()
            .noCollission()
            .instabreak()
            .sound(SoundType.GRASS)
            .offsetType(BlockBehaviour.OffsetType.XZ)
        builder.blockCustomizer?.invoke(p)
        FlowerBlock(MobEffects.SATURATION, 7.0f, p)
    }

    val itemHolder = items.registerItem(name) { properties ->
        builder.itemCustomizer?.invoke(properties)
        createItem(builder, blockHolder, properties)
    }

    val plan = FlowerPlan(name, blockHolder, itemHolder, texture)
    FlowerScope(plan, builder).apply(config)
    plans.add(plan)
    BlockWithItem(blockHolder, itemHolder)
}
