package net.sircesarium.qtz

import net.neoforged.bus.api.IEventBus
import net.neoforged.fml.common.Mod
import net.neoforged.fml.loading.FMLLoader
import net.sircesarium.qtz.dev.ModDevBlocks


@Mod(Quartz.MODID)
class Quartz(modEventBus: IEventBus) {
    init {
        if (!FMLLoader.isProduction()) {
            ModDevBlocks().register(modEventBus)
        }
    }

    companion object {
        const val MODID = "qtz"
    }
}
