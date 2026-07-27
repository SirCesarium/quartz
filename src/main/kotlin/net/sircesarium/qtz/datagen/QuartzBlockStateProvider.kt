package net.sircesarium.qtz.datagen

import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.data.PackOutput
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.block.RotatedPillarBlock
import net.minecraft.world.level.block.SlabBlock
import net.minecraft.world.level.block.SnowLayerBlock
import net.minecraft.world.level.block.StairBlock
import net.neoforged.neoforge.client.model.generators.BlockStateProvider
import net.neoforged.neoforge.client.model.generators.ConfiguredModel
import net.neoforged.neoforge.common.data.ExistingFileHelper
import net.sircesarium.qtz.api.datagen.BlockShape

class QuartzBlockStateProvider(
    output: PackOutput,
    private val modid: String,
    efh: ExistingFileHelper,
    private val blockModels: List<Triple<String, String, BlockShape>>,
    private val slabBlocks: List<Triple<String, String, String>>,
    private val stairBlocks: List<Triple<String, String, String>>,
    private val snowLayerBlocks: List<Triple<String, String, String>>,
) : BlockStateProvider(output, modid, efh) {

    override fun registerStatesAndModels() {
        registerSimpleModels()
        registerSlabModels()
        registerStairModels()
        registerSnowLayerModels()
    }

    private fun registerSimpleModels() {
        for ((_, blockName, shape) in blockModels) {
            val block = BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(modid, blockName))

            when (shape) {
                is BlockShape.CubeAll -> {
                    simpleBlockWithItem(block, cubeAll(block))
                }
                is BlockShape.CubeColumn -> {
                    val pillar = block as RotatedPillarBlock
                    val name = BuiltInRegistries.BLOCK.getKey(pillar).path
                    val side = blockTexture(pillar)
                    val end = ResourceLocation.fromNamespaceAndPath(side.namespace, side.path + shape.endSuffix)
                    val vertical = models().cubeColumn(name, side, end)
                    val horizontal = models().cubeColumnHorizontal(name + "_horizontal", side, end)

                    axisBlock(pillar, vertical, horizontal)
                    simpleBlockItem(pillar, vertical)
                }
            }
        }
    }

    private fun registerSlabModels() {
        for ((_, blockName, baseTexture) in slabBlocks) {
            val slab = BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(modid, blockName)) as SlabBlock
            val name = BuiltInRegistries.BLOCK.getKey(slab).path
            val baseId = ResourceLocation.parse(baseTexture)
            val texture = ResourceLocation.fromNamespaceAndPath(baseId.namespace, "block/${baseId.path}")
            val bottomModel = models().slab(name, texture, texture, texture)
            val topModel = models().slabTop("${name}_top", texture, texture, texture)
            val doubleModel = models().cubeAll("${name}_double", texture)

            slabBlock(slab, bottomModel, topModel, doubleModel)
            simpleBlockItem(slab, bottomModel)
        }
    }

    private fun registerStairModels() {
        for ((_, blockName, baseTexture) in stairBlocks) {
            val stair = BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(modid, blockName)) as StairBlock
            val name = BuiltInRegistries.BLOCK.getKey(stair).path
            val baseId = ResourceLocation.parse(baseTexture)
            val texture = ResourceLocation.fromNamespaceAndPath(baseId.namespace, "block/${baseId.path}")
            val straightModel = models().stairs(name, texture, texture, texture)
            val innerModel = models().stairsInner("${name}_inner", texture, texture, texture)
            val outerModel = models().stairsOuter("${name}_outer", texture, texture, texture)

            stairsBlock(stair, straightModel, innerModel, outerModel)
            simpleBlockItem(stair, straightModel)
        }
    }

    private fun registerSnowLayerModels() {
        for ((_, blockName, baseTexture) in snowLayerBlocks) {
            val layer = BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(modid, blockName)) as SnowLayerBlock
            val name = BuiltInRegistries.BLOCK.getKey(layer).path
            val baseId = ResourceLocation.parse(baseTexture)
            val texture = ResourceLocation.fromNamespaceAndPath(baseId.namespace, "block/${baseId.path}")

            val singleModel = models().withExistingParent("${name}_single", "block/thin_block")
                .texture("particle", texture)
                .texture("texture", texture)
                .element()
                .from(0.0F, 0.0F, 0.0F)
                .to(16.0F, 2.0F, 16.0F)
                .allFaces { _, face -> face.texture("#texture") }
                .end()

            getVariantBuilder(layer).forAllStates { state ->
                val layers = state.getValue(SnowLayerBlock.LAYERS)
                val height = layers * 2.0F

                val model = if (layers == 1) {
                    singleModel
                } else {
                    models().withExistingParent("${name}_layer${layers}", "block/thin_block")
                        .texture("particle", texture)
                        .texture("texture", texture)
                        .element()
                        .from(0.0F, 0.0F, 0.0F)
                        .to(16.0F, height, 16.0F)
                        .allFaces { _, face -> face.texture("#texture") }
                        .end()
                }

                ConfiguredModel.builder().modelFile(model).build()
            }

            simpleBlockItem(layer, singleModel)
        }
    }
}
