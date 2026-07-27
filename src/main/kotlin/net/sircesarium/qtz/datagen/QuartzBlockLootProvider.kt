package net.sircesarium.qtz.datagen

import net.minecraft.core.HolderLookup
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.data.loot.BlockLootSubProvider
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.flag.FeatureFlags
import net.minecraft.world.item.Item
import net.sircesarium.qtz.api.datagen.BlockShape

class QuartzBlockLootProvider(
    registries: HolderLookup.Provider,
    private val modid: String,
    private val blockModels: List<Triple<String, String, BlockShape>>,
    private val slabBlocks: List<Triple<String, String, String>>,
    private val stairBlocks: List<Triple<String, String, String>>,
    private val snowLayerBlocks: List<Triple<String, String, String>>,
) : BlockLootSubProvider(setOf<Item>(), FeatureFlags.VANILLA_SET, registries) {

    override fun generate() {
        for ((_, blockName, _) in blockModels) {
            val block = BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(modid, blockName))

            dropSelf(block)
        }

        for ((_, blockName, _) in slabBlocks) {
            val block = BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(modid, blockName))

            add(block, createSlabItemTable(block))
        }

        for ((_, blockName, _) in stairBlocks) {
            val block = BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(modid, blockName))

            dropSelf(block)
        }

        for ((_, blockName, _) in snowLayerBlocks) {
            val block = BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(modid, blockName))

            dropSelf(block)
        }
    }
}
