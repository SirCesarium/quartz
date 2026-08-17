package net.sircesarium.qtz.client.blockentity.type

import java.util.function.Supplier
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.sircesarium.qtz.client.blockentity.ClientBlockEntityRegistrar

/**
 * Registers a block entity renderer for the given block entity type.
 *
 * | Property | Example |
 * |---|---|
 * | Block entity type | `ModBlockEntities.myMachine` |
 * | Renderer provider | `::MyMachineRenderer` |
 *
 * ```
 * object ModBlockEntitiesClient : ClientBlockEntityRegistrar {
 *     val myMachine by blockEntityRenderer(ModBlockEntities.myMachine, ::MyMachineRenderer)
 * }
 * ```
 *
 * The property resolves to the [BlockEntityType] the renderer is bound to.
 *
 * The renderer is registered on the client with
 * [ClientBlockEntityRegistrar][net.sircesarium.qtz.client.blockentity.ClientBlockEntityRegistrar],
 * using the holder.
 */
fun <E : BlockEntity> ClientBlockEntityRegistrar.blockEntityRenderer(
    type: Supplier<out BlockEntityType<E>>,
    provider: BlockEntityRendererProvider<E>,
) = bindName { _ ->
    registrations.add { event ->
        event.registerBlockEntityRenderer(type.get(), provider)
    }

    type.get()
}
