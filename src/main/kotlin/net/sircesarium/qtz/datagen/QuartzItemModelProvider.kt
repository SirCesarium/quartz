package net.sircesarium.qtz.datagen

import net.minecraft.data.PackOutput
import net.minecraft.resources.ResourceLocation
import net.neoforged.neoforge.client.model.generators.ItemModelProvider
import net.neoforged.neoforge.common.data.ExistingFileHelper

class QuartzItemModelProvider(
    output: PackOutput,
    modid: String,
    efh: ExistingFileHelper,
    private val itemModels: List<Pair<String, String>>,
    private val handheldModels: List<Pair<String, String>>,
) : ItemModelProvider(output, modid, efh) {

    override fun registerModels() {
        for ((_, itemName) in itemModels) {
            basicItem(ResourceLocation.fromNamespaceAndPath(modid, itemName))
        }

        for ((_, itemName) in handheldModels) {
            handheldItem(ResourceLocation.fromNamespaceAndPath(modid, itemName))
        }
    }
}
