package net.sircesarium.qtz.datagen

import net.neoforged.neoforge.data.event.GatherDataEvent
import net.sircesarium.qtz.Quartz

object DataGatherers {
    fun gatherData(event: GatherDataEvent) {
        val generator = event.generator
        val packOutput = generator.packOutput
        val efh = event.existingFileHelper
        generator.addProvider(true, QuartzItemModelProvider(packOutput, Quartz.MODID, efh))
    }
}
