package net.sircesarium.qtz.datagen

import net.neoforged.neoforge.data.event.GatherDataEvent
import net.sircesarium.qtz.api.datagen.DatagenRegistry

object DataGatherers {
    fun gatherData(event: GatherDataEvent) {
        val modIds = DatagenRegistry.itemModels.map { it.first }.toSet() +
                DatagenRegistry.handheldModels.map { it.first }.toSet()
        for (modId in modIds) {
            val provider = QuartzItemModelProvider(event.generator.packOutput, modId, event.existingFileHelper)
            event.generator.addProvider(true, provider)
        }
    }
}
