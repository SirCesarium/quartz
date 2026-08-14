package net.sircesarium.qtz.api.sound

import net.minecraft.core.registries.Registries
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredRegister
import net.sircesarium.qtz.api.IRegistry
import kotlin.properties.PropertyDelegateProvider
import kotlin.properties.ReadOnlyProperty

open class SoundRegistry(val modId: String) : IRegistry {
    @PublishedApi internal val sounds =
        DeferredRegister.create(Registries.SOUND_EVENT, modId)
    @PublishedApi internal val soundDefs = mutableListOf<SoundDef>()

    companion object {
        val instances = mutableListOf<SoundRegistry>()
    }

    init {
        instances.add(this)
    }

    override fun register(bus: IEventBus) {
        sounds.register(bus)
    }

    fun <T> bindName(factory: (String) -> T) = PropertyDelegateProvider<Any?, ReadOnlyProperty<Any?, T>> { _, property ->
        val instance = factory(property.name)
        ReadOnlyProperty { _, _ -> instance }
    }
}