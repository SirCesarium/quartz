package net.sircesarium.qtz.api.block.provider

import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockBehaviour
import net.neoforged.neoforge.registries.DeferredBlock
import net.neoforged.neoforge.registries.DeferredItem
import net.sircesarium.qtz.api.block.BlockRegistry
import net.sircesarium.qtz.api.block.BlockWithItem
import net.sircesarium.qtz.api.datagen.BlockShape
import net.sircesarium.qtz.api.datagen.DatagenRegistry
import net.sircesarium.qtz.api.util.toSnakeCase
import kotlin.reflect.KProperty

open class BlockProvider<T : Block>(
    protected val registry: BlockRegistry,
    private val name: String?,
    protected val factory: (BlockBehaviour.Properties) -> T,
    protected val configure: BlockBehaviour.Properties.() -> Unit,
    protected val withItem: Boolean = true,
    protected val itemConfigure: Item.Properties.() -> Unit = {},
    protected val datagen: Boolean = true,
    protected val shape: BlockShape = BlockShape.CubeAll,
    protected val onRegister: ((String, DeferredBlock<T>) -> Unit)? = null,
) {
    open operator fun provideDelegate(thisRef: Any?, prop: KProperty<*>): BlockWithItem<T> {
        val id = name ?: prop.name.toSnakeCase()

        val props = BlockBehaviour.Properties.of()
        configure(props)

        val block = registry.blocks.registerBlock(id, factory, props)

        val item = if (withItem) {
            registry.blockItems.registerSimpleBlockItem(block, Item.Properties().apply(itemConfigure))
        } else null

        if (datagen) {
            DatagenRegistry.blockModels.add(Triple(registry.modId, id, shape))
        }

        onRegister?.invoke(id, block)

        return BlockWithItem(block, item)
    }
}
