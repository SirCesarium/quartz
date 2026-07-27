package net.sircesarium.qtz.api.block.provider

import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockBehaviour
import net.neoforged.neoforge.registries.DeferredBlock
import net.sircesarium.qtz.api.block.BlockOnly
import net.sircesarium.qtz.api.block.BlockRegistry
import net.sircesarium.qtz.api.datagen.BlockShape
import net.sircesarium.qtz.api.util.toSnakeCase
import kotlin.reflect.KProperty

open class BlockOnlyProvider<T : Block>(
    protected val registry: BlockRegistry,
    private val name: String?,
    protected val factory: (BlockBehaviour.Properties) -> T,
    protected val configure: BlockBehaviour.Properties.() -> Unit,
    protected val datagen: Boolean = true,
    protected val shape: BlockShape = BlockShape.CubeAll,
) {
    open operator fun provideDelegate(thisRef: Any?, prop: KProperty<*>): BlockOnly<T> {
        val id = name ?: prop.name.toSnakeCase()

        val props = BlockBehaviour.Properties.of()
        configure(props)

        val block = registry.blocks.registerBlock(id, factory, props)

        if (datagen) {
            registry.blockModels.add(Triple(registry.modId, id, shape))
        }

        return BlockOnly(block)
    }
}
