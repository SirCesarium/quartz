package net.sircesarium.qtz.api.block.type

import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.ButtonBlock
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.block.state.properties.BlockSetType
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

class ButtonPlan internal constructor(
    private val id: String,
    val deferredBlock: DeferredBlock<ButtonBlock>,
    val item: DeferredItem<Item>,
    private val texture: ResourceLocation,
) : DatagenPlan {
    override val block: Block get() = deferredBlock.get()
    var blockState: Boolean = true
    var lootTable: Boolean = true

    override fun emitBlockState(provider: BlockStateProvider) {
        if (!blockState) return
        val inventory = provider.models().singleTexture("${id}_inventory", provider.mcLoc("block/button_inventory"), "texture", texture)
        provider.buttonBlock(deferredBlock.get() as ButtonBlock, texture)
        provider.simpleBlockItem(deferredBlock.get(), inventory)
    }

    override fun emitLootTable(context: LootTableContext) {
        if (!lootTable) return
        context.dropSelf(deferredBlock.get())
    }
}

class ButtonScope(private val plan: ButtonPlan, private val builder: BlockBuilder<ButtonBlock>) {
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

fun BlockRegistry.button(
    baseBlock: Block,
    blockSetType: BlockSetType = BlockSetType.STONE,
    ticks: Int = 20,
    config: ButtonScope.() -> Unit = {},
) = bindName { rawName ->
    val name = rawName.toSnakeCase()
    val builder = BlockBuilder<ButtonBlock>()
    val baseId = BuiltInRegistries.BLOCK.getKey(baseBlock)
    val texture = ResourceLocation.fromNamespaceAndPath(baseId.namespace, "block/${baseId.path}")

    val blockHolder = blocks.registerBlock(name) {
        val p = BlockBehaviour.Properties.of()
            .noCollission()
            .strength(0.5f)
            .sound(blockSetType.soundType())
        builder.blockCustomizer?.invoke(p)
        ButtonBlock(blockSetType, ticks, p)
    }

    val itemHolder = items.registerItem(name) { properties ->
        builder.itemCustomizer?.invoke(properties)
        createItem(builder, blockHolder, properties)
    }

    val plan = ButtonPlan(name, blockHolder, itemHolder, texture)
    ButtonScope(plan, builder).apply(config)
    plans.add(plan)
    BlockWithItem(blockHolder, itemHolder)
}
