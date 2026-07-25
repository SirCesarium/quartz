package net.sircesarium.qtz.datagen

import net.minecraft.data.PackOutput
import net.minecraft.resources.ResourceLocation
import net.neoforged.neoforge.client.model.generators.ItemModelProvider
import net.neoforged.neoforge.common.data.ExistingFileHelper
import net.sircesarium.qtz.api.datagen.DatagenRegistry

class QuartzItemModelProvider(output: PackOutput, modid: String, efh: ExistingFileHelper) :
    ItemModelProvider(output, modid, efh) {

    override fun registerModels() {
        for ((modId, itemName) in DatagenRegistry.itemModels) {
            if (modId == this.modid) basicItem(ResourceLocation.fromNamespaceAndPath(modId, itemName))
        }
        for ((modId, itemName) in DatagenRegistry.handheldModels) {
            if (modId == this.modid) handheldItem(ResourceLocation.fromNamespaceAndPath(modId, itemName))
        }
    }
}
