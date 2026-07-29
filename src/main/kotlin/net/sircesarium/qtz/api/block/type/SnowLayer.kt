package net.sircesarium.qtz.api.block.type

import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.neoforged.neoforge.client.model.generators.BlockStateProvider
import net.neoforged.neoforge.client.model.generators.ConfiguredModel
import net.neoforged.neoforge.client.model.generators.ModelFile
import net.sircesarium.qtz.api.DatagenPlan
import net.sircesarium.qtz.api.LootTableContext
import net.sircesarium.qtz.api.block.BlockBuilder
import net.sircesarium.qtz.api.block.BlockRegistry
import net.sircesarium.qtz.api.block.BlockWithItem
import net.sircesarium.qtz.api.block.createItem
import net.sircesarium.qtz.utils.soundType
import net.sircesarium.qtz.utils.toSnakeCase
import net.neoforged.neoforge.registries.DeferredBlock
import net.neoforged.neoforge.registries.DeferredItem
import net.sircesarium.qtz.custom.block.LayerBlock
import net.sircesarium.qtz.custom.block.WaterloggableLayerBlock

class LayerPlan internal constructor(
    private val id: String,
    val deferredBlock: DeferredBlock<*>,
    val item: DeferredItem<Item>,
    var texture: ResourceLocation,
) : DatagenPlan {
    override val block: Block get() = deferredBlock.get()
    var blockState: Boolean = true
    var lootTable: Boolean = true

    override fun emitBlockState(provider: BlockStateProvider) {
        if (!blockState) return
        val models = (1..8).associateWith { layer ->
            val height = layer * 2
            val model = if (height == 16) {
                provider.models().withExistingParent("${id}_height${height}", provider.mcLoc("block/cube_all"))
                    .texture("all", texture)
            } else {
                provider.models().withExistingParent("${id}_height${height}", provider.mcLoc("block/snow_height${height}"))
                    .texture("texture", texture)
            }
            model.texture("particle", texture)
        }
        provider.getVariantBuilder(deferredBlock.get())
            .forAllStates { state ->
                val layers = state.getValue(BlockStateProperties.LAYERS)
                ConfiguredModel.builder().modelFile(models[layers] as ModelFile).build()
            }
        provider.simpleBlockItem(deferredBlock.get(), models[1] as ModelFile)
    }

    override fun emitLootTable(context: LootTableContext) {
        if (!lootTable) return
        context.dropLayered(deferredBlock.get(), BlockStateProperties.LAYERS, 1)
    }
}

class LayerScope(private val plan: LayerPlan, private val builder: BlockBuilder<*>) {
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

fun BlockRegistry.snowLayer(
    baseBlock: Block? = null,
    waterloggable: Boolean = false,
    config: LayerScope.() -> Unit = {},
) = layer(baseBlock = baseBlock, waterloggable = waterloggable, config = config)

fun BlockRegistry.layer(
    baseBlock: Block? = null,
    waterloggable: Boolean = false,
    config: LayerScope.() -> Unit = {},
) = bindName { rawName ->
    val name = rawName.toSnakeCase()
    val builder = BlockBuilder<LayerBlock>()
    val texture = if (baseBlock != null) {
        val baseId = net.minecraft.core.registries.BuiltInRegistries.BLOCK.getKey(baseBlock)
        ResourceLocation.fromNamespaceAndPath(baseId.namespace, "block/${baseId.path}")
    } else {
        ResourceLocation.fromNamespaceAndPath(modId, "block/${name}")
    }

    val blockHolder = blocks.registerBlock(name) {
        val p = BlockBehaviour.Properties.of()
            .sound(baseBlock?.soundType ?: net.minecraft.world.level.block.SoundType.STONE)
        builder.blockCustomizer?.invoke(p)
        if (waterloggable) WaterloggableLayerBlock(p) else LayerBlock(p)
    }

    val itemHolder = items.registerItem(name) { properties ->
        builder.itemCustomizer?.invoke(properties)
        createItem(builder, blockHolder, properties)
    }

    val plan = LayerPlan(name, blockHolder, itemHolder, texture)
    LayerScope(plan, builder).apply(config)
    plans.add(plan)
    BlockWithItem(blockHolder, itemHolder)
}
