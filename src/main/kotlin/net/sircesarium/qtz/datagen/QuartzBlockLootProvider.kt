package net.sircesarium.qtz.datagen

import net.minecraft.core.HolderLookup
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.data.loot.BlockLootSubProvider
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.flag.FeatureFlags
import net.minecraft.world.item.Item
import net.sircesarium.qtz.api.datagen.DatagenRegistry

class QuartzBlockLootProvider(
    registries: HolderLookup.Provider,
    private val modid: String,
) : BlockLootSubProvider(setOf<Item>(), FeatureFlags.VANILLA_SET, registries) {

    override fun generate() {
        for ((entryModId, blockName, _) in DatagenRegistry.blockModels) {
            if (entryModId != modid) continue
            val block = BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(entryModId, blockName))
            dropSelf(block)
        }
    }
}
