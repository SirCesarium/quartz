package net.sircesarium.qtz.api.block.type

import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.PressurePlateBlock
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.block.state.properties.BlockSetType
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

class PressurePlatePlan internal constructor(
    private val id: String,
    val deferredBlock: DeferredBlock<PressurePlateBlock>,
    val item: DeferredItem<Item>,
    private val texture: ResourceLocation,
) : DatagenPlan {
    override val block: Block get() = deferredBlock.get()
    var blockState: Boolean = true
    var lootTable: Boolean = true

    override fun emitBlockState(provider: BlockStateProvider) {
        if (!blockState) return
        val up = provider.models().singleTexture(id, provider.mcLoc("block/pressure_plate_up"), "texture", texture)
        val down = provider.models().singleTexture("${id}_down", provider.mcLoc("block/pressure_plate_down"), "texture", texture)
        provider.getVariantBuilder(deferredBlock.get())
            .partialState().with(PressurePlateBlock.POWERED, true).addModels(ConfiguredModel.builder().modelFile(down).buildLast())
            .partialState().with(PressurePlateBlock.POWERED, false).addModels(ConfiguredModel.builder().modelFile(up).buildLast())
        provider.simpleBlockItem(deferredBlock.get(), up)
    }

    override fun emitLootTable(context: LootTableContext) {
        if (!lootTable) return
        context.dropSelf(deferredBlock.get())
    }
}

class PressurePlateScope(private val plan: PressurePlatePlan, private val builder: BlockBuilder<PressurePlateBlock>) {
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

fun BlockRegistry.pressurePlate(
    baseBlock: Block,
    blockSetType: BlockSetType = BlockSetType.STONE,
    config: PressurePlateScope.() -> Unit = {},
) = bindName { rawName ->
    val name = rawName.toSnakeCase()
    val builder = BlockBuilder<PressurePlateBlock>()
    val baseId = BuiltInRegistries.BLOCK.getKey(baseBlock)
    val texture = ResourceLocation.fromNamespaceAndPath(baseId.namespace, "block/${baseId.path}")

    val blockHolder = blocks.registerBlock(name) {
        val p = BlockBehaviour.Properties.of()
            .noCollission()
            .strength(0.5f)
            .sound(blockSetType.soundType())
        builder.blockCustomizer?.invoke(p)
        PressurePlateBlock(blockSetType, p)
    }

    val itemHolder = items.registerItem(name) { properties ->
        builder.itemCustomizer?.invoke(properties)
        createItem(builder, blockHolder, properties)
    }

    val plan = PressurePlatePlan(name, blockHolder, itemHolder, texture)
    PressurePlateScope(plan, builder).apply(config)
    plans.add(plan)
    BlockWithItem(blockHolder, itemHolder)
}
