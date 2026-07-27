package net.sircesarium.qtz.datagen

import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.data.PackOutput
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.block.RotatedPillarBlock
import net.minecraft.world.level.block.SlabBlock
import net.minecraft.world.level.block.SnowLayerBlock
import net.minecraft.world.level.block.StairBlock
import net.neoforged.neoforge.client.model.generators.BlockModelBuilder
import net.neoforged.neoforge.client.model.generators.BlockStateProvider
import net.neoforged.neoforge.client.model.generators.ConfiguredModel
import net.neoforged.neoforge.client.model.generators.ModelFile
import net.neoforged.neoforge.common.data.ExistingFileHelper
import net.sircesarium.qtz.api.datagen.BlockShape
import net.sircesarium.qtz.api.datagen.DatagenRegistry

class QuartzBlockStateProvider(output: PackOutput, private val modid: String, efh: ExistingFileHelper) :
    BlockStateProvider(output, modid, efh) {

    override fun registerStatesAndModels() {
        registerSimpleModels()
        registerSlabModels()
        registerStairModels()
        registerSnowLayerModels()
    }

    private fun registerSimpleModels() {
        for ((entryModId, blockName, shape) in DatagenRegistry.blockModels) {
            if (entryModId != modid) continue

            val block = BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(entryModId, blockName))

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
        for ((entryModId, blockName, baseTexture) in DatagenRegistry.slabBlocks) {
            if (entryModId != modid) continue

            val slab = BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(entryModId, blockName)) as SlabBlock
            val name = BuiltInRegistries.BLOCK.getKey(slab).path
            val texture = ResourceLocation.fromNamespaceAndPath(modid, "block/$baseTexture")
            val bottomModel = models().slab(name, texture, texture, texture)
            val topModel = models().slabTop("${name}_top", texture, texture, texture)
            val doubleModel = models().cubeAll("${name}_double", texture)

            slabBlock(slab, bottomModel, topModel, doubleModel)
            simpleBlockItem(slab, bottomModel)
        }
    }

    private fun registerStairModels() {
        for ((entryModId, blockName, baseTexture) in DatagenRegistry.stairBlocks) {
            if (entryModId != modid) continue

            val stair = BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(entryModId, blockName)) as StairBlock
            val name = BuiltInRegistries.BLOCK.getKey(stair).path
            val texture = ResourceLocation.fromNamespaceAndPath(modid, "block/$baseTexture")
            val straightModel = models().stairs(name, texture, texture, texture)
            val innerModel = models().stairsInner("${name}_inner", texture, texture, texture)
            val outerModel = models().stairsOuter("${name}_outer", texture, texture, texture)

            stairsBlock(stair, straightModel, innerModel, outerModel)
            simpleBlockItem(stair, straightModel)
        }
    }

    private fun registerSnowLayerModels() {
        for ((entryModId, blockName, baseTexture) in DatagenRegistry.snowLayerBlocks) {
            if (entryModId != modid) continue

            val layer = BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(entryModId, blockName)) as SnowLayerBlock
            val name = BuiltInRegistries.BLOCK.getKey(layer).path
            val texture = ResourceLocation.fromNamespaceAndPath(modid, "block/$baseTexture")

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
