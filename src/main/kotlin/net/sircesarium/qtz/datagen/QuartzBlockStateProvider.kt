package net.sircesarium.qtz.datagen

import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.data.PackOutput
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.block.RotatedPillarBlock
import net.neoforged.neoforge.client.model.generators.BlockStateProvider
import net.neoforged.neoforge.common.data.ExistingFileHelper
import net.sircesarium.qtz.api.datagen.BlockShape
import net.sircesarium.qtz.api.datagen.DatagenRegistry

class QuartzBlockStateProvider(output: PackOutput, private val modid: String, efh: ExistingFileHelper) :
    BlockStateProvider(output, modid, efh) {

    override fun registerStatesAndModels() {
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
}
