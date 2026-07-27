package net.sircesarium.qtz.api.block

import net.minecraft.world.item.BlockItem
import net.minecraft.world.level.block.Block
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredBlock
import net.neoforged.neoforge.registries.DeferredItem
import net.neoforged.neoforge.registries.DeferredRegister
import kotlin.reflect.KProperty

abstract class BlockRegistry(val modId: String) {
    val blocks: DeferredRegister.Blocks = DeferredRegister.createBlocks(modId)
    @PublishedApi internal val blockItems: DeferredRegister.Items = DeferredRegister.createItems(modId)

    fun register(bus: IEventBus) {
        blocks.register(bus)
        blockItems.register(bus)
    }
}

class BlockWithItem<T : Block>(
    val block: DeferredBlock<T>,
    val item: DeferredItem<BlockItem>?,
) {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): BlockWithItem<T> = this
}
