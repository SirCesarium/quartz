package net.sircesarium.qtz.datagen

import net.minecraft.data.PackOutput
import net.minecraft.resources.ResourceLocation
import net.neoforged.neoforge.client.model.generators.ItemModelProvider
import net.neoforged.neoforge.common.data.ExistingFileHelper
import net.sircesarium.qtz.api.datagen.DatagenRegistry

class QuartzItemModelProvider(output: PackOutput, modid: String, efh: ExistingFileHelper) :
    ItemModelProvider(output, modid, efh) {

    override fun registerModels() {
        for ((entryModId, itemName) in DatagenRegistry.itemModels) {
            if (entryModId != modid) continue

            basicItem(ResourceLocation.fromNamespaceAndPath(entryModId, itemName))
        }

        for ((entryModId, itemName) in DatagenRegistry.handheldModels) {
            if (entryModId != modid) continue

            handheldItem(ResourceLocation.fromNamespaceAndPath(entryModId, itemName))
        }
    }
}
