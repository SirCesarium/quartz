package net.sircesarium.qtz.api.block

import net.minecraft.world.item.BlockItem
import net.minecraft.world.level.block.Block
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredBlock
import net.neoforged.neoforge.registries.DeferredItem
import net.neoforged.neoforge.registries.DeferredRegister
import net.sircesarium.qtz.api.datagen.BlockShape
import kotlin.reflect.KProperty

abstract class BlockRegistry(val modId: String) {
    val blocks: DeferredRegister.Blocks = DeferredRegister.createBlocks(modId)
    @PublishedApi internal val blockItems: DeferredRegister.Items = DeferredRegister.createItems(modId)

    internal val blockModels = mutableListOf<Triple<String, String, BlockShape>>()
    internal val slabBlocks = mutableListOf<Triple<String, String, String>>()
    internal val stairBlocks = mutableListOf<Triple<String, String, String>>()
    internal val snowLayerBlocks = mutableListOf<Triple<String, String, String>>()

    companion object {
        internal val instances = mutableListOf<BlockRegistry>()
    }

    init {
        instances.add(this)
    }

    fun register(bus: IEventBus) {
        blocks.register(bus)
        blockItems.register(bus)
    }
}

class BlockWithItem<T : Block>(
    val block: DeferredBlock<T>,
    val item: DeferredItem<BlockItem>,
) {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): BlockWithItem<T> = this
}

class BlockOnly<T : Block>(
    val block: DeferredBlock<T>,
) {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): BlockOnly<T> = this
}
