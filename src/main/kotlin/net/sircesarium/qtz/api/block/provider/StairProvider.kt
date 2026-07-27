package net.sircesarium.qtz.api.block.provider

import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.StairBlock
import net.minecraft.world.level.block.state.BlockBehaviour
import net.sircesarium.qtz.api.block.BlockRegistry
import net.sircesarium.qtz.api.block.BlockWithItem
import net.sircesarium.qtz.api.datagen.DatagenRegistry
import net.neoforged.neoforge.registries.DeferredBlock
import kotlin.reflect.KProperty

internal class StairProvider(
    registry: BlockRegistry,
    name: String?,
    private val baseBlock: DeferredBlock<*>,
    withItem: Boolean,
    private val enableDatagen: Boolean,
    configure: BlockBehaviour.Properties.() -> Unit,
    itemConfigure: Item.Properties.() -> Unit,
) : BlockProvider<StairBlock>(
    registry = registry, name = name,
    factory = { props -> StairBlock(Blocks.STONE.defaultBlockState(), props) },
    configure = configure, withItem = withItem,
    itemConfigure = itemConfigure, datagen = false,
) {
    override fun provideDelegate(thisRef: Any?, prop: KProperty<*>): BlockWithItem<StairBlock> {
        val result = super.provideDelegate(thisRef, prop)

        if (enableDatagen) {
            DatagenRegistry.stairBlocks.add(Triple(registry.modId, result.block.id.path, baseBlock.id.path))
        }

        return result
    }
}
