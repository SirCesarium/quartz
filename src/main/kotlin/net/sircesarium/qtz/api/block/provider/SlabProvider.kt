package net.sircesarium.qtz.api.block.provider

import net.minecraft.world.item.Item
import net.minecraft.world.level.block.SlabBlock
import net.minecraft.world.level.block.state.BlockBehaviour
import net.sircesarium.qtz.api.block.BlockRegistry
import net.sircesarium.qtz.api.block.BlockWithItem
import net.sircesarium.qtz.api.datagen.DatagenRegistry
import net.neoforged.neoforge.registries.DeferredBlock
import kotlin.reflect.KProperty

internal class SlabProvider(
    registry: BlockRegistry,
    name: String?,
    private val baseBlock: DeferredBlock<*>,
    withItem: Boolean,
    private val enableDatagen: Boolean,
    configure: BlockBehaviour.Properties.() -> Unit,
    itemConfigure: Item.Properties.() -> Unit,
) : BlockProvider<SlabBlock>(
    registry = registry, name = name, factory = ::SlabBlock,
    configure = configure, withItem = withItem,
    itemConfigure = itemConfigure, datagen = false,
) {
    override fun provideDelegate(thisRef: Any?, prop: KProperty<*>): BlockWithItem<SlabBlock> {
        val result = super.provideDelegate(thisRef, prop)

        if (enableDatagen) {
            DatagenRegistry.slabBlocks.add(Triple(registry.modId, result.block.id.path, baseBlock.id.path))
        }

        return result
    }
}
