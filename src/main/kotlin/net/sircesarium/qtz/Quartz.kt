package net.sircesarium.qtz

import net.neoforged.bus.api.IEventBus
import net.neoforged.fml.common.Mod
import net.sircesarium.qtz.api.block.BlockRegistry
import net.sircesarium.qtz.api.block.type.block

class ModBlocks : BlockRegistry(Quartz.MODID) {
    val testBlock by block()
}

@Mod(Quartz.MODID)
class Quartz(modEventBus: IEventBus) {
    init {
        ModBlocks().register(modEventBus)
    }

    companion object {
        const val MODID = "qtz"
    }
}
