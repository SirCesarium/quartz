package net.sircesarium.qtz.client.blockentity

import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.client.event.EntityRenderersEvent
import kotlin.properties.PropertyDelegateProvider
import kotlin.properties.ReadOnlyProperty

/**
 * Registers block entity renderers on the client.
 *
 * Client-only: this class references `net.minecraft.client.*` classes, so it
 * must never be loaded on a dedicated server. Register it from an `@Mod` class
 * annotated with `dist = [Dist.CLIENT]`; the server never loads that class:
 *
 * ```
 * object ModBlockEntitiesClient : ClientBlockEntityRegistrar {
 *     val myMachine by blockEntityRenderer(ModBlockEntities.myMachine, ::MyMachineRenderer)
 * }
 *
 * @Mod(value = "modid", dist = [Dist.CLIENT])
 * class ModClient(modEventBus: IEventBus) {
 *     init {
 *         ModBlockEntitiesClient.register(modEventBus)
 *     }
 * }
 * ```
 *
 * Renderers are declared with
 * [blockEntityRenderer][net.sircesarium.qtz.client.blockentity.type.blockEntityRenderer].
 */
open class ClientBlockEntityRegistrar {
    @PublishedApi internal val registrations = mutableListOf<(EntityRenderersEvent.RegisterRenderers) -> Unit>()

    fun register(bus: IEventBus) {
        bus.addListener(::onRegisterRenderers)
    }

    private fun onRegisterRenderers(event: EntityRenderersEvent.RegisterRenderers) {
        for (registration in registrations) {
            registration(event)
        }
    }

    fun <T> bindName(factory: (String) -> T) = PropertyDelegateProvider<Any?, ReadOnlyProperty<Any?, T>> { _, property ->
        val instance = factory(property.name)
        ReadOnlyProperty { _, _ -> instance }
    }
}
