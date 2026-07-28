package net.sircesarium.qtz.api.block.type

import net.minecraft.resources.ResourceLocation
import net.minecraft.advancements.critereon.StatePropertiesPredicate
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.TallFlowerBlock
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf
import net.minecraft.world.level.storage.loot.LootPool
import net.minecraft.world.level.storage.loot.LootTable
import net.minecraft.world.level.storage.loot.entries.LootItem
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue
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

class TallFlowerPlan internal constructor(
    private val id: String,
    val deferredBlock: DeferredBlock<TallFlowerBlock>,
    val item: DeferredItem<Item>,
    private val textureBottom: ResourceLocation,
    private val textureTop: ResourceLocation,
) : DatagenPlan {
    override val block: Block get() = deferredBlock.get()
    var blockState: Boolean = true
    var lootTable: Boolean = true

    override fun emitBlockState(provider: BlockStateProvider) {
        if (!blockState) return
        val bottomModel = provider.models().cross("${id}_bottom", textureBottom).renderType("cutout")
        val topModel = provider.models().cross("${id}_top", textureTop).renderType("cutout")
        provider.getVariantBuilder(deferredBlock.get())
            .partialState()
            .with(TallFlowerBlock.HALF, DoubleBlockHalf.LOWER)
            .addModels(ConfiguredModel.builder().modelFile(bottomModel).buildLast())
            .partialState()
            .with(TallFlowerBlock.HALF, DoubleBlockHalf.UPPER)
            .addModels(ConfiguredModel.builder().modelFile(topModel).buildLast())
        provider.itemModels().singleTexture(id, provider.mcLoc("item/generated"), "layer0", textureTop)
    }

    override fun emitLootTable(context: LootTableContext) {
        if (!lootTable) return
        val condition = LootItemBlockStatePropertyCondition.hasBlockStateProperties(deferredBlock.get())
            .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(TallFlowerBlock.HALF, "lower"))
        val table = LootTable.lootTable().withPool(
            LootPool.lootPool()
                .setRolls(ConstantValue.exactly(1.0f))
                .add(LootItem.lootTableItem(deferredBlock.get()).`when`(condition))
        )
        context.addLootTable(deferredBlock.get(), table)
    }
}

class TallFlowerScope(private val plan: TallFlowerPlan, private val builder: BlockBuilder<TallFlowerBlock>) {
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

fun BlockRegistry.tallFlower(
    textureBottom: ResourceLocation? = null,
    textureTop: ResourceLocation? = null,
    config: TallFlowerScope.() -> Unit = {},
) = bindName { rawName ->
    val name = rawName.toSnakeCase()
    val builder = BlockBuilder<TallFlowerBlock>()
    val texBottom = textureBottom ?: ResourceLocation.fromNamespaceAndPath(modId, "block/${name}_bottom")
    val texTop = textureTop ?: ResourceLocation.fromNamespaceAndPath(modId, "block/${name}_top")

    val blockHolder = blocks.registerBlock(name) {
        val p = BlockBehaviour.Properties.of()
            .noCollission()
            .instabreak()
            .sound(SoundType.GRASS)
            .offsetType(BlockBehaviour.OffsetType.XZ)
        builder.blockCustomizer?.invoke(p)
        TallFlowerBlock(p)
    }

    val itemHolder = items.registerItem(name) { properties ->
        builder.itemCustomizer?.invoke(properties)
        createItem(builder, blockHolder, properties)
    }

    val plan = TallFlowerPlan(name, blockHolder, itemHolder, texBottom, texTop)
    TallFlowerScope(plan, builder).apply(config)
    plans.add(plan)
    BlockWithItem(blockHolder, itemHolder)
}
