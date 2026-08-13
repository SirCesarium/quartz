package net.sircesarium.qtz.api.blockentity.type

import java.util.function.Supplier
import com.mojang.datafixers.types.Type
import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.neoforged.neoforge.registries.DeferredHolder
import net.sircesarium.qtz.api.blockentity.BlockEntityRegistry
import net.sircesarium.qtz.utils.toSnakeCase

/**
 * Registers a block entity type named after the property (converted to snake_case) and
 * attaches it to the given blocks.
 *
 * | Property | Configuration | Example |
 * |---|---|---|
 * | Property name → registry name | `by blockEntity(...)` → `"my_machine"` |
 * | Entity constructor | `constructor` | `::MyBlockEntity` |
 * | Attached blocks | `vararg blocks` | `ModBlocks.myMachine.block` |
 *
 * ```
 * class ModBlockEntities : BlockEntityRegistry("modid") {
 *     val myMachine by blockEntity(::MyBlockEntity, ModBlocks.myMachine.block)
 * }
 * ```
 *
 * The property resolves to a [DeferredHolder]; call `get()` for the [BlockEntityType].
 */
fun <E : BlockEntity> BlockEntityRegistry.blockEntity(
    constructor: (BlockEntityType<*>, BlockPos, BlockState) -> E,
    vararg blocks: Block,
) = bindName { rawName ->
    val name = rawName.toSnakeCase()

    lateinit var holder: DeferredHolder<BlockEntityType<*>, BlockEntityType<E>>

    holder = blockEntityTypes.register(name, Supplier {
        val noDataType: Type<*>? = null

        BlockEntityType.Builder.of(
            { pos, state -> constructor(holder.get(), pos, state) },
            *blocks,
        ).build(noDataType as Type<*>)
    })

    holder
}