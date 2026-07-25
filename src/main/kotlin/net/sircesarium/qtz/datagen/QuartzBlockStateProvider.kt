package net.sircesarium.qtz.datagen

import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.data.PackOutput
import net.minecraft.resources.ResourceLocation
import net.neoforged.neoforge.client.model.generators.BlockStateProvider
import net.neoforged.neoforge.common.data.ExistingFileHelper
import net.sircesarium.qtz.api.datagen.DatagenRegistry

class QuartzBlockStateProvider(output: PackOutput, private val modid: String, efh: ExistingFileHelper) :
    BlockStateProvider(output, modid, efh) {

    override fun registerStatesAndModels() {
        for ((entryModId, blockName) in DatagenRegistry.blockModels) {
            if (entryModId != modid) continue
            val block = BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(entryModId, blockName))
            val model = cubeAll(block)
            simpleBlockWithItem(block, model)
        }
    }
}
