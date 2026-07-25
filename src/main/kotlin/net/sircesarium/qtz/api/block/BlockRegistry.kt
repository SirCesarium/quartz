package net.sircesarium.qtz.api.block

import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockBehaviour
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredBlock
import net.neoforged.neoforge.registries.DeferredItem
import net.neoforged.neoforge.registries.DeferredRegister
import net.sircesarium.qtz.api.util.toSnakeCase
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

class BlockProvider<T : Block>(
    private val registry: BlockRegistry,
    private val name: String?,
    private val factory: (BlockBehaviour.Properties) -> T,
    private val configure: BlockBehaviour.Properties.() -> Unit,
    private val withItem: Boolean = true,
) {
    operator fun provideDelegate(thisRef: Any?, prop: KProperty<*>): BlockWithItem<T> {
        val id = name ?: prop.name.toSnakeCase()
        val props = BlockBehaviour.Properties.of()
        configure(props)
        val block = registry.blocks.registerBlock(id, factory, props)
        val item = if (withItem) {
            registry.blockItems.registerSimpleBlockItem(block, Item.Properties())
        } else null
        return BlockWithItem(block, item)
    }
}
