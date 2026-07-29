package net.sircesarium.qtz

import net.neoforged.bus.api.IEventBus
import net.neoforged.fml.common.Mod
import net.sircesarium.qtz.datagen.DataGatherers


@Mod(Quartz.MODID)
class Quartz(modEventBus: IEventBus) {
    init {
        modEventBus.addListener(DataGatherers::gatherData)
    }

    companion object {
        const val MODID = "qtz"
    }
}
