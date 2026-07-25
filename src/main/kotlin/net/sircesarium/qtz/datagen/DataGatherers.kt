package net.sircesarium.qtz.datagen

import net.minecraft.data.PackOutput
import net.neoforged.neoforge.data.event.GatherDataEvent
import net.sircesarium.qtz.api.datagen.DatagenRegistry

object DataGatherers {
    fun gatherData(event: GatherDataEvent) {
        val unscopedOutput = PackOutput(event.generator.packOutput.outputFolder.parent)
        val modIds = DatagenRegistry.itemModels.map { it.first }.toSet() +
                DatagenRegistry.handheldModels.map { it.first }.toSet()
        for (modId in modIds) {
            val provider = QuartzItemModelProvider(unscopedOutput, modId, event.existingFileHelper)
            event.generator.addProvider(true, provider)
        }
    }
}
