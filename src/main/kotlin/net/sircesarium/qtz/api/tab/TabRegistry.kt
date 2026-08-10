package net.sircesarium.qtz.api.tab

import net.minecraft.core.registries.Registries
import net.neoforged.bus.api.IEventBus
import net.neoforged.fml.ModList
import net.neoforged.neoforge.registries.DeferredRegister
import net.sircesarium.qtz.api.IRegistry
import kotlin.properties.PropertyDelegateProvider
import kotlin.properties.ReadOnlyProperty

open class TabRegistry(val modId: String) : IRegistry {
    @PublishedApi internal val tabs = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, modId)
    @PublishedApi internal val tabDefs = mutableListOf<TabDef>()

    companion object {
        val instances = mutableListOf<TabRegistry>()
    }

    init {
        instances.add(this)
    }

    override fun register(bus: IEventBus) {
        tabs.register(bus)
        if (ModList.get().isLoaded("fancytabsections")) {
            FTSAdapter.apply(modId, tabDefs)
        }
    }

    fun <T> bindName(factory: (String) -> T) = PropertyDelegateProvider<Any?, ReadOnlyProperty<Any?, T>> { _, property ->
        val instance = factory(property.name)
        ReadOnlyProperty { _, _ -> instance }
    }
}
