package net.sircesarium.qtz.datagen

import net.minecraft.data.PackOutput
import net.neoforged.neoforge.client.model.generators.ItemModelProvider
import net.neoforged.neoforge.common.data.ExistingFileHelper
import net.sircesarium.qtz.api.item.IItemPlan

class QuartzItemModelProvider(
    output: PackOutput,
    modId: String,
    helper: ExistingFileHelper,
    private val plans: List<IItemPlan>,
) : ItemModelProvider(output, modId, helper) {
    override fun registerModels() {
        plans.forEach { it.emitItemModel(this) }
    }
}
