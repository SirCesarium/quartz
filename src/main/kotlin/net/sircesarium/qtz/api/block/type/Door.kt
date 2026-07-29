package net.sircesarium.qtz.api.block.type

import net.minecraft.advancements.critereon.StatePropertiesPredicate
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.DoorBlock
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.block.state.properties.BlockSetType
import net.minecraft.world.level.storage.loot.LootPool
import net.minecraft.world.level.storage.loot.LootTable
import net.minecraft.world.level.storage.loot.entries.LootItem
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue
import net.neoforged.neoforge.client.model.generators.BlockStateProvider
import net.sircesarium.qtz.api.DatagenPlan
import net.sircesarium.qtz.api.LootTableContext
import net.sircesarium.qtz.api.block.BlockBuilder
import net.sircesarium.qtz.api.block.BlockRegistry
import net.sircesarium.qtz.api.block.BlockWithItem
import net.sircesarium.qtz.api.block.createItem
import net.sircesarium.qtz.utils.toSnakeCase
import net.neoforged.neoforge.registries.DeferredBlock
import net.neoforged.neoforge.registries.DeferredItem

class DoorPlan internal constructor(
    private val id: String,
    val deferredBlock: DeferredBlock<DoorBlock>,
    val item: DeferredItem<Item>,
    var textureBottom: ResourceLocation,
    var textureTop: ResourceLocation,
    private val itemTexture: ResourceLocation,
) : DatagenPlan {
    override val block: Block get() = deferredBlock.get()
    var blockState: Boolean = true
    var lootTable: Boolean = true

    override fun emitBlockState(provider: BlockStateProvider) {
        if (!blockState) return
        provider.models().singleTexture("${id}_bottom_left", provider.mcLoc("block/door_bottom_left"), "bottom", textureBottom).texture("top", textureTop).renderType("cutout")
        provider.models().singleTexture("${id}_bottom_left_open", provider.mcLoc("block/door_bottom_left_open"), "bottom", textureBottom).texture("top", textureTop).renderType("cutout")
        provider.models().singleTexture("${id}_bottom_right", provider.mcLoc("block/door_bottom_right"), "bottom", textureBottom).texture("top", textureTop).renderType("cutout")
        provider.models().singleTexture("${id}_bottom_right_open", provider.mcLoc("block/door_bottom_right_open"), "bottom", textureBottom).texture("top", textureTop).renderType("cutout")
        provider.models().singleTexture("${id}_top_left", provider.mcLoc("block/door_top_left"), "bottom", textureBottom).texture("top", textureTop).renderType("cutout")
        provider.models().singleTexture("${id}_top_left_open", provider.mcLoc("block/door_top_left_open"), "bottom", textureBottom).texture("top", textureTop).renderType("cutout")
        provider.models().singleTexture("${id}_top_right", provider.mcLoc("block/door_top_right"), "bottom", textureBottom).texture("top", textureTop).renderType("cutout")
        provider.models().singleTexture("${id}_top_right_open", provider.mcLoc("block/door_top_right_open"), "bottom", textureBottom).texture("top", textureTop).renderType("cutout")
        provider.doorBlock(deferredBlock.get() as DoorBlock, textureBottom, textureTop)
        provider.itemModels().singleTexture(id, provider.mcLoc("item/generated"), "layer0", itemTexture)
    }

    override fun emitLootTable(context: LootTableContext) {
        if (!lootTable) return
        val condition = LootItemBlockStatePropertyCondition.hasBlockStateProperties(deferredBlock.get())
            .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(DoorBlock.HALF, "lower"))
        val table = LootTable.lootTable().withPool(
            LootPool.lootPool()
                .setRolls(ConstantValue.exactly(1.0f))
                .add(LootItem.lootTableItem(deferredBlock.get()).`when`(condition))
        )
        context.addLootTable(deferredBlock.get(), table)
    }
}

class DoorScope(private val plan: DoorPlan, private val builder: BlockBuilder<DoorBlock>) {
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

    fun textureBottom(path: String) {
        plan.textureBottom = ResourceLocation.parse(path)
    }

    fun textureTop(path: String) {
        plan.textureTop = ResourceLocation.parse(path)
    }
}

fun BlockRegistry.door(
    blockSetType: BlockSetType = BlockSetType.OAK,
    textureBottom: ResourceLocation? = null,
    textureTop: ResourceLocation? = null,
    config: DoorScope.() -> Unit = {},
) = bindName { rawName ->
    val name = rawName.toSnakeCase()
    val builder = BlockBuilder<DoorBlock>()
    val texBottom = textureBottom ?: ResourceLocation.fromNamespaceAndPath(modId, "block/${name}_bottom")
    val texTop = textureTop ?: ResourceLocation.fromNamespaceAndPath(modId, "block/${name}_top")

    val blockHolder = blocks.registerBlock(name) {
        val p = BlockBehaviour.Properties.of()
            .noOcclusion()
            .strength(3.0f)
            .sound(blockSetType.soundType())
        builder.blockCustomizer?.invoke(p)
        DoorBlock(blockSetType, p)
    }

    val itemHolder = items.registerItem(name) { properties ->
        builder.itemCustomizer?.invoke(properties)
        createItem(builder, blockHolder, properties)
    }

    val itemTexture = ResourceLocation.fromNamespaceAndPath(modId, "item/${name}")
    val plan = DoorPlan(name, blockHolder, itemHolder, texBottom, texTop, itemTexture)
    DoorScope(plan, builder).apply(config)
    plans.add(plan)
    BlockWithItem(blockHolder, itemHolder)
}
