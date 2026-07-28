package net.sircesarium.qtz.dev

import net.minecraft.world.level.block.Blocks
import net.sircesarium.qtz.Quartz
import net.sircesarium.qtz.api.block.BlockRegistry
import net.sircesarium.qtz.api.block.type.block
import net.sircesarium.qtz.api.block.type.slab

class ModDevBlocks : BlockRegistry(Quartz.MODID) {
    val testBlock by block()
    val glassSlab by slab(Blocks.GLASS)
}