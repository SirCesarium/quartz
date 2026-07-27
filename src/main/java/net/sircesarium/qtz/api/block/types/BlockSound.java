package net.sircesarium.qtz.api.block.types;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;

final class BlockSound {
    static SoundType get(Block block) {
        return block.getSoundType(block.defaultBlockState(), null, null, null);
    }

    private BlockSound() {}
}
