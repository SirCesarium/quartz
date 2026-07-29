package net.sircesarium.qtz

import net.neoforged.bus.api.IEventBus
import net.neoforged.fml.common.Mod
import net.sircesarium.qtz.datagen.QuartzDataGatherers


@Mod(Quartz.MODID)
class Quartz(modEventBus: IEventBus) {
    init {
        QuartzDataGatherers.register(modEventBus)
    }

    companion object {
        const val MODID = "qtz"
    }
}
