package net.sircesarium.qtz.utils

import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.SoundType

val Block.soundType: SoundType
    get() {
        @Suppress("DEPRECATION")
        return defaultBlockState().getSoundType()
    }
