package net.sircesarium.qtz.datagen

import net.minecraft.data.PackOutput
import net.neoforged.neoforge.client.model.generators.BlockStateProvider
import net.neoforged.neoforge.common.data.ExistingFileHelper
import net.sircesarium.qtz.api.DatagenPlan

class QuartzBlockStateProvider(
    output: PackOutput,
    modId: String,
    helper: ExistingFileHelper,
    private val plans: List<DatagenPlan>,
) : BlockStateProvider(output, modId, helper) {
    override fun registerStatesAndModels() {
        plans.forEach { it.emitBlockState(this) }
    }
}
