package net.sircesarium.qtz.api.block.type

import net.minecraft.core.Direction
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.core.particles.SimpleParticleType
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item
import net.minecraft.world.item.StandingAndWallBlockItem
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.TorchBlock
import net.minecraft.world.level.block.WallTorchBlock
import net.minecraft.world.level.block.state.BlockBehaviour
import net.neoforged.neoforge.client.model.generators.BlockStateProvider
import net.neoforged.neoforge.client.model.generators.ConfiguredModel
import net.neoforged.neoforge.registries.DeferredBlock
import net.neoforged.neoforge.registries.DeferredItem
import net.sircesarium.qtz.api.DatagenPlan
import net.sircesarium.qtz.api.LootTableContext
import net.sircesarium.qtz.api.block.BlockBuilder
import net.sircesarium.qtz.api.block.BlockRegistry
import net.sircesarium.qtz.api.block.BlockWithItem
import net.sircesarium.qtz.utils.toSnakeCase

class TorchPlan internal constructor(
    private val id: String,
    val deferredBlock: DeferredBlock<TorchBlock>,
    val wallDeferredBlock: DeferredBlock<WallTorchBlock>,
    val item: DeferredItem<Item>,
    private val texture: ResourceLocation,
) : DatagenPlan {
    override val block: Block get() = deferredBlock.get()
    override val knownBlocks: List<Block> get() = listOf(block, wallBlock)
    val wallBlock: WallTorchBlock get() = wallDeferredBlock.get()
    var blockState: Boolean = true
    var lootTable: Boolean = true

    override fun emitBlockState(provider: BlockStateProvider) {
        if (!blockState) return

        val torchModel = provider.models().torch(id, texture).renderType("cutout")
        val wallModel = provider.models().torchWall("${id}_wall", texture).renderType("cutout")

        provider.simpleBlock(deferredBlock.get(), torchModel)
        provider.getVariantBuilder(wallDeferredBlock.get())
            .forAllStates { state ->
                val facing = state.getValue(WallTorchBlock.FACING)
                val y = when (facing) {
                    Direction.NORTH -> 270
                    Direction.SOUTH -> 90
                    Direction.WEST -> 180
                    else -> 0 // EAST
                }
                ConfiguredModel.builder().modelFile(wallModel).rotationY(y).build()
            }
        provider.itemModels().singleTexture(id, provider.mcLoc("item/generated"), "layer0", texture)
    }

    override fun emitLootTable(context: LootTableContext) {
        if (!lootTable) return
        context.dropSelf(deferredBlock.get())
        context.dropOther(wallDeferredBlock.get(), deferredBlock.get())
    }
}

class TorchScope(private val plan: TorchPlan, private val builder: BlockBuilder<TorchBlock>) {
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

fun BlockRegistry.torch(
    particle: SimpleParticleType = ParticleTypes.FLAME,
    config: TorchScope.() -> Unit = {},
) = bindName { rawName ->
    val name = rawName.toSnakeCase()
    val builder = BlockBuilder<TorchBlock>()
    val texture = ResourceLocation.fromNamespaceAndPath(modId, "block/${name}")

    val blockHolder = blocks.registerBlock(name) {
        val p = BlockBehaviour.Properties.of()
            .noCollission()
            .lightLevel { 14 }
            .sound(SoundType.WOOD)
        builder.blockCustomizer?.invoke(p)
        TorchBlock(particle, p)
    }

    val wallBlockHolder = blocks.registerBlock("wall_$name") {
        val p = BlockBehaviour.Properties.of()
            .noCollission()
            .lightLevel { 14 }
            .sound(SoundType.WOOD)
        WallTorchBlock(particle, p)
    }

    val itemHolder = items.registerItem(name) { properties ->
        builder.itemCustomizer?.invoke(properties)
        builder.itemFactory?.invoke(blockHolder.get(), properties)
            ?: StandingAndWallBlockItem(blockHolder.get(), wallBlockHolder.get(), properties, Direction.DOWN)
    }

    val plan = TorchPlan(name, blockHolder, wallBlockHolder, itemHolder, texture)
    TorchScope(plan, builder).apply(config)
    plans.add(plan)
    BlockWithItem(blockHolder, itemHolder)
}
