package net.sircesarium.qtz.api.block

import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredRegister
import net.sircesarium.qtz.api.IRegistry
import kotlin.properties.PropertyDelegateProvider
import kotlin.properties.ReadOnlyProperty

/**
 * Base class for block (and item) registries.
 *
 * Extend and declare blocks using `by block()` / `by slab()` delegates.
 * The property name becomes the registry name (converted to snake_case).
 *
 * ```
 * class ModBlocks : BlockRegistry("modid") {
 *     val myBlock by block()
 *     val stoneSlab by slab(Blocks.STONE)
 * }
 * ```
 *
 * Then register in your mod constructor:
 * ```
 * @Mod("modid")
 * class MyMod(bus: IEventBus) {
 *     init { ModBlocks().register(bus) }
 * }
 * ```
 *
 * @param modId The mod ID used for all registry names.
 */
open class BlockRegistry(modId: String) : IRegistry {
    @PublishedApi internal val blocks = DeferredRegister.createBlocks(modId)
    @PublishedApi internal val items = DeferredRegister.createItems(modId)

    override fun register(bus: IEventBus) {
        blocks.register(bus)
        items.register(bus)
    }

    /**
     * Delegate provider that uses the property name as a key.
     * Called automatically by `by` — do not invoke directly.
     *
     * @param T The delegate value type.
     * @param factory Creates the value from the property name.
     */
    fun <T> bindName(factory: (String) -> T) = PropertyDelegateProvider<Any?, ReadOnlyProperty<Any?, T>> { _, property ->
        val instance = factory(property.name)
        ReadOnlyProperty { _, _ -> instance }
    }
}